package k23b.ac.rest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import k23b.ac.Logger;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, UserLoginStatus> {

    public interface LoginCallback {

        public void loginSuccess();

        public void incorrectCredentials();

        public void networkError();

        public void cancelled();
    }

    private final LoginCallback loginCallback;
    private final String baseURI;
    private final String username;
    private final String password;

    public UserLoginTask(LoginCallback loginCallback, String baseURI, String username, String password) {

        this.loginCallback = loginCallback;

        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    @Override
    protected UserLoginStatus doInBackground(Void... params) {

        try {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }

            String url = baseURI + "login/" + username + "/" + hashForPassword(password);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String result = restTemplate.getForObject(url, String.class);

            if (result.startsWith("Accepted"))
                return UserLoginStatus.SUCCESS;
            else if (result.startsWith("Incorrect"))
                return UserLoginStatus.INCORRECT_CREDENTIALS;
            else
                return UserLoginStatus.INVALID;
            
        } catch (RestClientException e) {
            Logger.logThrowable(getClass().getSimpleName(), e);
            return UserLoginStatus.NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final UserLoginStatus status) {

        switch (status) {

        case INCORRECT_CREDENTIALS:
            loginCallback.incorrectCredentials();
            break;

        case NETWORK_ERROR:
        case INVALID:
            loginCallback.networkError();
            break;

        case SUCCESS:
            loginCallback.loginSuccess();
            break;

        case CANCELLED:
        default:
            loginCallback.cancelled();
            break;
        }
    }

    @Override
    protected void onCancelled() {

        loginCallback.cancelled();
    }

    private String hashForPassword(String password) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());

            byte[] digest = md.digest();

            return bytesToHex(digest);

        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
            return "";
        }
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}