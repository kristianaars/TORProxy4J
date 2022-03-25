package exceptions;

public class PayloadSizeNotFixedException extends TorException {

    public PayloadSizeNotFixedException(int expectedSize) {
        super("Payload must be fixed, with size " + expectedSize);
    }

}
