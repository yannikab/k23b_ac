package k23b.ac.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import k23b.ac.R;
import k23b.ac.activities.LoginActivity;
import k23b.ac.db.dao.DatabaseHandler;
import k23b.ac.services.AssetManager;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.SenderService;
import k23b.ac.services.UserManager;
import k23b.ac.util.Settings;

/**
 * The Fragment in which the necessary initialization for the services occur and the LoginActivity starts.
 */
public class StartFragment extends Fragment {

    private boolean initialized = false;

    @Override
    public void onAttach(Activity activity) {

        Logger.info(this.toString(), "onAttach()");

        super.onAttach(activity);

        if (initialized)
            return;

        initialized = true;

        Logger.info(this.toString(), "Initializing.");

        final Context context = activity.getApplicationContext();

        Settings.setContext(context);

        Settings.getBaseURI();

        Settings.getCacheAgentsAndJobs();

        UserManager.getInstance().setContext(context);

        NetworkManager.setContext(context);

        NetworkManager.getInstance().registerReceiver();
        
        AssetManager.setContext(context);

        DatabaseHandler.setContext(context);

        Intent intent = new Intent(context, SenderService.class);

        intent.putExtra("interval", Settings.getSenderThreadInterval());

        context.startService(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {

        Logger.info(this.toString(), "onStart()");

        super.onStart();

        Intent intent = new Intent(getActivity(), LoginActivity.class);

        startActivity(intent);
    }
}
