package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

public class CareServiceOwner extends ProtoObject {
    public String uri;
    public String email;
    public String name;
    public String surname;
    public String gender;

    @SerializedName("app_version")
    public String appVersion;
}
