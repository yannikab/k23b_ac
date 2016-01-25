package k23b.ac.activities;

import android.app.Activity;
import android.os.Bundle;
import k23b.ac.R;
import k23b.ac.db.dao.DatabaseHandler;
import k23b.ac.threads.SenderThread;
import k23b.ac.util.Logger;
import k23b.ac.util.Settings;

public class StartActivity extends Activity {

    private static SenderThread senderThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        Settings.getBaseURI();

        DatabaseHandler.setContext(this.getApplicationContext());

        senderThread = new SenderThread(this);
        senderThread.start();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        senderThread.interrupt();
        try {
            senderThread.join();
        } catch (InterruptedException e) {
            Logger.error(this.toString(), e.getMessage());
        }

        super.onDestroy();
    }
}
