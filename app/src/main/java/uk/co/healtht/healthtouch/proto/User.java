package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class User extends ProtoObject {
    public String uri;
    public String id;
    public String email;
    public String name;
    public String surname;
    public String gender;
    public String username;
    @SerializedName("dateofbirth")
    public String dateOfBirth;
    public int age;
    public String type;
    public String address;
    public String city;
    public String nhs;
    public String postcode;
    public String active;

    @SerializedName("app_version")
    public String appVersion;

    @SerializedName("firstlogin")
    public Object firstLogin; // WTF - We get a boolean when creating an account, and a number when requesting the user
    public Object tags; // TODO: What is this for?
    public List<Medication> medications = new ArrayList<>();
    //public Map<String, TrackerUser> monitoring = new HashMap<>(); // OLD server
    public List<TrackerUser> monitoring = new ArrayList<>();

    public TrackerUser getTrackerUser(String uri) {
        for (TrackerUser trackerUser : monitoring) {
            if (uri.equals(trackerUser.uri)) {
                return trackerUser;
            }
        }
        return null;
    }

    public Medication getMedication(String uri) {
        if (!uri.startsWith("/medications/")) {
            uri = "/medications/" + uri;
        }

        for (Medication medication : medications) {
            if (uri.equals(medication.uri)) {
                return medication;
            }
        }
        return null;
    }

    public void updateFromUserPost(UserPost userPost) {
        if (userPost.address != null)
            this.address = userPost.address;
        if (userPost.city != null)
            this.city = userPost.city;
        this.email = userPost.email;
        this.name = userPost.name;
        this.surname = userPost.surname;
        if (userPost.gender != null)
            this.gender = userPost.gender;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (userPost.dateOfBirth != null)
            this.dateOfBirth = df.format(userPost.dateOfBirth);
        if (userPost.postcode != null)
            this.postcode = userPost.postcode;
        if (userPost.nhs != null)
            this.nhs = userPost.nhs;
    }
}
