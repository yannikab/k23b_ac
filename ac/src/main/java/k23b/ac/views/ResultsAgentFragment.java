package k23b.ac.views;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import k23b.ac.MainActivity;
import k23b.ac.R;

public class ResultsAgentFragment extends Fragment {

    public ResultsAgentFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_results_agent, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        ((MainActivity) activity).onSectionAttached(3);
    }
}