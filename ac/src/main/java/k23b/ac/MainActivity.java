package k23b.ac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String LOGIN_ACTION = "k23b.ac.LOGIN_ACTION";
    private static final int LOGIN_REQUEST = 1;

    private TextView mTextView;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_view);

        startLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(k23b.ac.R.menu.main, menu);
        return true;
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent();
        loginIntent.setAction(LOGIN_ACTION);
        startActivityForResult(loginIntent, LOGIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

        case LOGIN_REQUEST:

            if (resultCode != RESULT_OK) {
                mTextView.setText("");
                break;
            }

            Bundle bundle = data.getExtras();

            String username = (String) bundle.get("username");

            mTextView.setText(username);

            break;

        default:
            break;
        }
    }
}
