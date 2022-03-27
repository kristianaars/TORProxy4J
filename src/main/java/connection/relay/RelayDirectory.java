package connection.relay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class RelayDirectory {

    public static RelayDirectory instance;

    private final ArrayList<TorRelay> RELAYS = new ArrayList<>(Arrays.asList(
            //new TorRelay("128.31.0.34", 9101, 9131, "9695DFC35FFEB861329B9F1AB04C46397020CE31", true, false),
            new TorRelay("85.25.185.17", 9001, 80, "F7B8A4B5F16ECDF6CA626F96F4E3C219D1A664EC", true, false),
            new TorRelay("185.72.244.37", 443, 80,"ECFDEA48C8A9F45A7241AEDA3C386F4D82F89689", true, false),
            new TorRelay("62.210.137.212", 443, 80,"F813C2EC410101E363AAF0C08D8C4B14EA614564", true, false),
            new TorRelay("138.201.169.12", 443, 80,"DBE82FA23B9FE3CB2462A6FCF5289DED3CBF4AEE", true, false),
            //new TorRelay("5.2.69.50", 9001, -1, "0B1120660999AD1022D08664BE1AD08A77F55E50", false, true),
            new TorRelay("144.172.73.50", 443, 80, "D42481A79771ADBB81B7DB5D2538815DCBE7B162", false, true)
    ));

    private final ArrayList<TorRelay> DIRECTORY_PICK_LIST;
    private final ArrayList<TorRelay> RELAY_PICK_LIST = new ArrayList<>(RELAYS);

    private RelayDirectory() {
        DIRECTORY_PICK_LIST = buildDirectoryList();
    }

    public static RelayDirectory getInstance() {
        if(instance == null) instance = new RelayDirectory();
        return instance;
    }

    private ArrayList<TorRelay> buildDirectoryList() {
        return RELAYS.stream().filter(TorRelay::isDirectoryNode).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Picks a relay and removes it from the RELAY PICK LIST. Two relays cannot occur twice on the same instance.
     *
     * @return Random relay removed from the pick_list.
     */
    public TorRelay pickRandomRelay(RelayType type) {
        Random r = new Random();
        ArrayList<TorRelay> pickList = null;

        switch (type) {
            case EXIT_NODE:
                pickList = RELAY_PICK_LIST.stream().filter(TorRelay::supportsExit).collect(Collectors.toCollection(ArrayList::new));
                break;
            case ENTRY_NODE:
                pickList = RELAY_PICK_LIST.stream().filter(TorRelay::isSuitableEntryNode).collect(Collectors.toCollection(ArrayList::new));
                break;
            case MIDDLE_NODE:
                pickList = RELAY_PICK_LIST.stream().filter(t -> !t.supportsExit()).collect(Collectors.toCollection(ArrayList::new));
                break;
        }

        TorRelay rl = pickList.get(r.nextInt(pickList.size()));
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

    /**
     * Picks a relay and removes it from the DIRECOTRY PICK LIST. Two relays cannot occur twice on this method on the same instance.
     *
     * @return Random relay, removed from the directory pick list.
     */
    public TorRelay pickRandomDirectoryRelay() {
        Random r = new Random();
        return DIRECTORY_PICK_LIST.remove(r.nextInt(DIRECTORY_PICK_LIST.size()-1));
    }

}
