package k23b.ac.fragments;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.fragments.adapters.AgentsArrayAdapter;
import k23b.ac.rest.Agent;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.services.JobDispatcher;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;
import k23b.ac.tasks.AgentsReceiveTask;
import k23b.ac.tasks.AgentsReceiveTask.AgentsReceiveCallback;
import k23b.ac.util.Settings;

public class AgentsFragment extends Fragment implements AgentsReceiveCallback, ActionMode.Callback {

    private AgentsReceiveTask agentsReceiveTask;

    private List<Agent> agents;

    private Agent selectedAgent;

    private ActionMode actionMode;

    boolean initialized = false;

    public AgentsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(1);

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

        if (savedInstanceState == null)
            return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agents, container, false);

        ListView agentsListView = (ListView) view.findViewById(R.id.agents_listView);

        agentsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null)
                    return false;

                selectedAgent = (Agent) parent.getAdapter().getItem(position);

                actionMode = AgentsFragment.this.getActivity().startActionMode(AgentsFragment.this);

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

        showProgress(agentsReceiveTask != null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.agents, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case R.id.action_agents_refresh:

            fetchAgents();
            break;

        default:

            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchAgents() {

        if (agentsReceiveTask != null)
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

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        showProgress(true);

        agentsReceiveTask = new AgentsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword());

        agentsReceiveTask.execute();
    }

    @Override
    public void agentsReceived(List<Agent> agents) {

        this.agents = agents;

        showAgents();

        showProgress(false);
    }

    private void showAgents() {

        if (getView() == null)
            return;

        ListView agentsListView = (ListView) getView().findViewById(R.id.agents_listView);

        agentsListView.setAdapter(agents == null ? null : new AgentsArrayAdapter(getActivity(), this.agents));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.agents_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.agents_view).setVisibility(show ? View.GONE : View.VISIBLE);
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
            getActivity().finish();
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

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        this.actionMode = null;
        this.selectedAgent = null;
    }

    private void terminateAgent(User u) {

        if (getActivity() == null)
            return;

        Job job = new Job();
        job.setAgentId(selectedAgent.getAgentId());
        job.setParams("exit");
        job.setPeriodic(false);

        u.getJobs().add(job);

        JobDispatcher.getInstance().dispatch(getActivity(), u);

        Toast.makeText(getActivity(), String.valueOf(job.getParams()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void registrationPending() {

        Logger.info(this.toString(), "Registration pending, aborting activity.");

        abortActivity();
    }

    @Override
    public void incorrectCredentials() {

        Logger.info(this.toString(), "Incorrect credentials, aborting activity.");

        abortActivity();
    }

    private void abortActivity() {

        if (getActivity() == null)
            return;

        getActivity().finish();
    }

    @Override
    public void serviceError() {

        Toast.makeText(getActivity(), getString(R.string.error_service_error), Toast.LENGTH_LONG).show();

        showProgress(false);
    }

    @Override
    public void networkError() {

        Toast.makeText(getActivity(), getString(R.string.error_network_error), Toast.LENGTH_LONG).show();

        showProgress(false);
    }

    @Override
    public void removeAgentsTask() {

        this.agentsReceiveTask = null;
    }
}
