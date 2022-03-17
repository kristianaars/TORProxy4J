package model.payload;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AuthenticatePayload extends Payload {

    public static final int TYPE_SIZE = 0x08;
    public static final int CID_SIZE = 0x20;
    public static final int SID_SIZE = 0x20;
    public static final int SLOG_SIZE = 0x20;
    public static final int CLOG_SIZE = 0x20;
    public static final int SCERT_SIZE = 0x20;
    public static final int TLSSECRETS_SIZE = 0x20;
    public static final int RAND_SIZE = 0x18;
    public static final int SIG_SIZE = 0xFF; //Variable size

    public AuthenticatePayload(byte[] payload) {
        super(payload);
    }

    /**
     * Read more: https://gitweb.torproject.org/torspec.git/plain/tor-spec.txt , section 4.4.1
     *
     * @param CID A SHA256 hash of the initiator's RSA1024 identity key [32 octets]
     * @param SID A SHA256 hash of the responder's RSA1024 identity key [32 octets]
     * @param SLOG A SHA256 hash of all bytes sent from the responder to the initiator as part of the negotiation up to and including the AUTH_CHALLENGE cell; that is, the VERSIONS cell, the CERTS cell, the AUTH_CHALLENGE cell, and any padding cells.  [32 octets]
     * @param CLOG A SHA256 hash of all bytes sent from the initiator to the responder as part of the negotiation so far; that is, the VERSIONS cell and the CERTS cell and any padding cells. [32 octets]
     * @param SCERT A SHA256 hash of the responder's TLS link certificate. [32 octets]
     * @param TLS_SECRETS A SHA256 HMAC, using the TLS master secret as the secret key, of the following:
     *            <ul>
     *                    <li>client_random, as sent in the TLS Client Hello</li>
     *                    <li>server_random, as sent in the TLS Server Hello</li>
     *                    <li>the NUL terminated ASCII string: "Tor V3 handshake TLS cross-certification"</li>
     *            </ul>
     *                    [32 octets]
     * @param RAND A 24 byte value, randomly chosen by the initiator. [24 octets]
     */
    public static void generateAuthenticationPayload(
            byte[] CID,
            byte[] SID,
            byte[] SLOG,
            byte[] CLOG,
            byte[] SCERT,
            byte[] TLS_SECRETS,
            byte[] RAND
    ) {
        ByteBuffer initialPayloadBuffer = ByteBuffer.allocate(TYPE_SIZE + CID_SIZE + SID_SIZE + SLOG_SIZE + CLOG_SIZE + SCERT_SIZE + TLSSECRETS_SIZE + RAND_SIZE);
        byte[] TYPE = "AUTH0001".getBytes(StandardCharsets.UTF_8);

        //Create initial buffer
        initialPayloadBuffer.put(TYPE, 0, TYPE_SIZE);
        initialPayloadBuffer.put(CID, 0, CID_SIZE);
        initialPayloadBuffer.put(SID, 0, SID_SIZE);
        initialPayloadBuffer.put(SLOG, 0, SLOG_SIZE);
        initialPayloadBuffer.put(CLOG, 0, CLOG_SIZE);
        initialPayloadBuffer.put(SCERT, 0, SCERT_SIZE);
        initialPayloadBuffer.put(TLS_SECRETS, 0, TLSSECRETS_SIZE);
        initialPayloadBuffer.put(RAND, 0, RAND_SIZE);

        //Create SIG-field (SHA256 Hash w/OAEP-MGF1 padding)

    }
}
