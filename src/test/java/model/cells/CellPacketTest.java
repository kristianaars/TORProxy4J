package model.cells;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CellPacketTest  {

    private static CellPacket packet;

    private final static int TEST_CIRC_ID = 0x80005000;
    private final static byte TEST_COMMAND = CellPacket.DESTROY_COMMAND;
    private final static byte[] TEST_PAYLOAD = new byte[] {0x40, (byte) 0xFF, 0x43, (byte) 0xBC, 0x12, 0x40, (byte) 0xFF, 0x43, (byte) 0xBC, 0x12, 0x40, (byte) 0xFF, 0x43, (byte) 0xBC, 0x12};

    @BeforeAll
    static void initializeTest() {
        packet = new CellPacket(TEST_CIRC_ID, TEST_COMMAND, TEST_PAYLOAD);
    }

    @Test
    public void testGetCIRC_ID() {
        assertEquals(TEST_CIRC_ID, packet.getCIRC_ID());
    }

    @Test
    public void testGetCOMMAND() {
        assertEquals(TEST_COMMAND, packet.getCOMMAND());
    }

    @Test
    public void testGetPayload() {
        assertEquals(TEST_PAYLOAD, packet.getPayload().getPayload());
    }

    @AfterAll
    static void destroyTest() {
        packet = null;
    }

}
