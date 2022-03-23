package connection;

public class ConnectionConstants {

    public static final short TOR_PROTOCOL_VERSION_3 = (short) 0x0003;
    public static final short TOR_PROTOCOL_VERSION_4 = (short) 0x0004;

    public static final short[] SUPPORTED_PROTOCOL_VERSIONS = new short[]{TOR_PROTOCOL_VERSION_3, TOR_PROTOCOL_VERSION_4};

}
