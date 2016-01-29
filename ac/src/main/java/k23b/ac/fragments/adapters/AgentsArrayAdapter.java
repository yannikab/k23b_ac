package k23b.ac.fragments.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import k23b.ac.R;
import k23b.ac.rest.Agent;
import k23b.ac.rest.status.AgentStatus;

public class AgentsArrayAdapter extends ArrayAdapter<Agent> {

    public AgentsArrayAdapter(Context context, List<Agent> agents) {
        super(context, 0, agents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Agent a = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_agents, parent, false);

        TextView textViewAgentId = (TextView) convertView.findViewById(R.id.rowAgentId);
        TextView textViewRequestHash = (TextView) convertView.findViewById(R.id.rowAgentRequestHash);
        TextView textViewTimeAccepted = (TextView) convertView.findViewById(R.id.rowAgentTimeAccepted);
        TextView textViewTimeJobRequest = (TextView) convertView.findViewById(R.id.rowAgentTimeJobRequest);
        TextView textViewTimeTerminated = (TextView) convertView.findViewById(R.id.rowAgentTimeTerminated);
        TextView textViewAgentStatus = (TextView) convertView.findViewById(R.id.rowAgentStatus);

        convertView.setBackgroundColor(getContext().getResources().getColor(position % 2 == 0 ? R.color.row_back : R.color.row_back_alt));

        textViewAgentId.setText(String.valueOf(a.getAgentId()));
        textViewRequestHash.setText(String.valueOf(a.getShortRequestHash()));
        textViewTimeAccepted.setText(a.getFormattedTimeAccepted());
        textViewTimeJobRequest.setText(a.getFormattedTimeJobRequest());
        textViewTimeTerminated.setText(a.getFormattedTimeTerminated());
        AgentStatus status = a.getAgentStatus();
        textViewAgentStatus.setText(status.toString());
        textViewAgentStatus.setBackgroundColor(getContext().getResources().getColor(status == AgentStatus.ONLINE ? R.color.cell_agent_online : R.color.cell_agent_offline));

        return convertView;
    }
}
