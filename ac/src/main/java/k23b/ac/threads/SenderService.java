package k23b.ac.threads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import k23b.ac.util.Logger;
import k23b.ac.util.Settings;

public class SenderService extends Service {

    private SenderThread senderThread;

    public SenderService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Logger.info(this.toString(), "onCreate()");

        senderThread = new SenderThread(Settings.getSenderThreadInterval());

        senderThread.start();

        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Logger.info(this.toString(), "onTaskRemoved()");

        Logger.info(this.toString(), "Stopping job sender thread.");

        senderThread.interrupt();

        try {

            senderThread.join();

        } catch (InterruptedException e) {
            // e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        Logger.info(this.toString(), "Sender service stopping.");
        
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }
}
