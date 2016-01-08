package k23b.am.rest;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * 	Serves the purpose of sending the Job info from the AM to the SA.
 */

@XmlRootElement(name = "job")
@XmlType(name = "job", propOrder = {
        "id",
        "cmd",
        "isPeriodic",
        "period"
})
@XmlAccessorType(XmlAccessType.NONE)
public class Job implements Comparable<Job> {

    @XmlElement(required = true)
    private long id;
    @XmlElement(required = true)
    private String cmd;
    @XmlElement(required = true)
    private boolean isPeriodic;
    @XmlElement(required = true)
    private int period;

    public Job() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPeriodic() {
        return isPeriodic;
    }

    public void setPeriodic(boolean isPeriodic) {
        this.isPeriodic = isPeriodic;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public int compareTo(Job o) {
        if (id < o.getId())
            return -1;
        else if (id == o.getId())
            return 0;
        else
            return 1;
    }

    @XmlTransient
    static class JobComparator implements Comparator<Job> {
        public int compare(Job c1, Job c2) {
            return c1.compareTo(c2);
        }
    }

    @Override
    public String toString() {
        return "Job [id=" + id + ", cmd=" + cmd + ", isPeriodic=" + isPeriodic + ", period=" + period + "]";
    }
}
