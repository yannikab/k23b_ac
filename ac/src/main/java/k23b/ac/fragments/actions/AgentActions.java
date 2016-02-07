package k23b.ac.fragments.actions;

import java.util.Date;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.rest.Agent;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.services.JobDispatcher;
import k23b.ac.services.Logger;
import k23b.ac.services.UserManager;

/**
 * The Action called by the AgentsFragment for Agent Termination
 */
public class AgentActions implements ActionMode.Callback {

    public interface Callback {

        public Activity getActivity();

        public void removeActionMode();
    }

    private Callback callback;
    private Agent selectedAgent;

    public AgentActions(Callback callback, Agent selectedAgent) {
        super();

        this.callback = callback;
        this.selectedAgent = selectedAgent;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.agent_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            callback.getActivity().finish();
            return false;
        }

        switch (item.getItemId()) {

        case R.id.action_agent_terminate:

            terminateAgent(u);

            mode.finish();

            return true;

        default:

            return false;
        }
    }

    private void terminateAgent(User u) {

        if (callback.getActivity() == null)
            return;

        Job job = new Job();
        job.setAgentId(selectedAgent.getAgentId());
        job.setTimeAssigned(new Date(System.currentTimeMillis()));
        job.setParams("exit");
        job.setPeriodic(false);

        u.getJobs().add(job);

        JobDispatcher.getInstance().dispatch(callback.getActivity(), u);

        Toast.makeText(callback.getActivity(), "Termination requested for agent " + selectedAgent.getShortRequestHash(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        this.callback.removeActionMode();

    }
}
