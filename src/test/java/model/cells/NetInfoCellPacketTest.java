package model.cells;

import exceptions.PayloadSizeNotFixedException;

import model.NetInfo;
import model.NetInfoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class NetInfoCellPacketTest {

    @Test
    public void testCreateFromPayload() throws PayloadSizeNotFixedException {
        byte[] dummyPayload = new byte[509];
        byte[] data = new byte[]{
                (byte) 0x62,  (byte) 0x40,  (byte) 0x80,  (byte) 0x3F,  (byte) 0x04,  (byte) 0x04,  (byte) 0x3E,
                (byte) 0xD2,  (byte) 0x89,  (byte) 0xD4,  (byte) 0x01, (byte) 0x04,  (byte) 0x04,  (byte) 0xBC,
                (byte) 0x71,  (byte) 0x44,  (byte) 0x80
        };

        for(int i = 0; i < data.length; i++) { dummyPayload[i] = data[i]; }

        NetInfoCellPacket packet = new NetInfoCellPacket(1000, dummyPayload);
        int expTime = 1648394303;
        NetInfoAddress expOtherNetAddr = new NetInfoAddress((byte) 4, new byte[]{(byte) 0x3E, (byte) 0xD2, (byte) 0x89, (byte) 0xD4} );
        NetInfoAddress[] expMyAddr = new NetInfoAddress[]{new NetInfoAddress((byte) 4, new byte[] {(byte) 0xBC, 0x71, 0x44, (byte) 0x80})};
        NetInfo expectedNetInfo = new NetInfo(expTime, expOtherNetAddr, expMyAddr);

        assertEquals(expectedNetInfo, packet.getNetInfo());
    }

    @Test
    public void testCreateFromWrongPayloadSize() {
        assertThrows(PayloadSizeNotFixedException.class, () -> {
            new NetInfoCellPacket(1000, new byte[] {
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
            });
        }) ;
    }

}
