package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public static InetAddress getPublicIP() {
        try {
            URL amazomIpCheckerInstance = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(amazomIpCheckerInstance.openStream()));
            return InetAddress.getByName(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
