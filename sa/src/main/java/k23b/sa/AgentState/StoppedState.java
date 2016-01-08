package k23b.sa.AgentState;

import k23b.sa.Threads.MainThread;

/**
 * The State in which the SA is stopped.
 */
public class StoppedState extends AgentState {

    public StoppedState(MainThread mainThread) {
        super(mainThread);
    }

    @Override
    public void handleState() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
