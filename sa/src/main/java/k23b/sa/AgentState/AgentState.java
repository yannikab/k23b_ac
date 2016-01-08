package k23b.sa.AgentState;

import org.apache.log4j.Logger;

import k23b.sa.Threads.MainThread;

/**
 * The mother class that holds and can change the current State of the SA.
 */

public abstract class AgentState {

    private static final Logger log = Logger.getLogger(AgentState.class);

    private MainThread mainThread;

    protected MainThread getMainThread() {
        return this.mainThread;
    }

    protected void setCurrentState(AgentState newState) {
        this.mainThread.setCurrentState(newState);
    }

    public AgentState(MainThread mainThread) {

        this.mainThread = mainThread;

        String message = "Switched to " + this.getClass().getSimpleName();
        log.info(message);
        System.out.println(message);
    }

    public abstract void handleState();
}
