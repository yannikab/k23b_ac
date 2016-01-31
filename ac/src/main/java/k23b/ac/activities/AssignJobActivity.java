package k23b.ac.activities;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import k23b.ac.R;
import k23b.ac.rest.Job;
import k23b.ac.rest.User;
import k23b.ac.services.JobDispatcher;
import k23b.ac.services.Logger;
import k23b.ac.services.UserManager;

public class AssignJobActivity extends Activity {

    long agentId;

    EditText editTextParams;

    RadioButton radioPeriodicNo;
    RadioButton radioPeriodicYes;

    EditText editTextPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.info(this.toString(), "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assign_job);

        Intent intent = getIntent();

        agentId = intent.getLongExtra("agentId", -1);

        String requestHash = intent.getStringExtra("requestHash");

        ((TextView) findViewById(R.id.assign_job_hash)).setText(requestHash);

        editTextParams = (EditText) findViewById(R.id.assign_job_params);

        radioPeriodicNo = (RadioButton) findViewById(R.id.assign_job_radio_periodic_no);
        radioPeriodicYes = (RadioButton) findViewById(R.id.assign_job_radio_periodic_yes);

        editTextPeriod = (EditText) findViewById(R.id.assign_job_period);

        radioPeriodicNo.setChecked(true);
        radioPeriodicYes.setChecked(false);

        editTextPeriod.setEnabled(false);
        editTextPeriod.setText("300");
    }

    @Override
    protected void onDestroy() {

        Logger.info(this.toString(), "onDestroy()");

        super.onDestroy();
    }

    public void onRadioButtonClicked(View v) {

        switch (v.getId()) {
        case R.id.assign_job_radio_periodic_no:
            ((TextView) findViewById(R.id.assign_job_period)).setEnabled(false);
            return;
        case R.id.assign_job_radio_periodic_yes:
            ((TextView) findViewById(R.id.assign_job_period)).setEnabled(true);
            return;
        default:
            break;
        }
    }

    public void onOkPressed(View v) {

        User u = UserManager.getInstance().getStoredUser();

        if (u == null) {

            Logger.info(this.toString(), "No user logged in, aborting activity.");
            finish();
            return;
        }

        Job j = new Job();

        j.setAgentId(agentId);
        j.setTimeAssigned(new Date(System.currentTimeMillis()));
        j.setParams(editTextParams.getText().toString());
        j.setPeriodic(radioPeriodicYes.isChecked());
        j.setPeriod(j.getPeriodic() ? Integer.valueOf(editTextPeriod.getText().toString()) : 0);

        u.getJobs().add(j);

        JobDispatcher.getInstance().dispatch(this, u);

        Intent data = new Intent();
        setResult(RESULT_OK, data);

        closeSoftKeyboard();

        finish();
    }

    public void onCancelPressed(View v) {

        setResult(RESULT_CANCELED);

        closeSoftKeyboard();

        finish();
    }

    private void closeSoftKeyboard() {

        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
