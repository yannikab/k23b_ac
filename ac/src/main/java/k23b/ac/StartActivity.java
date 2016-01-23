package k23b.ac;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.dao.DatabaseHandler;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        Settings.getBaseURI();

        DatabaseHandler.setContext(this.getApplicationContext());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }
}
