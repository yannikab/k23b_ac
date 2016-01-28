package k23b.ac.fragments.actions;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.services.JobDispatcher;
import k23b.ac.services.Logger;
import k23b.ac.services.UserManager;

public class JobsActionsJob implements ActionMode.Callback {

    public interface Callback {

        public Activity getActivity();

        public void destroyActionMode();
    }

    private Callback callback;
    private Job selectedJob;

    public JobsActionsJob(Callback callback, Job selectedJob) {
        super();

        this.callback = callback;
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

        if (callback.getActivity() == null)
            return false;

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            callback.getActivity().finish();
            return false;
        }

        switch (item.getItemId()) {

        case R.id.action_job_stop:

            stopJob(u, selectedJob);
            mode.finish();
            return true;

        case R.id.action_job_reassign:

            reassignJob(u, selectedJob);
            mode.finish();
            return true;

        default:

            return false;
        }
    }

    private void stopJob(User u, Job j) {

        if (callback.getActivity() == null)
            return;

        if (!selectedJob.getPeriodic())
            return;

        Job job = new Job();
        job.setAgentId(j.getAgentId());
        job.setParams(String.format("stop %d", j.getJobId()));
        job.setPeriodic(false);

        u.getJobs().add(job);

        JobDispatcher.getInstance().dispatch(callback.getActivity(), u);

        Toast.makeText(callback.getActivity(), String.valueOf(job.getParams()), Toast.LENGTH_LONG).show();
    }

    private void reassignJob(User u, Job j) {

        if (callback.getActivity() == null)
            return;

        Job job = new Job();
        job.setAgentId(j.getAgentId());
        job.setParams(j.getParams());
        job.setPeriodic(j.getPeriodic());
        job.setPeriod(j.getPeriod());

        u.getJobs().add(job);

        JobDispatcher.getInstance().dispatch(callback.getActivity(), u);

        Toast.makeText(callback.getActivity(), String.valueOf(j.getParams()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.callback.destroyActionMode();
    }
}
