package factory;

public class CircIDFactory extends IDFactory<Integer> {

    private static CircIDFactory defaultInstance;

    public static CircIDFactory getInstance() {
        if(defaultInstance == null) defaultInstance = new CircIDFactory();
        return defaultInstance;
    }

    private CircIDFactory() {
        super();
        occupiedIDs.add(0);
    }

    public int generateCircID() {
        int proposedValue = 0x80000000 + generateRandomShort();
        while (occupiedIDs.contains(proposedValue)) {
            proposedValue = 0x80000000 + generateRandomShort();
        }

        return proposedValue;
    }

}

