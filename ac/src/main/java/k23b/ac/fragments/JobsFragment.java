package k23b.ac.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.db.dao.CachedJobDao;
import k23b.ac.db.srv.CachedAgentSrv;
import k23b.ac.db.srv.CachedJobSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.fragments.actions.JobsActionsAgent;
import k23b.ac.fragments.actions.JobsActionsJob;
import k23b.ac.fragments.adapters.AgentsArrayAdapter;
import k23b.ac.fragments.adapters.JobsArrayAdapter;
import k23b.ac.rest.Agent;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;
import k23b.ac.tasks.AgentsReceiveTask;
import k23b.ac.tasks.AgentsReceiveTask.AgentsReceiveCallback;
import k23b.ac.tasks.JobsReceiveTask;
import k23b.ac.tasks.JobsReceiveTask.JobsReceiveCallback;
import k23b.ac.util.AgentFactory;
import k23b.ac.util.JobFactory;
import k23b.ac.util.Settings;

public class JobsFragment extends FragmentBase implements AgentsReceiveCallback, JobsReceiveCallback, JobsActionsAgent.Callback, JobsActionsJob.Callback {

    private AgentsReceiveTask agentsReceiveTask;

    private JobsReceiveTask jobsReceiveTask;

    private List<Agent> agents;

    private Agent selectedAgent;

    private List<Job> jobs;

    private ActionMode actionMode;

    boolean initialized = false;

    public JobsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(2);

        if (initialized)
            return;

        initialized = true;

        fetchAgents();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        ListView agentsListView = (ListView) view.findViewById(R.id.jobs_listView_agents);

        agentsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (agentsReceiveTask != null || jobsReceiveTask != null)
                    return;

                if (actionMode != null)
                    return;

                selectedAgent = (Agent) parent.getAdapter().getItem(position);

                fetchJobs();

                view.setSelected(true);
            }
        });

        agentsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (agentsReceiveTask != null || jobsReceiveTask != null)
                    return false;

                if (actionMode != null)
                    return false;

                Agent agent = (Agent) parent.getAdapter().getItem(position);

                actionMode = JobsFragment.this.getActivity().startActionMode(new JobsActionsAgent(JobsFragment.this, agent));

                view.setSelected(true);

                return true;
            }
        });

        ListView jobsListView = (ListView) view.findViewById(R.id.jobs_listView_jobs);

        jobsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null)
                    return false;

                Job job = (Job) parent.getAdapter().getItem(position);

                actionMode = JobsFragment.this.getActivity().startActionMode(new JobsActionsJob(JobsFragment.this, job));

                view.setSelected(true);

                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }

        showAgents();

        showJobs();

        showProgress(agentsReceiveTask != null);

        showProgressJobs(jobsReceiveTask != null);

        if (initialized)
            return;

        initialized = true;

        fetchAgents();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.jobs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_jobs_refresh:

            fetchAgents();

            break;

        default:

            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchAgents() {

        if (agentsReceiveTask != null || jobsReceiveTask != null)
            return;

        if (getActivity() == null)
            return;

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            getActivity().finish();
            return;
        }

        if (!NetworkManager.isNetworkAvailable()) {

            // Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_SHORT).show();

            try {

                agentsRetrieved(CachedAgentSrv.findAll());

            } catch (SrvException e) {

                Logger.error(this.toString(), e.getMessage());

                agentsRetrieved(new HashSet<CachedAgentDao>());
            }

            return;
        }

        showProgress(true);

        agentsReceiveTask = new AgentsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword());

        agentsReceiveTask.execute();
    }

    private void agentsRetrieved(Set<CachedAgentDao> agents) {

        this.agents = new ArrayList<Agent>();

        for (CachedAgentDao ad : agents)
            this.agents.add(AgentFactory.fromCachedDao(ad));

        Collections.sort(this.agents);
        
        showAgents();

        this.selectedAgent = null;

        this.jobs = null;

        showJobs();
        
        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), "Network unavailable, " + agents.size() + " cached" + (agents.size() == 1 ? " agent" : " agents") + " retrieved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void agentsReceived(List<Agent> agents) {

        this.agents = agents;

        showAgents();

        this.selectedAgent = null;

        this.jobs = null;

        showJobs();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), agents.size() + (agents.size() == 1 ? " agent" : " agents") + " received", Toast.LENGTH_SHORT).show();
    }

    private void showAgents() {

        if (getView() == null)
            return;

        ListView agentsListView = (ListView) getView().findViewById(R.id.jobs_listView_agents);

        agentsListView.setAdapter(agents == null ? null : new AgentsArrayAdapter(getActivity(), this.agents));
    }

    private void fetchJobs() {

        if (agentsReceiveTask != null || jobsReceiveTask != null)
            return;

        if (selectedAgent == null)
            return;

        if (getActivity() == null)
            return;

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");

            abortActivity();
            return;
        }

        if (!NetworkManager.isNetworkAvailable()) {

            // Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_SHORT).show();

            try {

                jobsRetrieved(CachedJobSrv.findAllWithAgentId(selectedAgent.getAgentId()));

            } catch (SrvException e) {

                Logger.error(this.toString(), e.getMessage());

                jobsRetrieved(new HashSet<CachedJobDao>());
            }

            return;
        }

        showProgressJobs(true);

        jobsReceiveTask = new JobsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword(), selectedAgent.getRequestHash());

        jobsReceiveTask.execute();
    }

    private void jobsRetrieved(Set<CachedJobDao> jobs) {

        this.jobs = new ArrayList<Job>();

        for (CachedJobDao jd : jobs)
            this.jobs.add(JobFactory.fromCachedDao(jd));

        Collections.sort(this.jobs);
        
        showJobs();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), "Network unavailable, " + jobs.size() + " cached" + (jobs.size() == 1 ? " job" : " jobs") + " retrieved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void jobsReceived(List<Job> jobs) {

        this.jobs = jobs;

        showJobs();

        showProgressJobs(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), jobs.size() + (jobs.size() == 1 ? " job" : " jobs") + " received", Toast.LENGTH_SHORT).show();
    }

    private void showJobs() {

        if (getView() == null)
            return;

        TextView jobsTextView = (TextView) getView().findViewById(R.id.jobs_textView_jobs);

        jobsTextView.setText(selectedAgent == null ? "Jobs" : "Jobs for agent " + selectedAgent.getShortRequestHash());
        jobsTextView.setVisibility(View.VISIBLE);

        ListView jobsListView = (ListView) getView().findViewById(R.id.jobs_listView_jobs);

        jobsListView.setAdapter(jobs == null ? null : new JobsArrayAdapter(getActivity(), this.jobs));
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.jobs_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.jobs_view_agents).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showProgressJobs(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.jobs_progress_jobs).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.jobs_view_jobs).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void serviceError() {

        super.serviceError();

        showProgress(false);
        showProgressJobs(false);
    }

    @Override
    public void networkError() {

        super.networkError();

        showProgress(false);
        showProgressJobs(false);
    }

    @Override
    public void removeAgentsTask() {

        this.agentsReceiveTask = null;
    }

    @Override
    public void removeJobsTask() {

        this.jobsReceiveTask = null;
    }

    @Override
    public void removeActionMode() {

        this.actionMode = null;
    }
}
