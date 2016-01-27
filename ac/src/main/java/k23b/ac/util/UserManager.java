package k23b.ac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import k23b.ac.rest.User;

public class UserManager {

    private static final String SHARED_PREFERENCES = "shared.preferences";
    private static final String PREF_USER_STORED = "user.stored";
    private static final String PREF_USER_NAME = "user.name";
    private static final String PREF_USER_PASSWORD = "user.password";

    private Context context;

    private static UserManager instance;

    private UserManager() {
        super();

        this.context = null;
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

    public void setContext(Context context) {

        synchronized (UserManager.class) {

            if (this.context == null)
                this.context = context;
        }
    }

    private synchronized User getUser() {

        if (context == null) {

            Logger.info(this.toString(), "No context set, can not retrieve stored user.");

            return null;
        }

        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        boolean userStored = sp.getBoolean(PREF_USER_STORED, false);
        String username = sp.getString(PREF_USER_NAME, null);
        String password = sp.getString(PREF_USER_PASSWORD, null);

        if (!userStored)
            return null;

        if (username == null || password == null) {

            setUser(null);
            return null;
        }

        Logger.info(getInstance().toString(), "User: " + username);

        return new User(username, password);
    }

    private synchronized void setUser(User user) {

        if (this.context == null) {

            Logger.info(this.toString(), "No context set, user will not be stored.");

            return;
        }

        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        Editor editor = sp.edit();

        editor.putBoolean(PREF_USER_STORED, user != null ? true : false);
        editor.putString(PREF_USER_NAME, user != null ? String.valueOf(user.getUsername()) : "");
        editor.putString(PREF_USER_PASSWORD, user != null ? String.valueOf(user.getPassword()) : "");

        if (user != null)
            Logger.info(getInstance().toString(), "User: " + String.valueOf(user.getUsername()));

        editor.commit();
    }

    public boolean isUserStored() {

        Logger.info(getInstance().toString(), "Checking if a user is stored.");

        return getUser() != null;
    }

    public User getStoredUser() {

        Logger.info(getInstance().toString(), "Getting stored user.");

        return getUser();
    }

    public void storeUser(User user) {

        Logger.info(getInstance().toString(), "Storing user.");

        setUser(user);
    }

    public void clearUser() {

        Logger.info(getInstance().toString(), "Clearing stored user.");

        setUser(null);
    }

    @Override
    public String toString() {

        return this.getClass().getSimpleName();
    }
}
