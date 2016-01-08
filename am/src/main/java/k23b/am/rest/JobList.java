package k23b.am.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * 	A list for the compact distribution of a batch of Jobs to a SA 
 */

@XmlRootElement(name = "jobs")
@XmlSeeAlso({ Job.class })
@XmlAccessorType(XmlAccessType.NONE)
public class JobList {

    private List<Job> jobList;
    private String status;

    public JobList() {

        jobList = null;
        status = null;
    }

    @XmlAttribute(required = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement(required = true)
    public List<Job> getJob() {
        return jobList;
    }

    public void setJob(List<Job> jobList) {
        this.jobList = jobList;
    }
}
