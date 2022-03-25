package exceptions;

public class UnexpectedDestroyException extends UnexpectedCellPacketTypeException {

    public UnexpectedDestroyException(String s) {
        super(s);
    }
}
