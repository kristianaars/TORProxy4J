package factory;

public class StreamIDFactory extends IDFactory<Short> {

    private static StreamIDFactory defaultInstance;

    public static StreamIDFactory getInstance() {
        if(defaultInstance == null) defaultInstance = new StreamIDFactory();
        return defaultInstance;
    }

    private StreamIDFactory() {
        super();
        occupiedIDs.add((short) 0);
    }

    public short generateStreamID() {
        short proposedValue = generateRandomShort();
        while (occupiedIDs.contains(proposedValue)) {
            proposedValue = generateRandomShort();
        }

        return proposedValue;
    }

}
