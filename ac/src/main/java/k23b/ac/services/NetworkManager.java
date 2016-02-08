package k23b.ac.services;

import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import k23b.ac.services.observer.Observable;
import k23b.ac.services.observer.Observer;

/**
 * A singleton child class of BroadcastReceiver sensing changes in the Connectivity State. It also implements the Observable interface notifying the SenderThread of the aformentioned changes.
 *
 */
public class NetworkManager extends BroadcastReceiver implements Observable<State> {

    private static Context context;
    private static List<Observer<State>> observerList;
    private static NetworkManager instance;

    public static void setContext(Context context) {

        synchronized (NetworkManager.class) {

            if (NetworkManager.context == null)
                NetworkManager.context = context;
        }
    }

    /**
     * A getter of the instance of NetworkManager. It initializes the Network Manager on the first call.
     * 
     * @return The instance of NetworkManager.
     */
    public static NetworkManager getInstance() {

        synchronized (NetworkManager.class) {

            if (NetworkManager.instance == null)
                instance = new NetworkManager();

            return instance;
        }
    }

    public void registerReceiver() {

        synchronized (NetworkManager.class) {

            if (context != null) {

                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

                context.registerReceiver(NetworkManager.getInstance(), intentFilter);
            }
        }
    }

    public void unregisterReceiver() {

        synchronized (NetworkManager.class) {
            
            if (context != null)
                context.unregisterReceiver(this);
        }
    }

    @Override
    public void registerObserver(Observer<State> observer) {

        synchronized (NetworkManager.class) {

            if (observerList == null)
                observerList = new LinkedList<Observer<State>>();

            if (!observerList.contains(observer))
                observerList.add(observer);

            Log.d(NetworkManager.class.getName(), "Observer registered");
        }
    }

    @Override
    public void unregisterObserver(Observer<State> observer) {

        synchronized (NetworkManager.class) {

            if (observerList == null)
                return;

            if (observerList.isEmpty())
                return;

            if (!observerList.remove(observer))
                Log.e(NetworkManager.class.getName(), "Could not unregister Observer");

            Log.d(NetworkManager.class.getName(), "Observer unregistered");
        }
    }

    @Override
    public void notifyObservers() {

        synchronized (NetworkManager.class) {

            if (observerList == null)
                return;

            for (Observer<State> obs : observerList)
                obs.update(this, getCurrentState());

            Log.d(NetworkManager.class.getName(), "Observers notified");

        }
    }

    @Override
    public State getCurrentState() {

        synchronized (NetworkManager.class) {

            return isNetworkAvailable() ? State.CONNECTED : State.DISCONNECTED;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

        State state = info.getState();
        Log.d("InternalBroadcastReceiver", info.toString() + " " + state.toString());

        if (state == State.CONNECTED) {

            Log.d(NetworkManager.class.getName(), "Network Connection Available!");
            Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show();

        } else {

            Log.d(NetworkManager.class.getName(), "Network Connection Unavailable!");
            Toast.makeText(context, "Network Unavailable", Toast.LENGTH_SHORT).show();
        }

        notifyObservers();
    }

    /**
     * Getting the Network connection state.
     * 
     * @return True if there is an active Network connection; false otherwise.
     */
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
