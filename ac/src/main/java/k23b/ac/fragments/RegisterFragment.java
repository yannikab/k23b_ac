package k23b.ac.fragments;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import k23b.ac.tasks.UserRegisterTask;
import k23b.ac.util.Logger;
import k23b.ac.util.NetworkManager;
import k23b.ac.util.Settings;

public class RegisterFragment extends Fragment implements UserRegisterTask.RegisterCallback {

    private UserRegisterTask userRegisterTask = null;

    public RegisterFragment() {

        super();
    }

    @Override
    public void onAttach(Activity activity) {

        Logger.info(this.toString(), "onAttach()");

        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText passwordView = (EditText) view.findViewById(R.id.password);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button registerButton = (Button) view.findViewById(R.id.register_button);

        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                attemptRegister();
            }
        });

        showProgress(userRegisterTask != null);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onActivityCreated()");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        Logger.info(this.toString(), "onStart()");

        super.onStart();

        showProgress(userRegisterTask != null);
    }

    @Override
    public void onResume() {

        Logger.info(this.toString(), "onResume()");

        super.onResume();
    }

    @Override
    public void onPause() {

        Logger.info(this.toString(), "onPause()");

        super.onPause();
    }

    @Override
    public void onStop() {

        Logger.info(this.toString(), "onStop()");

        super.onStop();
    }

    public void showProgress(final boolean show) {

        if (getView() == null)
            return;

        getView().findViewById(R.id.register_progress).setVisibility(show ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.register_form).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void clearEditTexts() {

        if (getView() == null)
            return;

        EditText usernameView = (EditText) getView().findViewById(R.id.username);
        EditText passwordView = (EditText) getView().findViewById(R.id.password);
        EditText repeatPasswordView = (EditText) getView().findViewById(R.id.repeat_password);

        usernameView.setError(null);
        passwordView.setError(null);
        repeatPasswordView.setError(null);

        usernameView.setText("");
        passwordView.setText("");
        repeatPasswordView.setText("");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private void attemptRegister() {

        if (userRegisterTask != null)
            return;

        if (getActivity() == null)
            return;

        if (!NetworkManager.networkAvailable(getActivity())) {

            Toast.makeText(getActivity(), getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
            return;
        }

        if (getView() == null)
            return;

        EditText usernameView = (EditText) getView().findViewById(R.id.username);
        EditText passwordView = (EditText) getView().findViewById(R.id.password);
        EditText repeatPasswordView = (EditText) getView().findViewById(R.id.repeat_password);

        usernameView.setError(null);
        passwordView.setError(null);
        repeatPasswordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String repeatPassword = repeatPasswordView.getText().toString();

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
        } else if (TextUtils.isEmpty(repeatPassword)) {
            repeatPasswordView.setError(getString(R.string.error_field_required));
            focusView = repeatPasswordView;
            cancel = true;
        } else if (!password.equals(repeatPassword)) {
            passwordView.setError(getString(R.string.error_passwords_do_not_match));
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

            userRegisterTask = new UserRegisterTask(this, Settings.getBaseURI(), username, hashForPassword(password));

            userRegisterTask.execute((Void) null);
        }
    }

    @Override
    public void registrationSuccess() {

        if (userRegisterTask == null)
            return;

        userRegisterTask = null;

        showProgress(false);

        Logger.info(this.toString(), "Registration success.");

        clearEditTexts();

        Toast.makeText(getActivity(), getString(R.string.info_registration_success), Toast.LENGTH_LONG).show();
    }

    @Override
    public void userExists() {

        if (userRegisterTask == null)
            return;

        userRegisterTask = null;

        Logger.info(this.toString(), "User exists.");

        showProgress(false);

        if (getView() == null)
            return;

        EditText usernameView = (EditText) getView().findViewById(R.id.username);

        usernameView.setError(getString(R.string.error_user_exists));
        usernameView.requestFocus();
    }

    @Override
    public void serviceError() {

        if (userRegisterTask == null)
            return;

        userRegisterTask = null;

        Logger.info(this.toString(), "Service error.");

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_service_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkError() {

        if (userRegisterTask == null)
            return;

        userRegisterTask = null;

        Logger.info(this.toString(), "Network error.");

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_network_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {

        Logger.info(this.toString(), "onDetach()");

        super.onDetach();
    }

    @Override
    public void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        userRegisterTask = null;

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
