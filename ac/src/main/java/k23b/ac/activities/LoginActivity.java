package k23b.ac.activities;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.R;
import k23b.ac.util.Logger;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Logger.info(this.toString(), "onCreate()");
        
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }
}
