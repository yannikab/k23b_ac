package k23b.ac.views;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import k23b.ac.MainActivity;
import k23b.ac.R;
import k23b.ac.Settings;
import k23b.ac.rest.AgentContainer;

public class AgentsFragment extends Fragment {

    private AgentsFetchTask agentsFetchTask;

    @SuppressWarnings("unused")
    private AgentContainer agentContainer;

    private View agentsProgressView;

    private View agentsSectionView;

    public AgentsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            return;

        // state = savedInstanceState.getInt("state");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agents, container, false);

        Button button = (Button) view.findViewById(R.id.agents_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                agentContainer = null;

                showProgress(true);

                agentsFetchTask = new AgentsFetchTask("Yannis", "36BBE50ED96841D10443BCB670D6554F0A34B761BE67EC9C4A8AD2C0C44CA42C");

                agentsFetchTask.execute();
            }
        });

        agentsProgressView = view.findViewById(R.id.agents_progress);
        agentsSectionView = view.findViewById(R.id.agents_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showProgress(final boolean show) {
        // simply show and hide the relevant UI components.
        agentsProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        agentsSectionView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public class AgentsFetchTask extends AsyncTask<Void, Void, AgentContainer> {

        private final String mUsername;
        private final String mPassword;

        AgentsFetchTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected AgentContainer doInBackground(Void... params) {

            try {

                // Thread.sleep(500);

                String url = Settings.getBaseURI() + "agents/" + mUsername + "/" + mPassword;

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

                AgentContainer agentContainer = restTemplate.getForObject(url, AgentContainer.class);

                return agentContainer;

            } catch (Exception e) {
                logException(getLocalClassName(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AgentContainer agentContainer) {

            AgentsFragment.this.agentsFetchTask = null;
            AgentsFragment.this.agentContainer = agentContainer;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {

            AgentsFragment.this.agentsFetchTask = null;
            AgentsFragment.this.agentContainer = null;
            showProgress(false);
        }
    }

    private void logException(String tag, Exception e) {

        if (e.getMessage() != null) {
            Log.e(tag, e.getMessage());
            return;
        }

        for (StackTraceElement ste : e.getStackTrace())
            Log.e(getLocalClassName(), ste.toString());
    }

    public String getLocalClassName() {

        return "AgentsFragment";
    }
}
