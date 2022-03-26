package persistence;

import connection.relay.TorRelay;

import java.io.IOException;
import java.util.ArrayList;

public class TorDirectoryFileHandler extends CSVFileHandler<TorRelay> {

    /**
     * Creates an instance of CSVFileHandler. Any file extension is allowed.
     * File at path will be created if it does not exist.
     *
     * @param path Full path to file on disk
     * @throws IOException If there is trouble creating the file
     */
    public TorDirectoryFileHandler(String path) throws IOException {
        super(path);
    }

    @Override
    public void saveToFile(TorRelay[] objectList) throws IOException {
        ArrayList<ArrayList<String>> rawData = new ArrayList<>();

        for(TorRelay tr : objectList) {
            ArrayList<String> row = new ArrayList<>();
            row.add(tr.getAddressAsString());
            row.add(tr.getPort() + "");
            row.add(tr.getDirPort() + "");
            row.add(tr.getFingerprint());
            row.add(tr.isSuitableEntryNode() + "");
            row.add(tr.supportsExit() + "");
        }

        super.saveRawFileDataToFile(rawData);
    }

    @Override
    public TorRelay[] getObjects() throws IOException {
        return new TorRelay[0];
    }
}
