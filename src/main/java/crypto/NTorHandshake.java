package crypto;

import exceptions.CouldNotVerifyHandshakeException;
import model.LinkSpecifier;
import model.LinkSpecifierGenerator;
import model.ServerHandshakeResponse;
import model.cells.Create2CellPacket;
import model.cells.relaycells.Extend2RelayCell;
import model.payload.Create2Payload;
import connection.relay.TorRelay;
import model.payload.Payload;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;
import utils.ByteUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

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

    private static final short H_TYPE = 0x0002;
    private static final int HANDSHAKE_SIZE = 84;
    public static final int G_LENGTH = 32;
    public static final int H_LENGTH = 32;
    private static final String PROTOID = "ntor-curve25519-sha256-1";
    private static final String t_mac = PROTOID + ":mac";
    private static final String t_key = PROTOID + ":key_extract";
    private static final String t_verify = PROTOID + ":verify";
    private static final String m_expand = PROTOID + ":key_expand";
    private static final String server = "Server";

    private final Curve25519KeyPair keyPair;
    private final TorRelay onionRouter;

    private SHA256HKDFKeyMaterial keyMaterial;
    private EncryptionService encryptionService;
    private RelayDigest fwDigest;
    private RelayDigest bwDigest;


    private Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    public NTorHandshake(TorRelay onionRouter) {
        this.keyPair = initiateKeyPair();
        this.onionRouter = onionRouter;
    }

    public SHA256HKDFKeyMaterial getKeyMaterial() {
        return keyMaterial;
    }

    private Curve25519KeyPair initiateKeyPair() {
        return cipher.generateKeyPair();
    }

    public Create2CellPacket getClientInitHandshake(int CIRC_ID) {
        Create2Payload payload = new Create2Payload(getOnionSkin());
        return new Create2CellPacket(CIRC_ID, payload);
    }

    public Extend2RelayCell getExtendCell(int CIRC_ID) {
        LinkSpecifier[] linkSpecifiers = new LinkSpecifier[] {
                LinkSpecifierGenerator.createIPv4LinkSpecifier(onionRouter.getAddress().getAddress(), (short) onionRouter.getPort()),
                LinkSpecifierGenerator.createSHA1LinkSpecifier(onionRouter.getDescriptor().IDENTITY_FINGERPRINT),
        };

        byte[] onionSkin = getOnionSkin();

        return new Extend2RelayCell(CIRC_ID, linkSpecifiers, onionSkin);
    }

    /**
     *    A ONION SKIN contains:
     *
     *    HTYPE     (Client Handshake Type)     [2 bytes]
     *    HLEN      (Client Handshake Data Len) [2 bytes]
     *    HDATA     (Client Handshake Data)     [HLEN bytes]
     *
     */
    public byte[] getOnionSkin() {
        byte[] HDATA = getClientHandshakeRequestData();
        short HLEN = (short) HDATA.length;
        ByteBuffer pumpBuffer = ByteBuffer.allocate(HDATA.length + 2 + 2);
        pumpBuffer.putShort(H_TYPE);
        pumpBuffer.putShort(HLEN);
        pumpBuffer.put(HDATA);

        return pumpBuffer.array();
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
     * ID: Fingerprint
     */
    public void provideServerHandshakeResponse(ServerHandshakeResponse handshakeResponse) throws CouldNotVerifyHandshakeException {
        Curve25519 cipher = Curve25519.getInstance(Curve25519.JAVA);

        byte[] x = this.keyPair.getPrivateKey();
        byte[] X = this.keyPair.getPublicKey();
        byte[] Y = handshakeResponse.getServerPK();
        byte[] B = onionRouter.getDescriptor().NTOR_ONION_KEY;
        byte[] ID = onionRouter.getDescriptor().IDENTITY_FINGERPRINT;

        byte[] EXP_Yx = cipher.calculateAgreement(Y, x);
        byte[] EXP_Bx = cipher.calculateAgreement(B, x);

        //Create secret_input
        ByteBuffer pumpBuffer = ByteBuffer.allocate(EXP_Bx.length + EXP_Yx.length + ID.length + B.length + X.length + Y.length + PROTOID.length());
        pumpBuffer.put(EXP_Yx);
        pumpBuffer.put(EXP_Bx);
        pumpBuffer.put(ID);
        pumpBuffer.put(B);
        pumpBuffer.put(X);
        pumpBuffer.put(Y);
        pumpBuffer.put(ByteUtils.toBytes(PROTOID));

        byte[] secretInput = pumpBuffer.array();

        //VERIFY KEY
        try {
            Mac sha256_mac = Mac.getInstance("HmacSHA256");
            sha256_mac.init(new SecretKeySpec(ByteUtils.toBytes(t_verify), "HmacSHA256"));
            sha256_mac.update(secretInput);
            byte[] verify = sha256_mac.doFinal();

            ByteBuffer authPumpBuffer = ByteBuffer.allocate(verify.length + ID.length + B.length + Y.length + X.length + PROTOID.length() + server.length());
            authPumpBuffer.put(verify);
            authPumpBuffer.put(ID);
            authPumpBuffer.put(B);
            authPumpBuffer.put(Y);
            authPumpBuffer.put(X);
            authPumpBuffer.put(ByteUtils.toBytes(PROTOID));
            authPumpBuffer.put(ByteUtils.toBytes(server));
            byte[] auth_input = authPumpBuffer.array();

            sha256_mac.init(new SecretKeySpec(ByteUtils.toBytes(t_mac), "HmacSHA256"));
            sha256_mac.update(auth_input);
            byte[] H_auth_input = sha256_mac.doFinal();
            byte[] AUTH = handshakeResponse.getAuth();

            boolean verified = Arrays.equals(H_auth_input, AUTH);

            if(!verified) {
                throw new CouldNotVerifyHandshakeException("Handshake data is corrupt or invalid.");
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        SHA256HKDF keyDerivator = new SHA256HKDF(secretInput, ByteUtils.toBytes(t_key), ByteUtils.toBytes(m_expand));
        keyMaterial = keyDerivator.hkdfExpand();

        initiateEncryptionService();
        initiateRelayDigest();
    }

    private void initiateEncryptionService() {
        encryptionService = new EncryptionService(keyMaterial.getKF(), keyMaterial.getKB());
    }

    private void initiateRelayDigest() {
        fwDigest = new RelayDigest(keyMaterial.getDF());
        bwDigest = new RelayDigest(keyMaterial.getDB());
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
    private byte[] getClientHandshakeRequestData() {
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

    /**
     * Encrypts the provided relay payload
     * @return Encrypted payload
     */
    public Payload encryptPayload(Payload payload) {
        byte[] encryptedPayload = encryptionService.encrypt(payload.getPayload());
        Payload encryptedPayloadObject = new Payload(encryptedPayload);
        encryptedPayloadObject.setFixedSize(true);

        return encryptedPayloadObject;
    }

    /**
     * Decrypts the provided relay payload
     * @return Decrypted payload-object
     */
    public Payload decryptPayload(Payload payload) {
        byte[] decryptedPayload = encryptionService.decrypt(payload.getPayload());
        Payload decryptedPayloadObject = new Payload(decryptedPayload);
        decryptedPayloadObject.setFixedSize(true);
        return decryptedPayloadObject;
    }

    public RelayDigest getFwDigest() {
        return fwDigest;
    }

    public RelayDigest getBwDigest() {
        return bwDigest;
    }

    @Override
    public String toString() {
        return "NTorHandshake{" +
                "keyMaterial=" + keyMaterial +
                ", encryptionService=" + encryptionService +
                '}';
    }
}
