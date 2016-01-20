package k23b.ac.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.util.Log;


public class Job {
	
	private int id;
	private String parameters;
	private String username;
	private int agentId;
	private Date time_assigned;
	private boolean periodic;
	private int period;
	
	public Job(int id, String parameters, String username, int agentId, Date time_assigned, boolean periodic, int period) {
		this.id = id;
		this.parameters = parameters;
		this.username = username;
		this.agentId = agentId;
		this.time_assigned = time_assigned;
		this.periodic = periodic;
		this.period = period;
	}
	
	@SuppressLint("SimpleDateFormat")
	public Job(int id, String parameters, String username, int agentId, String time_assigned, boolean periodic, int period) {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.id = id;
		this.parameters = parameters;
		this.username = username;
		this.agentId = agentId;
		try {
			this.time_assigned = format.parse(time_assigned);
		} catch (ParseException e) {
			Log.e(Job.class.getName(), "Parse error on Date parsing");
		}
		this.periodic = periodic;
		this.period = period;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public Date getTime_assigned() {
		return time_assigned;
	}

	public void setTime_assigned(Date time_assigned) {
		this.time_assigned = time_assigned;
	}

	public boolean getPeriodic() {
		return periodic;
	}

	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", parameters=" + parameters + ", username=" + username + ", agentId=" + agentId
				+ ", time_assigned=" + time_assigned + ", periodic=" + periodic + ", period=" + period + "]";
	}

}
