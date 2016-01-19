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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import k23b.ac.MainActivity;
import k23b.ac.R;
import k23b.ac.Settings;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentsFetchTask;
import k23b.ac.rest.AgentsFetchTask.AgentsReceiver;

public class AgentsFragment extends Fragment implements AgentsReceiver, ActionMode.Callback {

    private AgentsFetchTask agentsFetchTask;

    private List<Agent> agents;

    private Agent selectedAgent;

    private ActionMode actionMode;

    public AgentsFragment() {

        super();

        fetchAgents();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        if (savedInstanceState == null)
            return;

        // state = savedInstanceState.getInt("state");
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

        showProgress(agentsFetchTask != null);
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

        if (agentsFetchTask != null)
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

        // outState.putInt("state", state);
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

        switch (item.getItemId()) {
        case R.id.action_agent_terminate:
            show();
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

    private void show() {
        Toast.makeText(getActivity(), String.valueOf(selectedAgent.getAgentId()), Toast.LENGTH_LONG).show();
    }
}
