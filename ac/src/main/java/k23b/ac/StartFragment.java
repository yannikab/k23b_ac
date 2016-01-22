package k23b.ac;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import k23b.ac.tasks.UserLoginTask;

public class StartFragment extends Fragment implements UserLoginTask.LoginCallback {

    private UserLoginTask userLoginTask = null;

    public static final String SHARED_PREFERENCES = "shared.preferences";
    public static final String PREF_USER_STORED = "user.stored";
    public static final String PREF_USER_NAME = "user.name";
    public static final String PREF_USER_PASSWORD = "user.password";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Logger.info(this.toString(), "onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();

        checkSharedPreferences();
    }

    private void checkSharedPreferences() {

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        boolean userStored = sp.getBoolean(PREF_USER_STORED, false);
        String username = sp.getString(PREF_USER_NAME, null);
        String password = sp.getString(PREF_USER_PASSWORD, null);

        if (!userStored || username == null || password == null) {
            
            Logger.info(this.toString(), "Logged in user not found in shared preferences, starting log in activity.");
            
            startLoginActivity();
            return;
        }

        Logger.info(this.toString(), "Logged in user found in shared preferences, authenticating with server.");
        
        userLoginTask = new UserLoginTask(this, Settings.getBaseURI(), username, password);

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

        this.userLoginTask = null;

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

        this.userLoginTask = null;

        Intent intent = new Intent(getActivity(), LoginActivity.class);

        startActivity(intent);
    }

    @Override
    public String toString() {

        return "StartFragment";
    }
}
