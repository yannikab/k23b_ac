package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "users")
public class UserContainer {

    @ElementList(inline = true, required = false)
    private List<User> users;

    public UserContainer() {

        this.users = new ArrayList<User>();
    }

    public List<User> getUsers() {
        return users;
    }
}
