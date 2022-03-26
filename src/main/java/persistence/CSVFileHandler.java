package persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract class to manage both TaskFileHandler.java and CategoryFileHandler.java
 */
public abstract class CSVFileHandler<T> {

    private final static String DELIMITER = ";";
    private final File FILE;

    /**
     * Creates an instance of CSVFileHandler. Any file extension is allowed.
     * File at path will be created if it does not exist.
     *
     * @param path Full path to file on disk
     * @throws IOException If there is trouble creating the file
     */
    public CSVFileHandler(String path) throws IOException {
        this.FILE = new File(path);

        //Creates file if it does not exist
        if(!FILE.exists()) {
            boolean fileCreated = FILE.createNewFile();
            if(!fileCreated) {
                throw new IOException("Unable to create file " + FILE);
            }
        }

    }

    /**
     * Reads data from {@link #FILE}, splits the data into lines. And splits each line into values with {@link CSVFileHandler#DELIMITER}.
     *
     * @return List containing lines from CSV file. And one line is represented a list of String values.
     * @throws IOException Problems reading from the file.
     */
    protected ArrayList<ArrayList<String>> getRawFileDataFromFile() throws IOException {
        ArrayList<ArrayList<String>> fileData = new ArrayList<>();
        String delimiter = DELIMITER;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                //Splits each line by the given delimiter into an array of Strings
                ArrayList<String> values = new ArrayList<>(Arrays.asList(line.split("\\"+delimiter)));
                fileData.add(values);
            }
        }

        return fileData;
    }


    /**
     * Writes data to {@link #FILE}, splits the data into lines. And splits each line into values where a delimiter from {@link FileConstants#CSV_DELIMITER_STRING} is found.
     *
     * @return List containing lines from CSV file. And one line is represented a list of String values.
     * @throws IOException Problems reading from the file.
     */

    /**
     * Writes data to {@link #FILE}. Each line is represented with an ArrayList with another ArrayList inside itself.
     * The ArrayList inside is representation of values. Each line will be written with it's corresponding values, with a {@link CSVFileHandler#DELIMITER} between each value.
     *
     * @param fileData List containing lines, with each line containing a list of values to be written to file.
     * @throws IOException Error reading to file
     * @throws IllegalArgumentException If a value contains {@link CSVFileHandler#DELIMITER}
     */
    protected void saveRawFileDataToFile(ArrayList<ArrayList<String>> fileData) throws IOException, IllegalArgumentException {
        String delimiter = DELIMITER;

        boolean dataEmpty = fileData.isEmpty();

        //Verify data before opening file.
        for(ArrayList<String> lineData : fileData) {
            for(String data : lineData) {
                if(data.contains(delimiter)) {
                    throw new IllegalArgumentException(delimiter + " is used as delimiter in csv-file, and is not allowed in file data.");
                }
            }
        }

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(FILE)))){

            if(dataEmpty) {
                printWriter.write("");
                return;
            }

            for(ArrayList<String> lineData : fileData) {
                String line = "";

                for(String data : lineData) {
                    if(data.contains(delimiter)) {
                        throw new IllegalArgumentException(delimiter + " is used as delimiter in csv-file, and is not allowed in file data.");
                    }

                    line += (data + delimiter);
                }

                printWriter.println(line);
            }

        }
    }

    public String getFilePath() {
        return FILE.getAbsolutePath();
    }

    /**
     *  Abstract method for entering a list of objects to be saved.
     *  The child-method shall then sort the needed values in a nested ArrayList and send it to {@link #saveRawFileDataToFile(ArrayList)}
     *
     */
    public abstract void saveToFile(T[] objectList) throws IOException;
    public abstract T[] getObjects() throws IOException;

    @Override
    public String toString() {
        return "CSVFileHandler{" +
                "FILE=" + FILE +
                '}';
    }
}
