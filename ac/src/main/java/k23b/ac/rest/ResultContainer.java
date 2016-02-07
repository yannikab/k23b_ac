package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Annotated class for a list of Result entities along with the Agent status for which the corresponding jobs are associated.
 *
 */
@Root(name = "results")
public class ResultContainer {

    @Attribute(required = true)
    private String status;

    @ElementList(inline = true, required = false)
    private List<Result> results;

    public ResultContainer(String status) {

        this.status = status == null ? "" : status;

        this.results = new ArrayList<Result>();
    }

    public ResultContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Result> getResults() {
        return this.results;
    }
}