package factory;

import java.util.ArrayList;
import java.util.Random;

public class CircIDFactory {

    private static CircIDFactory defaultInstance;

    private ArrayList<Integer> occupiedIDs;
    private Random randomGenerator;

    public static CircIDFactory getInstance() {
        if(defaultInstance == null) defaultInstance = new CircIDFactory();
        return defaultInstance;
    }

    private CircIDFactory() {
        randomGenerator = new Random();
        occupiedIDs = new ArrayList<>();
        occupiedIDs.add(0);
    }

    public int getCircID() {
        int proposedValue = 0x80000000 + generateRandomShort();
        while (occupiedIDs.contains(proposedValue)) {
            proposedValue = 0x80000000 + generateRandomShort();
        }

        return proposedValue;
    }

    private short generateRandomShort() {
        return  (short) (randomGenerator.nextInt(Short.MAX_VALUE));
    }
}

