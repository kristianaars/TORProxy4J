package model.relay;

import exceptions.DescriptorFieldNotFoundException;
import utils.ByteUtils;
import utils.CryptoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;

public class RelayDescriptor {

    private final static String AUTHORITY_DESCRIPTOR_URL_PATH = "/tor/server/authority";
    ///tor/server/authority

    public final byte[] IDENTITY_FINGERPRINT;
    public final byte[] NTOR_ONION_KEY;

    public RelayDescriptor(String descriptorText) throws DescriptorFieldNotFoundException {
        NTOR_ONION_KEY = parseNTorOnionKey(descriptorText);
        IDENTITY_FINGERPRINT = parseIdentityFingerprint(descriptorText);
    }

    private byte[] parseNTorOnionKey(String descriptorText) throws DescriptorFieldNotFoundException {
        //Format is as follows (Single line): ntor-onion-key 92NSXDKDuPWyRXIKapb1FUpTuJKpR5Je8HZAATS3mTc

        final String SEARCH_FOR = "ntor-onion-key ";

        BufferedReader buf = new BufferedReader(new StringReader(descriptorText));
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                if(line.contains(SEARCH_FOR)) {
                    if(!line.endsWith("=")) { line = line += "="; }

                    return CryptoUtils.decodeBase64(
                            line.replace(SEARCH_FOR, "")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new DescriptorFieldNotFoundException("Unable to find valid descriptor field with header " + SEARCH_FOR);
    }

    private byte[] parseIdentityFingerprint(String descriptorText) throws DescriptorFieldNotFoundException {
        final String HEADER = "fingerprint ";

        BufferedReader buf = new BufferedReader(new StringReader(descriptorText));
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                if(line.contains(HEADER)) {
                    String rawValue = line.replace(HEADER, "").replace(" ", "");

                    return ByteUtils.hexStringToByteArray(rawValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new DescriptorFieldNotFoundException("Unable to find valid descriptor field with header " + HEADER);
    }
    public static RelayDescriptor getRelayDescriptorFor(String address, int port) throws IOException, DescriptorFieldNotFoundException {
        try {
            URL url = new URL("http://" + address + ":" + port + AUTHORITY_DESCRIPTOR_URL_PATH);
            BufferedReader s = new BufferedReader(new InputStreamReader(url.openStream()));

            String result = "", input;

            while ((input = s.readLine()) != null) {
                result += input + "\n";
            }

            s.close();

            return new RelayDescriptor(result);

        } catch (IOException e) {
            throw new IOException("Unable to read server descriptor: " + e.getMessage());
        }

    }

    @Override
    public String toString() {
        return "RelayDescriptor{" +
                "IDENTITY_FINGERPRINT='" + ByteUtils.toString(IDENTITY_FINGERPRINT) + '\'' +
                ", NTOR_ONION_KEY='" + ByteUtils.toString(NTOR_ONION_KEY) + '\'' +
                '}';
    }
}
