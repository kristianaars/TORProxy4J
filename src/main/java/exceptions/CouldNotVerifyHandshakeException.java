package exceptions;

public class CouldNotVerifyHandshakeException extends NTorHandshakeException {

    public CouldNotVerifyHandshakeException(String m) {
        super(m);
    }
}
