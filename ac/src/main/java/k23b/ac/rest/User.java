package k23b.ac.rest;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name = "user")
public class User implements Parcelable {

    @Attribute(required = true)
    private String username;

    @Attribute(required = true)
    private String password;

    @ElementList(inline = true, required = false)
    private List<Job> jobs;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(username);
        dest.writeString(password);
        dest.writeTypedList(jobs);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };

    public User(Parcel source) {

        this(source.readString(), source.readString());
        source.readTypedList(this.jobs, Job.CREATOR);

    }

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
