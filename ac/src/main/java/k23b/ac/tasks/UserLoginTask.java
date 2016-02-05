package k23b.ac.tasks;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.services.Logger;
import k23b.ac.tasks.status.UserLoginStatus;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, UserLoginStatus> {

    public interface LoginCallback extends TaskCallback {

        public void loginSuccess();
        
        public void removeLoginTask();
    }

    private final LoginCallback loginCallback;
    private final String baseURI;
    private final String username;
    private final String password;

    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }

    public UserLoginTask(LoginCallback loginCallback, String baseURI, String username, String password) {

        this.loginCallback = loginCallback;

        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected UserLoginStatus doInBackground(Void... params) {

        try {

            String url = baseURI + "login/" + username + "/" + password;

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String result = restTemplate.getForObject(url, String.class);

            try {

                Thread.sleep(2000);

            } catch (InterruptedException e) {
                // e.printStackTrace();
            }

            if (result.equals("Login Success"))
                return UserLoginStatus.LOGIN_SUCCESS;

            if (result.equals("Incorrect Credentials"))
                return UserLoginStatus.INCORRECT_CREDENTIALS;

            if (result.equals("Registration Pending"))
                return UserLoginStatus.REGISTRATION_PENDING;

            if (result.equals("Service Error"))
                return UserLoginStatus.SERVICE_ERROR;

            return UserLoginStatus.INVALID;

        } catch (RestClientException e) {
            
            Logger.logException(getClass().getSimpleName(), e);
            
            return UserLoginStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final UserLoginStatus status) {

        switch (status) {

        case LOGIN_SUCCESS:
            
            loginCallback.loginSuccess();
            break;

        case INCORRECT_CREDENTIALS:
            
            loginCallback.incorrectCredentials();
            break;

        case REGISTRATION_PENDING:
            
            loginCallback.registrationPending();
            break;

        case NETWORK_ERROR:
            
            loginCallback.networkError();
            break;
            
        case SERVICE_ERROR:
            
            loginCallback.serviceError();
            break;

        default:
            
            loginCallback.serviceError();
            break;
        }
        
        loginCallback.removeLoginTask();
    }

    @Override
    protected void onCancelled() {

        loginCallback.removeLoginTask();
    }
}