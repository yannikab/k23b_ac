package k23b.ac.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    private static Context context;

    public static void setContext(Context context) {

        synchronized (NetworkManager.class) {

            if (NetworkManager.context == null)
                NetworkManager.context = context;
        }
    }

    public static boolean isNetworkAvailable() {

        synchronized (NetworkManager.class) {

            if (context == null)
                return false;

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
