package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "user")
public class User {

    @Attribute(required = true)
    private String username;

    @Attribute(required = true)
    private String password;

    @ElementList(inline = true, required = false)
    private List<Job> jobs;
	
    public User(String username, String password) {

        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;

        this.jobs = new ArrayList<Job>();
    }

    public User() {
        
        this(null, null);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
