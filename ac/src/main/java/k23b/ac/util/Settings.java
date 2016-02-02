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
        Logger.info(Settings.class.getSimpleName(), "baseURI: " + getBaseURI());

        setSenderThreadInterval(30);
        Logger.info(Settings.class.getSimpleName(), "senderThreadInterval: " + getSenderThreadInterval());

        setLogOutOnSessionExpiry(true);
        Logger.info(Settings.class.getSimpleName(), "logOutOnSessionExpiry: " + getLogOutOnSessionExpiry());
    }

    private static String baseURI;
    private static int senderThreadInterval;
    private static boolean logOutOnSessionExpiry;

    public static int getSenderThreadInterval() {
        return senderThreadInterval;
    }

    private static void setSenderThreadInterval(int senderThreadInterval) {
        Settings.senderThreadInterval = senderThreadInterval;
    }

    public static String getBaseURI() {
        return baseURI;
    }

    private static void setBaseURI(String baseURI) {
        Settings.baseURI = baseURI;
    }

    public static boolean getLogOutOnSessionExpiry() {
        return logOutOnSessionExpiry;
    }

    private static void setLogOutOnSessionExpiry(boolean logOutOnSessionExpiry) {
        Settings.logOutOnSessionExpiry = logOutOnSessionExpiry;
    }
}
