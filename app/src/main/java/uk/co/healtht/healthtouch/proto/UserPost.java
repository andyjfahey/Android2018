package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserPost extends ProtoObject {
    public String email;
    public String name;
    public String surname;
    public String gender;
    public String username;
    @SerializedName("dateofbirth")
    public Date dateOfBirth;
    public String type;
    public String password;

    public String address;
    public String city;
    public String nhs;
    public String postcode;
}
