package factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class IDFactory<T> {

    protected final List<T> occupiedIDs;
    protected final Random randomGenerator;

    protected IDFactory() {
        this.occupiedIDs = new ArrayList<>();
        this.randomGenerator = new Random();
    }

    protected short generateRandomShort() {
        return  (short) (randomGenerator.nextInt(Short.MAX_VALUE));
    }

    protected int generateRandomInt() {
        return  (randomGenerator.nextInt(Integer.MAX_VALUE));
    }
}
