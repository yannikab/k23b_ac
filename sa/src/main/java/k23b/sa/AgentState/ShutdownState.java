package k23b.sa.AgentState;

import org.apache.log4j.Logger;

import k23b.sa.Threads.MainThread;
import sun.misc.Signal;

/**
 * The State in which the SA exits.
 */

@SuppressWarnings("restriction")
public class ShutdownState extends AgentState {

    private static final Logger log = Logger.getLogger(ShutdownState.class);

    public ShutdownState(MainThread mainThread) {
        super(mainThread);
    }

    @Override
    public void handleState() {

        log.info("Raising SIGINT signal.");

        Signal.raise(new Signal("INT"));

        setCurrentState(new StoppedState(getMainThread()));
    }
}
