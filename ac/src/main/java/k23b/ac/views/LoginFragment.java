package k23b.ac.views;

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
import k23b.ac.MainActivity;
import k23b.ac.R;
import k23b.ac.Settings;
import k23b.ac.tasks.UserLoginTask;

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

        Button signInButton = (Button) view.findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        showProgress(userLoginTask != null);

        return view;
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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            showProgress(true);

            userLoginTask = new UserLoginTask(this, Settings.getBaseURI(), username, password);

            userLoginTask.execute((Void) null);
        }
    }

    @Override
    public void loginSuccess() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        startMainActivity();
    }

    private void startMainActivity() {

        if (getActivity() == null)
            return;

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.putExtra("user", "Yannis Kabilafkas");
        startActivity(mainIntent);
    }

    @Override
    public void registrationPending() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_registration_pending), Toast.LENGTH_LONG).show();
    }

    @Override
    public void incorrectCredentials() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        showProgress(false);

        if (getView() == null)
            return;

        EditText passwordView = (EditText) getView().findViewById(R.id.password);

        passwordView.setError(getString(R.string.error_incorrect_credentials));
        passwordView.requestFocus();
    }

    @Override
    public void networkError() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_network_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void cancelled() {

        if (userLoginTask == null)
            return;

        userLoginTask = null;

        showProgress(false);

        Toast.makeText(getActivity(), getString(R.string.error_login_cancelled), Toast.LENGTH_LONG).show();
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
}
