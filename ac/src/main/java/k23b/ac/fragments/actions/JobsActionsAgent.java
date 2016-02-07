package k23b.ac.fragments.actions;

import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import k23b.ac.R;
import k23b.ac.activities.AssignJobActivity;
import k23b.ac.activities.MainActivity;
import k23b.ac.rest.Agent;
import k23b.ac.services.Logger;
import k23b.ac.services.UserManager;

/**
 * The Action called by the JobsFragment for Job Assignment 
 */
public class JobsActionsAgent implements ActionMode.Callback {

    public interface Callback {

        public Activity getActivity();

        public void removeActionMode();
    }

    private Callback callback;
    private Agent selectedAgent;

    public JobsActionsAgent(Callback callback, Agent selectedAgent) {
        super();

        this.callback = callback;
        this.selectedAgent = selectedAgent;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.job_actions_agent, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        if (callback.getActivity() == null)
            return false;

        if (!UserManager.getInstance().isUserStored()) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            callback.getActivity().finish();
            return false;
        }

        switch (item.getItemId()) {

        case R.id.action_agent_assign_job:

            startAssignJobActivity(selectedAgent);
            
            mode.finish();
            
            return true;

        default:

            return false;
        }
    }

    private void startAssignJobActivity(Agent selectedAgent) {

        if (callback.getActivity() == null || selectedAgent == null)
            return;

        Intent assignIntent = new Intent(callback.getActivity(), AssignJobActivity.class);
        assignIntent.putExtra("agentId", selectedAgent.getAgentId());
        assignIntent.putExtra("requestHash", selectedAgent.getShortRequestHash());

        // current.startActivity(assignIntent);

        callback.getActivity().startActivityForResult(assignIntent, MainActivity.ASSIGN_JOB_REQUEST);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.callback.removeActionMode();
    }
}
