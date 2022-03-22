package crypto;

import model.cell.Create2CellPacket;
import model.cell.Created2CellPacket;
import model.payload.Create2Payload;
import model.relay.TorRelay;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;
import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NTorHandshake {


    /**
     *    In this section, define:
     *
     *       H(x,t) as HMAC_SHA256 with message x and key t.
     *       H_LENGTH  = 32.
     *       ID_LENGTH = 20.
     *       G_LENGTH  = 32
     *       PROTOID   = "ntor-curve25519-sha256-1"
     *       t_mac     = PROTOID | ":mac"
     *       t_key     = PROTOID | ":key_extract"
     *       t_verify  = PROTOID | ":verify"
     *       MULT(a,b) = the multiplication of the curve25519 point 'a' by the
     *                   scalar 'b'.
     *       G         = The preferred base point for curve25519 ([9])
     *       KEYGEN()  = The curve25519 key generation algorithm, returning
     *                   a private/public keypair.
     *       m_expand  = PROTOID | ":key_expand"
     *       KEYID(A)  = A
     *
     *    To perform the handshake, the client needs to know an identity key
     *    digest for the server, and an ntor onion key (a curve25519 public
     *    key) for that server. Call the ntor onion key "B".  The client
     *    generates a temporary keypair:
     *
     *        x,X = KEYGEN()
     */

    private static final int HANDSHAKE_SIZE = 84;
    public static final int G_LENGTH = 32;
    public static final int H_LENGTH = 32;
    private static final String PROTOID = "ntor-curve25519-sha256-1";
    private static final String t_mac = PROTOID + ":mac";
    private static final String t_key = PROTOID + ":key_extract";
    private static final String t_verify = PROTOID + ":verify";
    private static final String m_expand = PROTOID + ":key_expand";


    private final Curve25519KeyPair keyPair;
    private byte[] handshakeData;
    private TorRelay onionRouter;

    private SHA256HKDF keyDerivator;
    private SHA256HKDFKeyMaterial keyMaterial;

    public NTorHandshake(TorRelay onionRouter) {
        this.handshakeData = new byte[HANDSHAKE_SIZE];
        this.keyPair = initiateKeyPair();
        this.onionRouter = onionRouter;
    }

    private Curve25519KeyPair initiateKeyPair() {
        Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);
        return cipher.generateKeyPair();
    }

    public Create2CellPacket getClientInitHandshake(int CIRC_ID) {
        byte[] handshakeData = createClientHandshakeRequest();
        Create2Payload payload = Create2Payload.generateCreate2Payload(Create2Payload.HTYPE_NTOR, handshakeData);

        return new Create2CellPacket(CIRC_ID, payload);
    }

    /**
     *    The client then checks Y is in G^* [see NOTE below], and computes
     *
     *      secret_input = EXP(Y,x) | EXP(B,x) | ID | B | X | Y | PROTOID
     *      KEY_SEED = H(secret_input, t_key)
     *      verify = H(secret_input, t_verify)
     *      auth_input = verify | ID | B | Y | X | PROTOID | "Server"
     *
     *    The client verifies that AUTH == H(auth_input, t_mac).
     *
     * Where
     *
     * EXP(a, b): Shared key between a and b
     *
     * x: Client private key
     * X: Client Public Key
     * Y: Server public key
     * B: NTor Onion Key
     * ID: Onion Fingerprint
     */
    public void provideServerHandshakeResponse(Created2CellPacket packet) {
        Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

        byte[] x = this.keyPair.getPrivateKey();
        byte[] X = this.keyPair.getPublicKey();
        byte[] Y = packet.getServerPK();
        byte[] B = onionRouter.getDescriptor().NTOR_ONION_KEY;
        byte[] ID = onionRouter.getDescriptor().IDENTITY_FINGERPRINT;

        byte[] EXP_Yx = cipher.calculateAgreement(Y, x);
        byte[] EXP_Bx = cipher.calculateAgreement(B, x);

        //Create secret_input
        ByteBuffer pumpBuffer = ByteBuffer.allocate(EXP_Bx.length + EXP_Yx.length + ID.length + B.length + X.length + PROTOID.length());
        pumpBuffer.put(EXP_Yx);
        pumpBuffer.put(EXP_Bx);
        pumpBuffer.put(ID);
        pumpBuffer.put(B);
        pumpBuffer.put(X);
        pumpBuffer.put(ByteUtils.toBytes(PROTOID));

        byte[] secretInput = pumpBuffer.array();
        System.out.println(ByteUtils.toHexString(secretInput));

        keyDerivator = new SHA256HKDF(secretInput, ByteUtils.toBytes(t_key), ByteUtils.toBytes(m_expand));
        keyMaterial = keyDerivator.hkdfExpand();

        System.out.println(keyMaterial);
    }

    /**
     *    To perform the handshake, the client needs to know an identity key
     *    digest for the server, and an ntor onion key (a curve25519 public
     *    key) for that server. Call the ntor onion key "B".  The client
     *    generates a temporary keypair:
     *
     *        x,X = KEYGEN()
     *
     *    and generates a client-side handshake with contents:
     *
     *        NODEID      Server identity digest  [ID_LENGTH bytes]
     *        KEYID       KEYID(B)                [H_LENGTH bytes]
     *        CLIENT_PK   X                       [G_LENGTH bytes]
     *
     * @return NTAP Onion Skin
     */
    private byte[] createClientHandshakeRequest() {
        byte[] NODE_ID = onionRouter.getDescriptor().IDENTITY_FINGERPRINT;
        byte[] KEY_ID = onionRouter.getDescriptor().NTOR_ONION_KEY;
        byte[] CLIENT_PK = keyPair.getPublicKey();

        final int bufferSize = NODE_ID.length + KEY_ID.length + CLIENT_PK.length;

        ByteBuffer pumpBuffer = ByteBuffer.allocate(bufferSize);
        pumpBuffer.put(NODE_ID);
        pumpBuffer.put(KEY_ID);
        pumpBuffer.put(CLIENT_PK);

        return pumpBuffer.array();
    }
}
