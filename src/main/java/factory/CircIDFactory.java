package factory;

import java.util.ArrayList;
import java.util.Random;

public class CircIDFactory {

    private static CircIDFactory defaultInstance;

    private ArrayList<Short> occupiedIDs;
    private Random randomGenerator;

    public static CircIDFactory getInstance() {
        if(defaultInstance == null) defaultInstance = new CircIDFactory();
        return defaultInstance;
    }

    private CircIDFactory() {
        occupiedIDs = new ArrayList<>();
        occupiedIDs.add((short) 0);
    }

    public short getCircID() {
        short proposedValue = generateRandomShort();
        while (occupiedIDs.contains(proposedValue)) {
            proposedValue = generateRandomShort();
        }

        return proposedValue;
    }

    private short generateRandomShort() {
        return  (short) (randomGenerator.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE);
    }
}

