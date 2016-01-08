package k23b.sa.Threads;

import javax.ws.rs.client.Client;

import k23b.sa.AgentStats;
import k23b.sa.Settings;
import k23b.sa.AgentState.AgentState;
import k23b.sa.AgentState.StartupState;

/**
 * The main thread that provides the functionality of the Software Agent
 *
 */
public class MainThread extends Thread {

    private Settings settings;
    private AgentStats agentStats;
    private Client client;
    private AgentState currentState;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public AgentStats getAgentStats() {
        return agentStats;
    }

    public void setAgentStats(AgentStats agentStats) {
        this.agentStats = agentStats;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AgentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AgentState newState) {
        this.currentState = newState;
    }

    public MainThread() {

        setName("Main");

        setCurrentState(new StartupState(this));
    }

    @Override
    public String toString() {

        return getName();
    }

    @Override
    public void run() {

        while (!isInterrupted())
            getCurrentState().handleState();

    }
}
