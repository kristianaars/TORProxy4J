package connection;

import model.cell.CellPacket;
import utils.ByteUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CellPacketOutputStream {

    private final Logger logger = Logger.getLogger("CellPacketOutputStream");
    private OutputStream outputStream;

    private short TOR_PROTOCOL_VERSION = ConnectionConstants.TOR_PROTOCOL_VERSION_3;

    public CellPacketOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(CellPacket packet) throws IOException {
        logger.log(Level.INFO, "Sending " + packet);

        byte[] buffer = generateCellPacketBuffer(packet);
        logger.log(Level.INFO, "Sending buffer " + ByteUtils.toHexString(buffer));
        outputStream.write(buffer);
        outputStream.flush();
    }

    private byte[] generateCellPacketBuffer(CellPacket packet) {
        boolean fixedSize = packet.getPayload().isFixedSize();
        int packetSize = getExpectedPacketSize(packet);

        ByteBuffer pumpBuffer = ByteBuffer.allocate(packetSize);

        if(TOR_PROTOCOL_VERSION >= ConnectionConstants.TOR_PROTOCOL_VERSION_4) {
            pumpBuffer.putInt(packet.getCIRC_ID());
        } else {
            pumpBuffer.putShort((short) packet.getCIRC_ID());
        }

        pumpBuffer.put(packet.getCOMMAND());

        if(!fixedSize) {
            pumpBuffer.putShort((short) (packet.getPayload().getLength() & 0xFFFF));
        }

        pumpBuffer.put(packet.getPayload().getPayload());
        return pumpBuffer.array();
    }

    private int getExpectedPacketSize(CellPacket packet) {
        boolean fixedSize = packet.getPayload().isFixedSize();

        if(fixedSize) {
            return CellPacket.FIXED_CELL_PACKET_SIZE;
        } else {
            if(TOR_PROTOCOL_VERSION >= ConnectionConstants.TOR_PROTOCOL_VERSION_4) {
                return CellPacket.HEADER_SIZE_V4 + packet.getPayload().getLength();
            } else {
                return CellPacket.HEADER_SIZE_V3 + packet.getPayload().getLength();
            }

        }
    }

    public void setTorProtocolVersion(short version) {
        this.TOR_PROTOCOL_VERSION = version;
    }
}
