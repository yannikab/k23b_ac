package k23b.sa.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import k23b.sa.Job;

/**
 * A list for the compact distribution of a batch of Jobs to a SA
 */

@XmlRootElement(name = "jobs")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobContainer {

    @XmlAttribute(required = true)
    private String status;

    @XmlElement(name = "job", required = false)
    private List<Job> Jobs;

    public JobContainer(String status) {

        this.status = status == null ? "" : status;

        this.Jobs = new ArrayList<Job>();
    }

    public JobContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Job> getJobs() {
        return Jobs;
    }
}
