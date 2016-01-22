package k23b.ac;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.dao.DatabaseHandler;

public class StartActivity extends Activity {

    private static final String fragmentTag = "retained.start.fragment";

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        DatabaseHandler.setContext(this.getApplicationContext());

        if (getFragmentManager().findFragmentByTag(fragmentTag) != null)
            return;

        getFragmentManager().beginTransaction().add(new StartFragment(), fragmentTag).commit();
        
        Logger.info(getLocalClassName(), "onCreate()");
    }
}
