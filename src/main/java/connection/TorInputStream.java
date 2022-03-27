package connection;

import utils.BlockingBuffer;
import utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class TorInputStream extends InputStream {

    private final static int END_OF_STREAM_VALUE = -1;
    private final BlockingBuffer<Integer> buffer;
    private boolean isOpen = true;

    public TorInputStream() {
        this.buffer = new BlockingBuffer<>();
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an {@code int} in the range {@code 0} to
     * {@code 255}. If no byte is available because the end of the stream
     * has been reached, the value {@code -1} is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or {@code -1} if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if(!isOpen) {
            return END_OF_STREAM_VALUE;
        }

        int v = buffer.poll();
        if(v == END_OF_STREAM_VALUE) {
            isOpen = false;
        }

        return v;
    }

    /**
     * Reads up to {@code len} bytes of data from the input stream into
     * an array of bytes.  An attempt is made to read as many as
     * {@code len} bytes, but a smaller number may be read.
     * The number of bytes actually read is returned as an integer.
     *
     * <p> This method blocks until input data is available, end of file is
     * detected, or an exception is thrown.
     *
     * <p> If {@code len} is zero, then no bytes are read and
     * {@code 0} is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of
     * file, the value {@code -1} is returned; otherwise, at least one
     * byte is read and stored into {@code b}.
     *
     * <p> The first byte read is stored into element {@code b[off]}, the
     * next one into {@code b[off+1]}, and so on. The number of bytes read
     * is, at most, equal to {@code len}. Let <i>k</i> be the number of
     * bytes actually read; these bytes will be stored in elements
     * {@code b[off]} through {@code b[off+}<i>k</i>{@code -1]},
     * leaving elements {@code b[off+}<i>k</i>{@code ]} through
     * {@code b[off+len-1]} unaffected.
     *
     * <p> In every case, elements {@code b[0]} through
     * {@code b[off-1]} and elements {@code b[off+len]} through
     * {@code b[b.length-1]} are unaffected.
     *
     * <p> The {@code read(b, off, len)} method
     * for class {@code InputStream} simply calls the method
     * {@code read()} repeatedly. If the first such call results in an
     * {@code IOException}, that exception is returned from the call to
     * the {@code read(b,} {@code off,} {@code len)} method.  If
     * any subsequent call to {@code read()} results in a
     * {@code IOException}, the exception is caught and treated as if it
     * were end of file; the bytes read up to that point are stored into
     * {@code b} and the number of bytes read before the exception
     * occurred is returned. The default implementation of this method blocks
     * until the requested amount of input data {@code len} has been read,
     * end of file is detected, or an exception is thrown. Subclasses are
     * encouraged to provide a more efficient implementation of this method.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset in array {@code b}
     *            at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     * {@code -1} if there is no more data because the end of
     * the stream has been reached.
     * @throws IOException               If the first byte cannot be read for any reason
     *                                   other than end of file, or if the input stream has been closed,
     *                                   or if some other I/O error occurs.
     * @throws NullPointerException      If {@code b} is {@code null}.
     * @throws IndexOutOfBoundsException If {@code off} is negative,
     *                                   {@code len} is negative, or {@code len} is greater than
     *                                   {@code b.length - off}
     * @see InputStream#read()
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, 1);
    }

    protected void post(byte[] data) {
        for (byte b : data) {
            buffer.insert(ByteUtils.toUnsigned(b));
        }
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking, which may be 0, or 0 when
     * end of stream is detected.  The read might be on the same thread or
     * another thread.  A single read or skip of this many bytes will not block,
     * but may read or skip fewer bytes.
     *
     * <p> Note that while some implementations of {@code InputStream} will
     * return the total number of bytes in the stream, many will not.  It is
     * never correct to use the return value of this method to allocate
     * a buffer intended to hold all data in this stream.
     *
     * <p> A subclass's implementation of this method may choose to throw an
     * {@link IOException} if this input stream has been closed by invoking the
     * {@link #close()} method.
     *
     * <p> The {@code available} method of {@code InputStream} always returns
     * {@code 0}.
     *
     * <p> This method should be overridden by subclasses.
     *
     * @return an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking or
     * {@code 0} when it reaches the end of the input stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        return buffer.getSize();
    }

    @Override
    public void close() {
        this.buffer.insert(END_OF_STREAM_VALUE);
    }

    /**
     * Tests if this input stream supports the {@code mark} and
     * {@code reset} methods. Whether or not {@code mark} and
     * {@code reset} are supported is an invariant property of a
     * particular input stream instance. The {@code markSupported} method
     * of {@code InputStream} returns {@code false}.
     *
     * @return {@code true} if this stream instance supports the mark
     * and reset methods; {@code false} otherwise.
     * @see InputStream#mark(int)
     * @see InputStream#reset()
     */
    @Override
    public boolean markSupported() {
        return false;
    }
}
