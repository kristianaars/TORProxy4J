package connection.relay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RelayDirectory {

    public static RelayDirectory instance;

    private RelayDirectory() {}

    public static RelayDirectory getInstance() {
        if(instance == null) instance = new RelayDirectory();
        return instance;
    }

    private final ArrayList<TorRelay> RELAYS = new ArrayList<>(Arrays.asList(
            new TorRelay("128.31.0.34", 9101, 9131, "9695DFC35FFEB861329B9F1AB04C46397020CE31"),
            new TorRelay("85.25.185.17", 9001, "F7B8A4B5F16ECDF6CA626F96F4E3C219D1A664EC"),
            new TorRelay("185.72.244.37", 443, "ECFDEA48C8A9F45A7241AEDA3C386F4D82F89689"),
            new TorRelay("62.210.137.212", 443, "F813C2EC410101E363AAF0C08D8C4B14EA614564"),
            new TorRelay("138.201.169.12", 443, "DBE82FA23B9FE3CB2462A6FCF5289DED3CBF4AEE"),
            new TorRelay("138.201.169.12", 443, "DBE82FA23B9FE3CB2462A6FCF5289DED3CBF4AEE")
    ));

    private final ArrayList<TorRelay> RELAY_PICK_LIST = new ArrayList<>(RELAYS);

    /**
     * Picks a relay and removes it from the RELAY PICK LIST. Two relays cannot occur twice on the same instance.
     *
     * @return Random relay removed from the pick_list.
     */
    public TorRelay pickRandomRelay() {
        Random r = new Random();
        TorRelay rl = RELAY_PICK_LIST.get(r.nextInt(RELAY_PICK_LIST.size()-1));
        RELAY_PICK_LIST.remove(rl);
        return rl;
    }

    /**
     * Returns a random relay from the global relay list. The item will not be deleted form the list after being picked.
     *
     * @return Random relay from the global relay list.
     */
    public TorRelay getRandomRelay() {
        Random r = new Random();
        return RELAYS.get(r.nextInt(RELAYS.size()-1));
    }

}
