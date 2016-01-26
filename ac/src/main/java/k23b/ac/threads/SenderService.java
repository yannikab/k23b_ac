package k23b.ac.threads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import k23b.ac.util.Logger;

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

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int interval = intent.getIntExtra("interval", 60);

        Logger.info(this.toString(), String.format("Starting job sender thread. (interval=%d)", interval));

        senderThread = new SenderThread(interval);

        senderThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Logger.info(this.toString(), "onTaskRemoved()");

        super.onTaskRemoved(rootIntent);

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
    }
}
