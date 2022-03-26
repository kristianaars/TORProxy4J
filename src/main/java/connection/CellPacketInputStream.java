package connection;

import exceptions.PayloadSizeNotFixedException;
import model.cells.*;
import model.cells.relaycells.RelayCell;
import model.cells.relaycells.RelayEarlyCell;
import model.payload.Payload;
import utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CellPacketInputStream {

    private final Logger logger = Logger.getLogger("CellPacketInputStream");

    private InputStream inputStream;
    private short TOR_PROTOCOL_VERSION = ConnectionConstants.TOR_PROTOCOL_VERSION_3;

    public CellPacketInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setTorProtocolVersion(short version) {
        this.TOR_PROTOCOL_VERSION = version;
    }

    /**
     * Gets the next packet in queue. If return-value is null, the socket is closed.
     *
     * @return Data from inputstream in CellPacket format
     */
    public CellPacket getPacket() throws IOException {
        byte[] CIRC_ID_v1 = new byte[] { (byte) inputStream.read(), (byte) inputStream.read() };

        int CIRC_ID = ByteUtils.toShort(CIRC_ID_v1[0], CIRC_ID_v1[1]);
        if(TOR_PROTOCOL_VERSION == ConnectionConstants.TOR_PROTOCOL_VERSION_4) {
            //Tor version of 4 or above uses 4 bytes to represent CIRC_ID
            CIRC_ID = ByteUtils.toInt(new byte[]{CIRC_ID_v1[0], CIRC_ID_v1[1], (byte) inputStream.read(), (byte) inputStream.read()});
        }

        byte command = (byte) inputStream.read();
        if(command == -1) {
            logger.log(Level.WARNING, "Inputstream was unexpectedly closed.");
            //Connection is closed
            return null;
        }

        //logger.info("Retrieving payload of cell with command " + command);

        //Find length of packet
        short length;
        boolean expectFixedPayload = CellPacket.isFixedPacketCell(command);
        if(expectFixedPayload) length = Payload.FIXED_PAYLOAD_SIZE;
        else length = ByteUtils.toShort((byte) inputStream.read(), (byte) inputStream.read());

        //Read payload
        byte[] payload = new byte[ByteUtils.toUnsigned(length)];
        for(int i = 0; i < payload.length; i++) { payload[i] = (byte) inputStream.read(); }

        CellPacket packet;

        switch (command) {

            case CellPacket.VERSION_COMMAND:
                packet = new VersionCellPacket(CIRC_ID, payload);
                break;
            case CellPacket.CERTS_COMMAND:
                packet = new CertCellPacket(CIRC_ID, payload);
                break;
            case CellPacket.AUTH_CHALLENGE_COMMAND:
                packet = new AuthChallengeCellPacket(CIRC_ID, payload);
                break;
            case CellPacket.CREATED2_COMMAND:
                packet = new Created2CellPacket(CIRC_ID, payload);
                break;
            case CellPacket.RELAY_COMMAND:
                packet = new RelayCell(CIRC_ID, command, payload);
                break;
            case CellPacket.RELAY_EARLY_COMMAND:
                packet = new RelayEarlyCell(CIRC_ID, payload);
                break;
            case CellPacket.DESTROY_COMMAND:
                packet = new DestroyCellPacket(CIRC_ID, payload);
                logger.info("Received Destroy Command. Closing connection... Reason: " + ((DestroyCellPacket)packet).getDESTROY_REASON());
                inputStream.close();
                break;
            case CellPacket.NETINFO_COMMAND:
                try {
                    packet = new NetInfoCellPacket(CIRC_ID, payload);
                    break;
                } catch (PayloadSizeNotFixedException e) {
                    e.printStackTrace();
                    return null;
                }

            default:
                packet = new CellPacket(CIRC_ID, command, payload);
        }

        logger.log(Level.INFO, "Received: " + packet);
        return packet;
    }
}
