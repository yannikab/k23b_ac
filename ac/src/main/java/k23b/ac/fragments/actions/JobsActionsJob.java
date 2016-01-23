package k23b.ac.fragments.actions;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import k23b.ac.R;
import k23b.ac.fragments.JobsFragment;
import k23b.ac.rest.Job;

public class JobsActionsJob implements ActionMode.Callback {

    private JobsFragment jobsFragment;
    private Job selectedJob;

    public JobsActionsJob(JobsFragment jobsFragment, Job selectedJob) {
        super();

        this.jobsFragment = jobsFragment;
        this.selectedJob = selectedJob;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.job_actions_job, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_job_reassign:
            // TODO: re-create job with the same parameters
            jobsFragment.showJob(selectedJob);
            mode.finish();
            return true;
        case R.id.action_job_stop:
            if (selectedJob.getPeriodic()) {
                // TODO: create a stopping job for this periodic job
                jobsFragment.showJob(selectedJob);
            }
            mode.finish();
            return true;
        default:
            return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.jobsFragment.onDestroyActionMode();
    }
}
