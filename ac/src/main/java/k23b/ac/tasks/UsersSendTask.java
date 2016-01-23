package k23b.ac.tasks;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.UserContainer;
import k23b.ac.util.Logger;

public class UsersSendTask extends AsyncTask<Void, Void, Boolean> {

    private final String baseURI;
    private final UserContainer userContainer;

    public UsersSendTask(String baseURI, UserContainer userContainer) {
        super();

        this.baseURI = baseURI;
        this.userContainer = userContainer;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {

            // Thread.sleep(5000);

            String url = baseURI + "users/";

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String response = restTemplate.postForObject(url, userContainer, String.class);

            return response.startsWith("Accepted");

        } catch (RestClientException e) {
            // e.printStackTrace();
            Logger.logException(getClass().getSimpleName(), e);

            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        return;
    }

    @Override
    protected void onCancelled() {
        return;
    }
}
