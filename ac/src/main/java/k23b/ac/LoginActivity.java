package k23b.ac;

import android.app.Activity;
import android.os.Bundle;

/**
 * A login screen that offers login via username/password.
 * 
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }
}
