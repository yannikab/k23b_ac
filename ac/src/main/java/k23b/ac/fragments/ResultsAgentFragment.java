package k23b.ac.fragments;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import k23b.ac.fragments.adapters.AgentsArrayAdapter;
import k23b.ac.fragments.adapters.ResultsArrayAdapter;
import k23b.ac.rest.Agent;
import k23b.ac.rest.Result;
import k23b.ac.rest.User;
import k23b.ac.tasks.AgentsReceiveTask;
import k23b.ac.tasks.AgentsReceiveTask.AgentsReceiveCallback;
import k23b.ac.tasks.ResultsAgentReceiveTask;
import k23b.ac.tasks.ResultsAgentReceiveTask.ResultsAgentReceiveCallback;
import k23b.ac.util.InputFilterMinMax;
import k23b.ac.util.Logger;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;
import k23b.ac.util.UserManager;
import k23b.ac.util.WebViewManager;

public class ResultsAgentFragment extends Fragment implements AgentsReceiveCallback, ResultsAgentReceiveCallback {

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

        case R.id.action_results_agent:

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

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        showProgress(true);

        clearOutput();

        agentsReceiveTask = new AgentsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword());

        agentsReceiveTask.execute();
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

        Toast.makeText(getActivity(), agents.size() + (agents.size() == 1 ? " agent" : " agents") + " received", Toast.LENGTH_LONG).show();
    }

    private void showAgents() {

        if (getView() == null)
            return;

        ListView agentsListView = (ListView) getView().findViewById(R.id.results_agent_listView_agents);

        agentsListView.setAdapter(agents == null ? null : new AgentsArrayAdapter(getActivity(), this.agents));
    }

    private void fetchResults() {

        if (resultsReceiveTask != null)
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

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
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

        Toast.makeText(getActivity(), results.size() + (results.size() == 1 ? " result" : " results") + " received", Toast.LENGTH_LONG).show();
    }

    private void showResults() {

        if (getView() == null)
            return;

        TextView resultsTextView = (TextView) getView().findViewById(R.id.results_agent_textView_results);

        resultsTextView.setText(selectedAgent == null ? "" : "Results for Agent " + selectedAgent.getShortRequestHash());
        
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

        getView().findViewById(R.id.results_agent_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.results_agent_view).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showProgressResults(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.results_agent_progress_results).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.results_agent_view_results).setVisibility(show ? View.GONE : View.VISIBLE);
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

        agentsReceiveTask = null;
    }

    @Override
    public void removeResultsTask() {

        resultsReceiveTask = null;
    }
}
