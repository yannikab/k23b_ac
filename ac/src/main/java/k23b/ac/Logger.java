package k23b.ac;

import android.util.Log;

public class Logger {

    public static void debug(String tag, String message) {
        Log.d(tag, message);
    }

    public static void info(String tag, String message) {
        Log.i(tag, message);
    }

    public static void error(String tag, String message) {
        Log.e(tag, message);
    }

    public static void logException(String tag, Exception e) {

        if (e.getMessage() != null) {
            Logger.error(tag, e.getMessage());
            return;
        }

        for (StackTraceElement ste : e.getStackTrace())
            Logger.error(tag, ste.toString());
    }

    public static void wtf(String tag, Throwable throwable) {

        Log.wtf(tag, throwable);
    }
}
