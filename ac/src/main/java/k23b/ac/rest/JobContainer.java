package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Annotated class for a list of Job entities along with the Agent status for which they are meant.
 *
 */
@Root(name = "jobs")
public class JobContainer {

    @Attribute(required = true)
    private String status;

    @ElementList(inline = true, required = false)
    private List<Job> jobs;

    public JobContainer(String status) {

        this.status = status == null ? "" : status;

        this.jobs = new ArrayList<Job>();
    }

    public JobContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
