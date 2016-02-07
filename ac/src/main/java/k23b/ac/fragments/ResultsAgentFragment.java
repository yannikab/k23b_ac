package k23b.ac.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.db.srv.CachedAgentSrv;
import k23b.ac.db.srv.SrvException;
import k23b.ac.fragments.adapters.AgentsArrayAdapter;
import k23b.ac.fragments.adapters.ResultsArrayAdapter;
import k23b.ac.rest.Agent;
import k23b.ac.rest.Result;
import k23b.ac.rest.User;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;
import k23b.ac.tasks.AgentsReceiveTask;
import k23b.ac.tasks.AgentsReceiveTask.AgentsReceiveCallback;
import k23b.ac.tasks.ResultsAgentReceiveTask;
import k23b.ac.tasks.ResultsAgentReceiveTask.ResultsAgentReceiveCallback;
import k23b.ac.util.AgentFactory;
import k23b.ac.util.InputFilterMinMax;
import k23b.ac.util.Settings;
import k23b.ac.util.WebViewManager;

/**
 * The Fragment which attached to the Main Activity offers the list of available Results of a single Agent out of a list of the latter.
 */
public class ResultsAgentFragment extends FragmentBase implements AgentsReceiveCallback, ResultsAgentReceiveCallback {

    private AgentsReceiveTask agentsReceiveTask;

    private List<Agent> agents;

    private Agent selectedAgent;

    private ResultsAgentReceiveTask resultsReceiveTask;

    private List<Result> results;

    private Result selectedResult;

    private ActionMode actionMode;

    private WebViewManager webviewManager;

    private static final int defaultNumberOfResults = 50;

    private int numberOfResults;

    boolean initialized = false;

    public ResultsAgentFragment() {
        super();

        Logger.debug(this.toString(), "Instantiating.");

        numberOfResults = defaultNumberOfResults;
    }

    @Override
    public void onAttach(Activity activity) {

        Logger.debug(this.toString(), "onAttach()");

        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Logger.debug(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        if (savedInstanceState == null)
            return;

        this.numberOfResults = savedInstanceState.getInt("numberOfResults");
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logger.debug(this.toString(), "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_results_agent, container, false);

        final EditText results_number_editText = (EditText) view.findViewById(R.id.results_agent_editText_number);

        results_number_editText.setFilters(new InputFilter[] { new InputFilterMinMax("1", "999") });
        results_number_editText.setText(String.valueOf(numberOfResults));

        final Button buttonRefreshResults = (Button) view.findViewById(R.id.results_agent_button_refresh);

        buttonRefreshResults.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                closeSoftKeyboard();

                fetchResults();
            }
        });

        final ListView agentsListView = (ListView) view.findViewById(R.id.results_agent_listView_agents);

        agentsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null)
                    return;

                selectedAgent = (Agent) parent.getAdapter().getItem(position);

                fetchResults();

                view.setSelected(true);
            }
        });

        final ListView resultsListView = (ListView) view.findViewById(R.id.results_agent_listView_results);

        resultsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null)
                    return;

                selectedResult = (Result) parent.getAdapter().getItem(position);

                showOutput();

                view.setSelected(true);
            }
        });

        final WebView outputWebView = (WebView) view.findViewById(R.id.webview_results_agent);

        this.webviewManager = new WebViewManager(outputWebView);

        final Button buttonExpandAll = (Button) view.findViewById(R.id.results_agent_button_expand_all);

        buttonExpandAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                webviewManager.expandAll();
            }
        });

        final Button buttonCollapseAll = (Button) view.findViewById(R.id.results_agent_button_collapse_all);

        buttonCollapseAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                webviewManager.collapseAll();
            }
        });

        return view;
    }

    private void closeSoftKeyboard() {

        if (getActivity() == null)
            return;

        View view = getActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Logger.debug(this.toString(), "onActivityCreated()");

        super.onActivityCreated(savedInstanceState);

        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }

        showAgents();

        showResults();

        clearOutput();

        showOutput();

        showProgress(agentsReceiveTask != null);

        showProgressResults(resultsReceiveTask != null);

        if (initialized)
            return;

        initialized = true;

        fetchAgents();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Logger.debug(this.toString(), "onSaveInstanceState()");

        outState.putInt("numberOfResults", numberOfResults);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.results_agent, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case R.id.action_results_agent_refresh:

            fetchAgents();

            break;

        default:

            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchAgents() {

        if (agentsReceiveTask != null || resultsReceiveTask != null)
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

            if (Settings.getCacheAgentsAndJobs()) {

                try {

                    agentsRetrieved(CachedAgentSrv.findAll());

                } catch (SrvException e) {

                    Logger.error(this.toString(), e.getMessage());

                    agentsRetrieved(new HashSet<CachedAgentDao>());
                }

            } else {

                Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_SHORT).show();
            }

            return;
        }

        showProgress(true);

        clearOutput();

        agentsReceiveTask = new AgentsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword(), Settings.getCacheAgentsAndJobs());

        agentsReceiveTask.execute();
    }

    private void agentsRetrieved(Set<CachedAgentDao> agents) {

        this.agents = new ArrayList<Agent>();

        for (CachedAgentDao ad : agents)
            this.agents.add(AgentFactory.fromCachedDao(ad));

        Collections.sort(this.agents);

        showAgents();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), "Network unavailable, " + agents.size() + " cached" + (agents.size() == 1 ? " agent" : " agents") + " retrieved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void agentsReceived(List<Agent> agents) {

        this.agentsReceiveTask = null;

        this.agents = agents;

        showAgents();

        this.selectedAgent = null;

        this.results = null;

        showResults();

        clearOutput();

        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), agents.size() + (agents.size() == 1 ? " agent" : " agents") + " received", Toast.LENGTH_SHORT).show();
    }

    private void showAgents() {

        if (getView() == null)
            return;

        ListView agentsListView = (ListView) getView().findViewById(R.id.results_agent_listView_agents);

        agentsListView.setAdapter(agents == null ? null : new AgentsArrayAdapter(getActivity(), this.agents));
    }

    private void fetchResults() {

        if (agentsReceiveTask != null || resultsReceiveTask != null)
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

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_SHORT).show();
            return;
        }

        if (getView() == null)
            return;

        EditText results_number_editText = (EditText) getView().findViewById(R.id.results_agent_editText_number);

        int number = 0;

        try {

            number = Integer.valueOf(results_number_editText.getText().toString());

        } catch (NumberFormatException e) {

            return;
        }

        showProgressResults(true);

        clearOutput();

        resultsReceiveTask = new ResultsAgentReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword(), selectedAgent.getRequestHash(), number);

        resultsReceiveTask.execute();
    }

    private void clearOutput() {

        if (getView() == null)
            return;

        webviewManager.clearOutput();

        final View outputControls = getView().findViewById(R.id.output_controls);
        outputControls.setVisibility(View.GONE);

        final TextView textViewResultId = (TextView) getView().findViewById(R.id.textViewResultId);
        textViewResultId.setText("");
    }

    @Override
    public void resultsReceived(List<Result> results) {

        this.results = results;

        this.selectedResult = null;

        showResults();

        clearOutput();

        showProgressResults(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), results.size() + (results.size() == 1 ? " result" : " results") + " received", Toast.LENGTH_SHORT).show();
    }

    private void showResults() {

        if (getView() == null)
            return;

        TextView resultsTextView = (TextView) getView().findViewById(R.id.results_agent_textView_results);

        resultsTextView.setText(selectedAgent == null ? "" : "Results for agent " + selectedAgent.getShortRequestHash());
        resultsTextView.setVisibility(selectedAgent == null ? View.GONE : View.VISIBLE);

        ListView resultsListView = (ListView) getView().findViewById(R.id.results_agent_listView_results);

        resultsListView.setAdapter(results == null ? null : new ResultsArrayAdapter(getActivity(), this.results));
    }

    private void showOutput() {

        if (selectedResult == null)
            return;

        if (getView() == null)
            return;

        final TextView textViewResultId = (TextView) getView().findViewById(R.id.textViewResultId);

        textViewResultId.setText("Output for result " + selectedResult.getResultId());

        final View outputControls = getView().findViewById(R.id.output_controls);

        outputControls.setVisibility(View.VISIBLE);

        webviewManager.displayOutput(selectedResult.getJobResult());
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.results_agents_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.results_agents_view).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showProgressResults(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.results_agent_progress_results).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.results_agent_view_results).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void serviceError() {

        showProgress(false);
        showProgressResults(false);

        super.serviceError();
    }

    @Override
    public void networkError() {

        showProgress(false);
        showProgressResults(false);

        super.networkError();
    }

    @Override
    public void removeAgentsTask() {

        agentsReceiveTask = null;
    }

    @Override
    public void removeResultsTask() {

        resultsReceiveTask = null;
    }
}
