package k23b.ac.util;

import k23b.ac.services.Logger;

/**
 * Get/Set the configurable parameters of the application
 *
 */
public class Settings {

    static {

        setBaseURI("http://ykab.dynu.net:8080/am/client/");
        // setBaseURI("http://192.168.2.7:8080/am/client/");
        Logger.info("Settings", "baseURI: " + getBaseURI());

        setSenderThreadInterval(60);
        Logger.info("Settings", "senderThreadInterval: " + getSenderThreadInterval());
    }

    private static String baseURI;
    private static int senderThreadInterval;

    public static int getSenderThreadInterval() {
        return senderThreadInterval;
    }

    public static void setSenderThreadInterval(int senderThreadInterval) {
        Settings.senderThreadInterval = senderThreadInterval;
    }

    public static String getBaseURI() {
        return baseURI;
    }

    private static void setBaseURI(String baseURI) {
        Settings.baseURI = baseURI;
    }
}
