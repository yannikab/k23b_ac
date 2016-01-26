package k23b.ac.util;

/**
 * Get/Set the configurable parameters of the application
 *
 */
public class Settings {

    static {

        setBaseURI("http://192.168.1.9:8080/am/client/");
        // setBaseURI("http://192.168.2.7:8080/am/client/");

        Logger.info("Settings", "baseURI: " + getBaseURI());

        setJobRequestInterval(20);
        Logger.info("Settings", "jobRequestInterval: " + getJobRequestInterval());

        setSenderThreadInterval(60);
        Logger.info("Settings", "senderThreadInterval: " + getSenderThreadInterval());
    }

    private static String baseURI;
    private static int jobRequestInterval;
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

    public static int getJobRequestInterval() {
        return jobRequestInterval;
    }

    private static void setJobRequestInterval(int jobRequestInterval) {
        Settings.jobRequestInterval = jobRequestInterval;
    }
}
