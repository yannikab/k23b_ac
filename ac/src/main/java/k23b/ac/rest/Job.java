package k23b.ac.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import k23b.ac.rest.status.JobStatus;

/**
 *  Annotated class for the Job entity 
 *
 */
@Root(name = "job")
public class Job implements Comparable<Job>, Parcelable {

    @Element(required = true)
    protected long jobId;

    @Element(required = true)
    protected long agentId;

    @Element(required = true)
    protected long adminId;

    @Element(required = true)
    protected Date timeAssigned;

    @Element(required = false)
    protected Date timeSent;

    @Element(required = false)
    protected String params;

    @Element(required = true)
    protected boolean periodic;

    @Element(required = false)
    protected int period;

    @Element(required = false)
    protected Date timeStopped;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(agentId);
        dest.writeLong(timeAssigned.getTime());
        dest.writeString(params);
        dest.writeByte((byte) (periodic ? 1 : 0));
        dest.writeInt(period);
        
    }

    public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>() {

        @Override
        public Job createFromParcel(Parcel source) {
            return new Job(source);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }

    };

    public Job(Parcel source) {

        jobId = -1;
        agentId = source.readLong();
        adminId = -1;
        timeAssigned = new Date(source.readLong());
        timeSent = new Date();
        params = source.readString();
        periodic = (source.readByte() != 0);
        period = source.readInt();
        timeStopped = new Date();

    }

    public Job() {
        super();
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
    }

    public void setTimeAssigned(Date timeAssigned) {
        this.timeAssigned = timeAssigned;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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

    public Date getTimeStopped() {
        return timeStopped;
    }

    public void setTimeStopped(Date timeStopped) {
        this.timeStopped = timeStopped;
    }

    public JobStatus getStatus() {

        if (this.getPeriodic() && this.getTimeStopped() != null)
            return JobStatus.STOPPED;
        else if (this.getTimeSent() != null)
            return JobStatus.SENT;
        else
            return JobStatus.ASSIGNED;
    }

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String getFormattedTimeAssigned() {

        if (timeAssigned == null)
            return "-";

        return dateFormat.format(timeAssigned);
    }

    public String getFormattedTimeSent() {

        if (timeSent == null)
            return "-";

        return dateFormat.format(timeSent);
    }

    public String getFormattedTimeStopped() {

        if (timeStopped == null)
            return "-";

        return dateFormat.format(timeStopped);
    }

    @Override
    public String toString() {
        return "Job [jobId=" + jobId + ", agentId=" + agentId + ", adminId=" + adminId + ", timeAssigned="
                + timeAssigned + ", timeSent=" + timeSent + ", params=" + params + ", periodic=" + periodic
                + ", period=" + period + ", timeStopped=" + timeStopped + "]";
    }

    @Override
    public int compareTo(Job that) {

        if (this.equals(that))
            return 0;

        if (this.jobId > that.jobId)
            return -1;
        else if (this.jobId < that.jobId)
            return 1;
        else
            return 0;
    }
}