package k23b.ac;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    private static NetworkManager instance;

    private NetworkManager() {
        super();
    }

    public static NetworkManager getInstance() {

        synchronized (NetworkManager.class) {

            if (instance == null)
                instance = new NetworkManager();
        }

        return instance;
    }

    public static boolean networkAvailable(Activity activity) {

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
