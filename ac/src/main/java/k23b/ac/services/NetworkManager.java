package k23b.ac.services;

import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import k23b.ac.services.observer.Observable;
import k23b.ac.services.observer.Observer;

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

    public static NetworkManager getInstance() {

        synchronized (NetworkManager.class) {

            if (NetworkManager.instance == null)
                instance = new NetworkManager();

            return instance;
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

            if (observerList.isEmpty())
                return;

            if (!observerList.remove(observer))
                Log.e(NetworkManager.class.getName(), "Could not unregister Observer");

            Log.d(NetworkManager.class.getName(), "Observer unregistered");
        }
    }

    @Override
    public void notifyObservers() {

        for (Observer<State> obs : observerList)
            obs.update(this, getCurrentState());

        Log.d(NetworkManager.class.getName(), "Observers notified");

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
