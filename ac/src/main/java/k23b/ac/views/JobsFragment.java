package k23b.ac.views;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
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
import k23b.ac.MainActivity;
import k23b.ac.R;
import k23b.ac.Settings;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentsFetchTask;
import k23b.ac.rest.AgentsFetchTask.AgentsReceiver;
import k23b.ac.rest.Job;
import k23b.ac.rest.JobsFetchTask;
import k23b.ac.rest.JobsFetchTask.JobsReceiver;

public class JobsFragment extends Fragment implements AgentsReceiver, JobsReceiver {

    private AgentsFetchTask agentsFetchTask;

    private JobsFetchTask jobsFetchTask;

    private List<Agent> agents;

    private Agent selectedAgent;

    private List<Job> jobs;

    private ActionMode actionMode;

    public JobsFragment() {

        super();
        
        fetchAgents();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(2);
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

        showProgress(agentsFetchTask != null || jobsFetchTask != null);
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

        if (agentsFetchTask != null || jobsFetchTask != null)
            return;

        showProgress(true);

        agentsFetchTask = new AgentsFetchTask(this, Settings.getBaseURI(), "Yannis", "36BBE50ED96841D10443BCB670D6554F0A34B761BE67EC9C4A8AD2C0C44CA42C");

        agentsFetchTask.execute();
    }

    @Override
    public void setAgents(List<Agent> agents) {

        this.agentsFetchTask = null;

        this.agents = agents;

        showAgents();

        this.selectedAgent = null;

        this.jobs = null;

        showJobs();

        showProgress(false);
    }

    private void showAgents() {

        if (getView() == null)
            return;

        ListView agentsListView = (ListView) getView().findViewById(R.id.jobs_listView_agents);

        agentsListView.setAdapter(agents == null ? null : new AgentsArrayAdapter(getActivity(), this.agents));
    }

    private void fetchJobs() {

        if (agentsFetchTask != null || jobsFetchTask != null)
            return;

        if (selectedAgent == null)
            return;

        showProgress(true);

        jobsFetchTask = new JobsFetchTask(this, Settings.getBaseURI(), "Yannis", "36BBE50ED96841D10443BCB670D6554F0A34B761BE67EC9C4A8AD2C0C44CA42C", selectedAgent.getRequestHash());

        jobsFetchTask.execute();
    }

    @Override
    public void setJobs(List<Job> jobs) {

        jobsFetchTask = null;

        this.jobs = jobs;

        showJobs();

        showProgress(false);
    }

    private void showJobs() {

        if (getView() == null)
            return;

        TextView jobsTextView = (TextView) getView().findViewById(R.id.jobs_textView_jobs);

        jobsTextView.setText(selectedAgent == null ? "" : "Jobs for Agent " + selectedAgent.getShortRequestHash());

        ListView jobsListView = (ListView) getView().findViewById(R.id.jobs_listView_jobs);

        jobsListView.setAdapter(jobs == null ? null : new JobsArrayAdapter(getActivity(), this.jobs));
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.jobs_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.jobs_view_agents).setVisibility(show ? View.GONE : View.VISIBLE);
        getView().findViewById(R.id.jobs_view_jobs).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void showAgent(Agent agent) {

        Toast.makeText(getActivity(), String.valueOf(agent.getAgentId()), Toast.LENGTH_LONG).show();
    }

    public void showJob(Job job) {

        Toast.makeText(getActivity(), String.valueOf(job.getParams()), Toast.LENGTH_LONG).show();
    }

    public void onDestroyActionMode() {
        actionMode = null;
    }
}
