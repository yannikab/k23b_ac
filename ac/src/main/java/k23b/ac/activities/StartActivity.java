package k23b.ac.activities;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.R;
import k23b.ac.services.Logger;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }
}
