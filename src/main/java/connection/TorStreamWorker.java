package connection;

import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class TorStreamWorker {

        private ReentrantLock availableJobsLock;
        private Condition availableJobs;
        private PriorityQueue<Job> availableJobsList;

        private ReentrantLock activeJobsLock;
        private ArrayBlockingQueue<Thread> activeJobs;
        private Condition activeJobsQueueFull;

        private boolean isRunning = false;

        public TorStreamWorker(int numberOfJobs) {
            this.activeJobs =  new ArrayBlockingQueue<>(numberOfJobs);
            this.activeJobsLock = new ReentrantLock();
            this.activeJobsQueueFull = activeJobsLock.newCondition();

            this.availableJobsList = new PriorityQueue<>();
            this.availableJobsLock = new ReentrantLock();
            this.availableJobs = availableJobsLock.newCondition();
        }

        public void start() {
            isRunning = true;
            consumer().start();
        }

        /**
         * Returns consumer-thread, which is responsible for adding jobs to active jobs from available jobs list at the correct time.
         * @return Consumer-thread
         */
        private Thread consumer() {
            return new Thread(() -> {

                while(isRunning) {
                    availableJobsLock.lock();

                    while (availableJobsList.isEmpty() || availableJobsList.peek().startTime > System.currentTimeMillis()) {
                        try {
                            if(!availableJobsList.isEmpty()) {
                                long waitTime = availableJobsList.peek().startTime - System.currentTimeMillis();
                                // Waiting for job-start time
                                availableJobs.await(waitTime, TimeUnit.MILLISECONDS);
                            } else {
                                // Waiting for new jobs to arrive in availableJobsList
                                availableJobs.await();
                            }

                            if(!isRunning) {
                                //Consumer-thread will now exit to respect join()
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Adding new job to active jobs-list.
                    addJobToQueue(availableJobsList.poll().runnable);
                    availableJobs.signalAll();
                }
            });
        }

        /**
         * Adds job to active jobs list, if there is space left.
         * @param job
         */
        private void addJobToQueue(Runnable job) {
            // Get lock for job
            activeJobsLock.lock();

            Thread jobThread = new Thread(job);
            while(!activeJobs.offer(jobThread)) {
                try {
                    activeJobsQueueFull.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Creates separate thread to follow the thread state, and remove it from active jobs once completed.
            Thread waitThread = new Thread(() -> {
                jobThread.start();

                try {
                    jobThread.join();

                    activeJobsLock.lock();
                    activeJobs.remove(jobThread);
                    activeJobsQueueFull.signalAll();
                    activeJobsLock.unlock();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            waitThread.start();

            activeJobsLock.unlock();
        }

        /**
         * Adds runnable code to job-queue
         * @param code Code to run
         * @param waitTime Time in milliseconds to wait for job to start.
         */
        public void post(Runnable code, long waitTime) {
            Job job = new Job(code, System.currentTimeMillis() + waitTime);

            availableJobsLock.lock();
            availableJobsList.add(job);
            availableJobs.signalAll();
            availableJobsLock.unlock();
        }

        public void post(Runnable job) {
            post(job, 0);
        }

        public void join() {
            availableJobsLock.lock();

            while (!availableJobsList.isEmpty()) {
                try {
                    availableJobs.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            isRunning = false;
            availableJobs.signalAll();
            availableJobsLock.unlock();

            activeJobsLock.lock();

            while(!activeJobs.isEmpty()) {
                try {
                    activeJobsQueueFull.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return;
        }

    }

    /**
     * Data structure for jobs with a start time.
     */
    class Job implements Comparable {

        Runnable runnable;
        long startTime;

        public Job(Runnable runnable, long startTime) {
            this.runnable = runnable;
            this.startTime = startTime;
        }

        @Override
        public int compareTo(Object o) {
            Job j = (Job) o;
            return Long.compare(this.startTime, j.startTime);
        }
    }
