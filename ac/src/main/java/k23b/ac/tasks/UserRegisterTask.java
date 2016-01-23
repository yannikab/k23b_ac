package k23b.ac.tasks;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.Logger;

/**
 * Represents an asynchronous registration task used to register the user.
 */
public class UserRegisterTask extends AsyncTask<Void, Void, UserRegisterStatus> {

    public interface RegisterCallback {

        public void registrationSuccess();

        public void userExists();

        public void serviceError();

        public void networkError();
    }

    private final RegisterCallback registerCallback;
    private final String baseURI;
    private final String username;
    private final String password;

    public UserRegisterTask(RegisterCallback registerCallback, String baseURI, String username, String password) {

        this.registerCallback = registerCallback;

        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected UserRegisterStatus doInBackground(Void... params) {

        try {

            String url = baseURI + "register/" + username + "/" + password;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String result = restTemplate.getForObject(url, String.class);

            try {

                Thread.sleep(5000);

            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
            
            if (result.startsWith("Accepted"))
                return UserRegisterStatus.REGISTRATION_SUCCESS;
            else if (result.startsWith("User Exists"))
                return UserRegisterStatus.USER_EXISTS;
            else
                return UserRegisterStatus.SERVICE_ERROR;

        } catch (RestClientException e) {
            Logger.logException(getClass().getSimpleName(), e);
            return UserRegisterStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final UserRegisterStatus status) {

        switch (status) {

        case REGISTRATION_SUCCESS:
            registerCallback.registrationSuccess();
            break;

        case USER_EXISTS:
            registerCallback.userExists();
            break;

        case SERVICE_ERROR:
            registerCallback.serviceError();

        case NETWORK_ERROR:
            registerCallback.networkError();
            break;

        default:
            break;
        }
    }
}