package k23b.ac.tasks;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.rest.UserContainer;
import k23b.ac.services.Logger;
import k23b.ac.tasks.status.UsersSendStatus;

public class UsersSendTask extends AsyncTask<Void, Void, UsersSendStatus> {

    public interface UsersSendCallback {

        public void sendSuccess();

        public void serviceError();

        public void networkError();

        public void cancelled();
    }

    private final UsersSendCallback usersSendCallBack;
    private final String baseURI;
    private final UserContainer userContainer;

    public UsersSendTask(UsersSendCallback usersSendCallBack, String baseURI, UserContainer userContainer) {
        super();

        this.usersSendCallBack = usersSendCallBack;
        this.baseURI = baseURI;
        this.userContainer = userContainer;
    }

    @Override
    protected UsersSendStatus doInBackground(Void... params) {

        try {

            // Thread.sleep(5000);

            String url = baseURI + "users/";

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String response = restTemplate.postForObject(url, userContainer, String.class);

            if (response.startsWith("Accepted"))
                return UsersSendStatus.SUCCESS;
            else if (response.startsWith("Service Error"))
                return UsersSendStatus.SERVICE_ERROR;
            else
                return UsersSendStatus.INVALID;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return UsersSendStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(UsersSendStatus status) {
        switch (status) {

        case NETWORK_ERROR:
        case INVALID:
            usersSendCallBack.networkError();
            break;

        case SERVICE_ERROR:
            usersSendCallBack.serviceError();
            break;

        case SUCCESS:
            usersSendCallBack.sendSuccess();
            break;

        case CANCELLED:
        default:
            usersSendCallBack.cancelled();
            break;
        }
    }

    @Override
    protected void onCancelled() {

        usersSendCallBack.cancelled();
    }
}