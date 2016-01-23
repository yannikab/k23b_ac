package k23b.ac.activities;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.R;
import k23b.ac.db.dao.DatabaseHandler;
import k23b.ac.util.Logger;
import k23b.ac.util.Settings;

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
