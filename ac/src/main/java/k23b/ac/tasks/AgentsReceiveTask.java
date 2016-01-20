package k23b.ac.tasks;

import java.util.Collections;
import java.util.List;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.Logger;
import k23b.ac.rest.Agent;
import k23b.ac.rest.AgentContainer;

public class AgentsReceiveTask extends AsyncTask<Void, Void, List<Agent>> {

    public interface AgentsReceiver {
        public void setAgents(List<Agent> agents);
    }

    private final AgentsReceiver receiver;
    private final String baseURI;
    private final String username;
    private final String password;

    public AgentsReceiveTask(AgentsReceiver receiver, String baseURI, String username, String password) {
        super();
        this.receiver = receiver;
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected List<Agent> doInBackground(Void... params) {

        try {

            // Thread.sleep(5000);

            String url = baseURI + "agents/" + username + "/" + password;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            AgentContainer agentContainer = restTemplate.getForObject(url, AgentContainer.class);

            Collections.sort(agentContainer.getAgents());

            return agentContainer.getStatus().equals("Accepted") ? agentContainer.getAgents() : null;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(final List<Agent> agents) {

        receiver.setAgents(agents);
    }

    @Override
    protected void onCancelled() {

        receiver.setAgents(null);
    }
}
