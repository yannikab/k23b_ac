package k23b.ac.fragments;

import java.util.ArrayList;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.db.srv.CachedAgentSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.fragments.actions.AgentActions;
import k23b.ac.fragments.adapters.AgentsArrayAdapter;
import k23b.ac.rest.Agent;
import k23b.ac.rest.User;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;
import k23b.ac.tasks.AgentsReceiveTask;
import k23b.ac.tasks.AgentsReceiveTask.AgentsReceiveCallback;
import k23b.ac.util.AgentFactory;
import k23b.ac.util.Settings;

public class AgentsFragment extends FragmentBase implements AgentsReceiveCallback, AgentActions.Callback {

    private AgentsReceiveTask agentsReceiveTask;

    private List<Agent> agents;

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

                Agent selectedAgent = (Agent) parent.getAdapter().getItem(position);

                actionMode = AgentsFragment.this.getActivity().startActionMode(new AgentActions(AgentsFragment.this, selectedAgent));

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

            // Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();

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

        showAgents();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), "Network unavailable, " + agents.size() + " cached" + (agents.size() == 1 ? " agent" : " agents") + " retrieved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void agentsReceived(List<Agent> agents) {

        this.agents = agents;

        showAgents();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), agents.size() + (agents.size() == 1 ? " agent" : " agents") + " received", Toast.LENGTH_LONG).show();
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
    public void networkError() {

        super.networkError();

        showProgress(false);
    }

    @Override
    public void serviceError() {

        super.serviceError();

        showProgress(false);
    }

    @Override
    public void removeAgentsTask() {

        this.agentsReceiveTask = null;
    }

    @Override
    public void removeActionMode() {

        this.actionMode = null;
    }
}
