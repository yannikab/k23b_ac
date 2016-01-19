package k23b.ac.views;

import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import k23b.ac.AssignJobActivity;
import k23b.ac.MainActivity;
import k23b.ac.R;
import k23b.ac.rest.Agent;

public class JobsActionsAgent implements ActionMode.Callback {

    private JobsFragment jobsFragment;
    private Agent selectedAgent;

    public JobsActionsAgent(JobsFragment jobsFragment, Agent selectedAgent) {
        super();

        this.jobsFragment = jobsFragment;
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

        switch (item.getItemId()) {
        case R.id.action_agent_assign_job:
            // jobsFragment.showAgent(selectedAgent);
            Activity current = jobsFragment.getActivity();
            startAssignJobActivity(current, selectedAgent);
            mode.finish();
            return true;
        default:
            return false;
        }
    }

    private void startAssignJobActivity(Activity current, Agent selectedAgent) {

        if (current == null || selectedAgent == null)
            return;

        Intent assignIntent = new Intent(current, AssignJobActivity.class);
        assignIntent.putExtra("agentId", selectedAgent.getAgentId());
        assignIntent.putExtra("requestHash", selectedAgent.getShortRequestHash());

        // current.startActivity(assignIntent);

        current.startActivityForResult(assignIntent, MainActivity.ASSIGN_JOB_REQUEST);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.jobsFragment.onDestroyActionMode();
    }
}
