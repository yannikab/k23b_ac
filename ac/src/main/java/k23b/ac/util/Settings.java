package k23b.ac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import k23b.ac.services.Logger;

/**
 * Get/Set the configurable parameters of the application
 *
 */
public class Settings {

    private static final String defaultServer = "http://ykab.dynu.net:8080";
    // private static final String defaultServer = "http://192.168.2.7:8080";

    private static final String serverPath = "/am/client/";

    private static boolean serverLoaded = false;
    private static boolean cacheAgentsAndJobsLoaded = false;

    static {

        setSenderThreadInterval(30);
        Logger.info(Settings.class.getSimpleName(), "senderThreadInterval: " + getSenderThreadInterval());

        setLogOutOnSessionExpiry(true);
        Logger.info(Settings.class.getSimpleName(), "logOutOnSessionExpiry: " + getLogOutOnSessionExpiry());
    }

    private static Context context;

    public static void setContext(Context context) {

        synchronized (Settings.class) {

            if (Settings.context == null)
                Settings.context = context;
        }
    }

    private static int senderThreadInterval;
    private static boolean logOutOnSessionExpiry;

    public static int getSenderThreadInterval() {
        return senderThreadInterval;
    }

    private static void setSenderThreadInterval(int senderThreadInterval) {
        Settings.senderThreadInterval = senderThreadInterval;
    }

    public static String getBaseURI() {

        synchronized (Settings.class) {

            if (context == null)
                return null;

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            String serverAddress = sp.getString("server_address", null);

            if (serverAddress == null) {

                serverAddress = defaultServer;

                setServerAddress(defaultServer);
            }

            String baseURI = serverAddress + serverPath;

            if (!serverLoaded) {
                serverLoaded = true;

                Logger.info(Settings.class.getSimpleName(), "baseURI: " + baseURI);
            }

            return baseURI;
        }
    }

    private static void setServerAddress(String serverAddress) {

        if (context == null)
            return;

        Logger.info(Settings.class.getSimpleName(), "Setting server address to: " + serverAddress);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = sp.edit();
        editor.putString("server_address", serverAddress);
        editor.commit();
    }

    public static boolean getLogOutOnSessionExpiry() {
        return logOutOnSessionExpiry;
    }

    private static void setLogOutOnSessionExpiry(boolean logOutOnSessionExpiry) {
        Settings.logOutOnSessionExpiry = logOutOnSessionExpiry;
    }

    public static boolean getCacheAgentsAndJobs() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean cacheAgentsAndJobs = sp.getBoolean("cache_agents_and_jobs", true);

        if (!cacheAgentsAndJobsLoaded) {
            cacheAgentsAndJobsLoaded = true;

            Logger.info(Settings.class.getSimpleName(), "cacheAgentsAndJobs: " + getCacheAgentsAndJobs());
        }

        return cacheAgentsAndJobs;
    }
}
