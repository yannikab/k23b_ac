package k23b.ac;

import android.app.Activity;
import android.os.Bundle;

/**
 * A screen that offers registration via username/password.
 * 
 */
public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Logger.info(this.toString(), "onCreate()");
        
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onStart() {

        Logger.info(this.toString(), "onStart()");

        super.onStart();
    }

    @Override
    protected void onResume() {
        
        Logger.info(this.toString(), "onResume()");

        super.onResume();
    }

    @Override
    protected void onPause() {
        
        Logger.info(this.toString(), "onPause()");

        super.onPause();
    }

    @Override
    protected void onStop() {
        
        Logger.info(this.toString(), "onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        
        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }
}
