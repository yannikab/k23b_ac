package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Annotated class for a list of Agent entities along with a common Agent status
 *
 */
@Root(name = "agents")
public class AgentContainer {

    @Attribute(required = true)
    private String status;

    @ElementList(inline = true, required = false)
    private List<Agent> agents;

    public AgentContainer(String status) {

        this.status = status == null ? "" : status;

        this.agents = new ArrayList<Agent>();
    }

    public AgentContainer() {

        this(null);
    }

    public String getStatus() {
        return status;
    }

    public List<Agent> getAgents() {
        return agents;
    }
}
