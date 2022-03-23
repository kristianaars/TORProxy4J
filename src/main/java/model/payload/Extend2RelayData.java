package model.payload;

import model.LinkSpecifier;

import java.nio.ByteBuffer;

public class Extend2RelayData extends RelayData {

    public Extend2RelayData(byte[] payload) {
        super(payload);
    }

    public static Extend2RelayData createRelayData(LinkSpecifier[] LSPEC, byte[] handshakeData) {
        int size = 0;
        for(LinkSpecifier l : LSPEC) { size += l.getCellContent().length; }
        size += handshakeData.length;

        ByteBuffer pumpBuffer = ByteBuffer.allocate(size + 1);

        pumpBuffer.put((byte) LSPEC.length); //NSPEC
        for(LinkSpecifier l : LSPEC) { pumpBuffer.put(l.getCellContent()); } //LSPEC
        pumpBuffer.put(handshakeData);

        return new Extend2RelayData(pumpBuffer.array());
    }
}
