package k23b.ac.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import k23b.ac.R;
import k23b.ac.activities.LoginActivity;
import k23b.ac.activities.MainActivity;
import k23b.ac.rest.User;
import k23b.ac.tasks.UserLoginTask;
import k23b.ac.util.Logger;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;
import k23b.ac.util.UserManager;

public class StartFragment extends Fragment implements UserLoginTask.LoginCallback {

    private UserLoginTask userLoginTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        checkStatus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onStart() {

        Logger.info(this.toString(), "onStart()");

        super.onStart();

        if (userLoginTask != null)
            return;

        startLoginActivity();
    }

    private void checkStatus() {

        if (getActivity() == null)
            return;

        User u = UserManager.getInstance().getStoredUser(getActivity());

        if (u == null) {

            Logger.info(this.toString(), "No user stored, starting log in activity.");

            startLoginActivity();
            return;
        }

        if (!NetworkManager.networkAvailable(getActivity())) {

            Logger.info(this.toString(), "Network unavailable, starting log in activity.");

            startLoginActivity();
            return;
        }

        Logger.info(this.toString(), "Found stored user, authenticating with server.");

        userLoginTask = new UserLoginTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword());

        userLoginTask.execute((Void) null);
    }

    @Override
    public void loginSuccess() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Log in success, starting main activity.");

        startMainActivity();
    }

    private void startMainActivity() {

        showProgress(userLoginTask != null);

        Intent intent = new Intent(getActivity(), MainActivity.class);

        startActivity(intent);
    }

    @Override
    public void registrationPending() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Registration pending, starting log in activity.");

        startLoginActivity();
    }

    @Override
    public void incorrectCredentials() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Incorrect credentials, starting log in activity.");

        startLoginActivity();
    }

    @Override
    public void serviceError() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Service error, starting log in activity.");

        startLoginActivity();
    }

    @Override
    public void networkError() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Network error, starting log in activity.");

        startLoginActivity();
    }

    private void startLoginActivity() {

        showProgress(userLoginTask != null);

        Intent intent = new Intent(getActivity(), LoginActivity.class);

        startActivity(intent);
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.start_progress).setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
