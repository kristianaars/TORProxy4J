package model.payload;

import model.LinkSpecifier;
import utils.ByteUtils;

import java.nio.ByteBuffer;

public class Extend2RelayPayload extends RelayPayload {

    public Extend2RelayPayload(byte[] payload) {
        super(payload);
    }

    /*
       NSPEC      (Number of link specifiers)     [1 byte]
         NSPEC times:
           LSTYPE (Link specifier type)           [1 byte]
           LSLEN  (Link specifier length)         [1 byte]
           LSPEC  (Link specifier)                [LSLEN bytes]
       HDATA      (handshake data)                [HDATA_LEN]
     */
    public static Extend2RelayPayload createRelayData(LinkSpecifier[] LSPEC, byte[] handshakeData) {
        int length = 1;
        for(LinkSpecifier l : LSPEC) { length += l.getCellContent().length; }
        length += handshakeData.length;

        ByteBuffer pumpBuffer = ByteBuffer.allocate(length);

        pumpBuffer.put((byte) LSPEC.length); //NSPEC
        for(LinkSpecifier l : LSPEC) { pumpBuffer.put(l.getCellContent()); } //LSPEC
        pumpBuffer.put(handshakeData);

        return new Extend2RelayPayload(pumpBuffer.array());
    }
}
