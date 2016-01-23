package k23b.ac.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import k23b.ac.rest.User;

public class UserManager {

    private static final String SHARED_PREFERENCES = "shared.preferences";
    private static final String PREF_USER_STORED = "user.stored";
    private static final String PREF_USER_NAME = "user.name";
    private static final String PREF_USER_PASSWORD = "user.password";

    private static UserManager instance;

    private UserManager() {
        super();
    }

    public static UserManager getInstance() {

        synchronized (UserManager.class) {

            if (instance == null) {

                instance = new UserManager();

                Logger.info(instance.toString(), "Created user manager instance.");
            }
        }

        return instance;
    }

    private User getUser(Activity activity) {

        SharedPreferences sp = activity.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        boolean userStored = sp.getBoolean(PREF_USER_STORED, false);
        String username = sp.getString(PREF_USER_NAME, null);
        String password = sp.getString(PREF_USER_PASSWORD, null);

        if (!userStored)
            return null;

        if (username == null || password == null) {

            setUser(activity, null);
            return null;
        }

        Logger.info(getInstance().toString(), "User: " + username);

        return new User(username, password);
    }

    private void setUser(Activity activity, User user) {

        SharedPreferences sp = activity.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        Editor editor = sp.edit();

        editor.putBoolean(PREF_USER_STORED, user != null ? true : false);
        editor.putString(PREF_USER_NAME, user != null ? String.valueOf(user.getUsername()) : "");
        editor.putString(PREF_USER_PASSWORD, user != null ? String.valueOf(user.getPassword()) : "");

        if (user != null)
            Logger.info(getInstance().toString(), "User: " + String.valueOf(user.getUsername()));
            
        editor.commit();
    }

    public boolean isUserStored(Activity activity) {

        Logger.info(getInstance().toString(), "Checking if a user is stored.");

        return getUser(activity) != null;
    }

    public User getStoredUser(Activity activity) {

        Logger.info(getInstance().toString(), "Getting stored user.");

        return getUser(activity);
    }

    public void storeUser(Activity activity, User user) {

        Logger.info(getInstance().toString(), "Storing user.");

        setUser(activity, user);
    }

    public void clearUser(Activity activity) {

        Logger.info(getInstance().toString(), "Clearing stored user.");

        setUser(activity, null);
    }

    @Override
    public String toString() {

        return this.getClass().getSimpleName();
    }
}
