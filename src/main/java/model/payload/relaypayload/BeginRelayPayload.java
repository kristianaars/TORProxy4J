package model.payload.relaypayload;

import model.cells.relaycells.BeginRelayCell;
import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BeginRelayPayload extends RelayPayload {

    private final static int FLAG_LENGTH = 4;
    private final static int ZEROS_AT_END_OF_ADDRESS = 1;
    private final static int ADDRESS_STRUCTURE_LENGTH = ZEROS_AT_END_OF_ADDRESS + 1; //1 For the : between port and address

    private BeginRelayPayload(byte[] payload) {
        super(payload);
    }

    public String retrieveAddress() {
        byte b = payload[0];
        StringBuilder s = new StringBuilder();
        while (b != ':') {
            s.append((char) b);
            b = payload[s.length()];
        }

        return s.toString();
    }

    public int retrievePort() {
        int i = 0;
        byte b = payload[i];
        while (b != ':') { b = payload[++i]; }
        return ByteUtils.toInt(new byte[] { payload[++i], payload[++i], payload[++i], payload[++i]});
    }

    /*
     *    To open a new anonymized TCP connection, the OP chooses an open
     *    circuit to an exit that may be able to connect to the destination
     *    address, selects an arbitrary StreamID not yet used on that circuit,
     *    and constructs a RELAY_BEGIN cell with a payload encoding the address
     *    and port of the destination host.  The payload format is:
     *
     *          ADDRPORT [nul-terminated string]
     *          FLAGS    [4 bytes]
     *
     *    ADDRPORT is made of ADDRESS | ':' | PORT | [00]
     *
     *    where  ADDRESS can be a DNS hostname, or an IPv4 address in
     *    dotted-quad format, or an IPv6 address surrounded by square brackets;
     *    and where PORT is a decimal integer between 1 and 65535, inclusive.
     *
     *    The ADDRPORT string SHOULD be sent in lower case, to avoid
     *    fingerprinting.  Implementations MUST accept strings in any case.
     *
     */
    public static BeginRelayPayload generateBeginRelayPayload(String hostname, int port) {
        String ADDRPORT = hostname + ":" + port;
        byte[] flags = generateDefaultFlags();
        int length = ADDRPORT.length() + 1 + FLAG_LENGTH;

        ByteBuffer b = ByteBuffer.allocate(length);
        b.put(ByteUtils.toBytes(ADDRPORT.toLowerCase()));
        b.put((byte) 0x00);
        b.put(flags);

        return new BeginRelayPayload(b.array());
    }

    /*
     *
     *    The FLAGS value has one or more of the following bits set, where
     *    "bit 1" is the LSB of the 32-bit value, and "bit 32" is the MSB.
     *    (Remember that all values in Tor are big-endian (see 0.1.1 above), so
     *    the MSB of a 4-byte value is the MSB of the first byte, and the LSB
     *    of a 4-byte value is the LSB of its last byte.)
     *
     *      bit   meaning
     *       1 -- IPv6 okay.  We support learning about IPv6 addresses and
     *            connecting to IPv6 addresses.
     *       2 -- IPv4 not okay.  We don't want to learn about IPv4 addresses
     *            or connect to them.
     *       3 -- IPv6 preferred.  If there are both IPv4 and IPv6 addresses,
     *            we want to connect to the IPv6 one.  (By default, we connect
     *            to the IPv4 address.)
     *       4..32 -- Reserved. Current clients MUST NOT set these. Servers
     *            MUST ignore them.
     */
    private static byte[] generateDefaultFlags() {
        //All flags shall be sat to zero -> Generate zero-only byte-array
        return new byte[FLAG_LENGTH];
    }
}
