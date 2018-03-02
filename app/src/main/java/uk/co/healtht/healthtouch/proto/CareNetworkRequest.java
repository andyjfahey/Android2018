package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CareNetworkRequest extends ProtoObject {
    public String uri;

    @SerializedName("shares_with")
    public String sharesWith;

    @SerializedName("shares_with_service")
    public String sharesWithService;

    public String seen;
    public boolean accepted;
    public String originator;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;

    public User user;
    public CareService service;

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
