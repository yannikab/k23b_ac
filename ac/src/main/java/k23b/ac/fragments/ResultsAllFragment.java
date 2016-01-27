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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.fragments.adapters.ResultsArrayAdapter;
import k23b.ac.rest.Result;
import k23b.ac.rest.User;
import k23b.ac.tasks.ResultsReceiveTask;
import k23b.ac.tasks.ResultsReceiveTask.ResultsReceiveCallback;
import k23b.ac.util.AssetManager;
import k23b.ac.util.InputFilterMinMax;
import k23b.ac.util.Logger;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;
import k23b.ac.util.UserManager;
import k23b.ac.util.XmlTreeConverter;

public class ResultsAllFragment extends Fragment implements ResultsReceiveCallback {

    private ResultsReceiveTask resultsReceiveTask;

    private List<Result> results;

    private Result selectedResult;

    private ActionMode actionMode;

    private String htmlHeaderString;
    private String htmlFooterString;

    private static final String headerAsset = "header.html";
    private static final String footerAsset = "footer.html";

    private static final XmlTreeConverter xmlTreeConverter = new XmlTreeConverter();

    private static final int defaultNumberOfResults = 50;

    boolean initialized = false;

    private int numberOfResults;

    public ResultsAllFragment() {
        super();

        Logger.debug(this.toString(), "Instantiating.");

        htmlHeaderString = AssetManager.loadAsset(headerAsset);
        htmlFooterString = AssetManager.loadAsset(footerAsset);

        numberOfResults = defaultNumberOfResults;
    }

    @Override
    public void onAttach(Activity activity) {

        Logger.debug(this.toString(), "onAttach()");

        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(4);
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

        View view = inflater.inflate(R.layout.fragment_results_all, container, false);

        final EditText results_number_editText = (EditText) view.findViewById(R.id.results_number_editText);
        
        results_number_editText.setFilters(new InputFilter[] { new InputFilterMinMax("1", "999") });
        results_number_editText.setText(String.valueOf(numberOfResults));

        final Button buttonRefreshResults = (Button) view.findViewById(R.id.results_refresh_button);

        buttonRefreshResults.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                fetchResults();
            }
        });

        final ListView resultsListView = (ListView) view.findViewById(R.id.results_listView);

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

        final WebView outputWebView = (WebView) view.findViewById(R.id.webview_results_all);
        
        outputWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        outputWebView.setInitialScale(125);

        WebSettings webSettings = outputWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        final Button buttonExpandAll = (Button) view.findViewById(R.id.button_expand_all);

        buttonExpandAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                outputWebView.loadUrl("javascript:expandTree('tree')");
            }
        });

        final Button buttonCollapseAll = (Button) view.findViewById(R.id.button_collapse_all);

        buttonCollapseAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                outputWebView.loadUrl("javascript:collapseTree('tree')");
            }
        });

        final View outputControls = view.findViewById(R.id.output_controls);
        
        outputControls.setVisibility(View.GONE);

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

        showResults();

        showOutput();

        showProgress(resultsReceiveTask != null);

        if (initialized)
            return;

        initialized = true;

        fetchResults();
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

        inflater.inflate(R.menu.results_all, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        
        case R.id.action_results_all:
            
            fetchResults();
            break;
            
        default:
            
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchResults() {

        if (resultsReceiveTask != null)
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

        EditText results_number_editText = (EditText) getView().findViewById(R.id.results_number_editText);

        int number = 0;

        try {

            number = Integer.valueOf(results_number_editText.getText().toString());

        } catch (NumberFormatException e) {

            return;
        }

        showProgress(true);

        clearOutput();

        resultsReceiveTask = new ResultsReceiveTask(this, Settings.getBaseURI(), u.getUsername(), u.getPassword(), number);

        resultsReceiveTask.execute();
    }

    private void clearOutput() {

        if (getView() == null)
            return;

        final WebView outputWebView = (WebView) getView().findViewById(R.id.webview_results_all);
        outputWebView.loadUrl("about:blank");

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
        
        showProgress(false);

        if (getActivity() == null)
            return;

        Toast.makeText(getActivity(), results.size() + (results.size() == 1 ? " result" : " results") + " received", Toast.LENGTH_LONG).show();
    }

    private void showResults() {

        if (getView() == null)
            return;

        ListView resultsListView = (ListView) getView().findViewById(R.id.results_listView);

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

        String content = xmlTreeConverter.stringForXml(selectedResult.getJobResult());

        String htmlString = htmlHeaderString + content + htmlFooterString;

        final WebView outputWebView = (WebView) getView().findViewById(R.id.webview_results_all);
        
        outputWebView.loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html", "utf-8", null);
    }

    private void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.results_all_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.results_all_view).setVisibility(show ? View.GONE : View.VISIBLE);
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
    public void removeResultsTask() {

        this.resultsReceiveTask = null;
    }
}
