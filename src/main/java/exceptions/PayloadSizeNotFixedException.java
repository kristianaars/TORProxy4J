package exceptions;

public class PayloadSizeNotFixedException extends Throwable {

    public PayloadSizeNotFixedException(int expectedSize) {
        super("Payload must be fixed, with size " + expectedSize);
    }

}
