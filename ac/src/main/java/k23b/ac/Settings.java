package k23b.ac;

import android.util.Log;

/**
 * Get/Set the configurable parameters of the application
 *
 */
public class Settings {

    static {

        setBaseURI("http://192.168.1.15:8080/am/client/");
        Log.i("Settings", "baseURI: " + getBaseURI());
    }

    private static String baseURI;

    public static String getBaseURI() {
        return baseURI;
    }

    private static void setBaseURI(String baseURI) {
        Settings.baseURI = baseURI;
    }
}
