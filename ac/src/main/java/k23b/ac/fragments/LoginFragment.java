package k23b.ac.fragments;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import k23b.ac.R;
import k23b.ac.activities.MainActivity;
import k23b.ac.activities.RegisterActivity;
import k23b.ac.rest.User;
import k23b.ac.services.Logger;
import k23b.ac.services.NetworkManager;
import k23b.ac.services.UserManager;
import k23b.ac.tasks.UserLoginTask;
import k23b.ac.util.Settings;

public class LoginFragment extends Fragment implements UserLoginTask.LoginCallback {

    private UserLoginTask userLoginTask = null;

    public LoginFragment() {

        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText passwordView = (EditText) view.findViewById(R.id.password);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button logInButton = (Button) view.findViewById(R.id.log_in_button);

        logInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        Button registerButton = (Button) view.findViewById(R.id.register_button);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                startRegisterActivity();
            }
        });

        return view;
    }

    private void startRegisterActivity() {

        if (getActivity() == null)
            return;

        if (!NetworkManager.isNetworkAvailable()) {

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        clearEditTexts();

        Intent intent = new Intent(getActivity(), RegisterActivity.class);

        getActivity().startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        showProgress(userLoginTask != null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.login_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.login_form).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private void attemptLogin() {

        if (userLoginTask != null)
            return;

        if (getActivity() == null)
            return;

        if (!NetworkManager.isNetworkAvailable()) {

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        if (getView() == null)
            return;

        EditText usernameView = (EditText) getView().findViewById(R.id.username);
        EditText passwordView = (EditText) getView().findViewById(R.id.password);

        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();

        } else {

            View view = getActivity().getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            showProgress(true);

            userLoginTask = new UserLoginTask(this, Settings.getBaseURI(), username, hashForPassword(password));

            userLoginTask.execute((Void) null);
        }
    }

    @Override
    public void loginSuccess() {

        if (userLoginTask == null)
            return;

        Logger.info(this.toString(), "Log in success, storing user in shared preferences and starting main activity.");

        storeUser(userLoginTask.getUsername(), userLoginTask.getPassword());

        userLoginTask = null;

        startMainActivity();
    }

    private void clearEditTexts() {

        if (getView() == null)
            return;

        EditText usernameView = (EditText) getView().findViewById(R.id.username);
        EditText passwordView = (EditText) getView().findViewById(R.id.password);

        usernameView.setError(null);
        passwordView.setError(null);

        usernameView.setText("");
        passwordView.setText("");
    }

    private void storeUser(String username, String password) {

        if (getActivity() == null)
            return;

        UserManager.getInstance().storeUser(new User(username, password));
    }

    private void startMainActivity() {

        if (getActivity() == null)
            return;

        clearEditTexts();

        Intent intent = new Intent(getActivity(), MainActivity.class);

        startActivity(intent);
    }

    @Override
    public void registrationPending() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Registration pending.");

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_registration_pending), Toast.LENGTH_LONG).show();
    }

    @Override
    public void incorrectCredentials() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Incorrect credentials.");

        showProgress(false);

        if (getView() == null)
            return;

        EditText passwordView = (EditText) getView().findViewById(R.id.password);

        passwordView.setError(getString(R.string.error_incorrect_credentials));
        passwordView.requestFocus();
    }

    @Override
    public void serviceError() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Service error.");

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_service_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkError() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        Logger.info(this.toString(), "Network error.");

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_network_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onDestroy() {

        userLoginTask = null;

        super.onDestroy();
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
