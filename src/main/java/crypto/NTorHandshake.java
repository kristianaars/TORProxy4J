package crypto;

import model.cell.Create2CellPacket;
import model.cell.Created2CellPacket;
import model.payload.Create2Payload;
import model.relay.TorRelay;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;

import java.nio.ByteBuffer;

public class NTorHandshake {

    private static final int HANDSHAKE_SIZE = 84;

    private final Curve25519KeyPair keyPair;
    private byte[] handshakeData;
    private TorRelay onionRouter;

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

    public void provideServerHandshakeResponse(Created2CellPacket packet) {

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
