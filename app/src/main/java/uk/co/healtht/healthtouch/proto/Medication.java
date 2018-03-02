package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Medication extends ProtoObject {
    public String uri;
    public boolean active;
    public String title;
    public String description;
    public String message;

    @SerializedName("staff_id")
    public String staffId;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;
}
