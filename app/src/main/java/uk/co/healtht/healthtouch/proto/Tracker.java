package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tracker extends ProtoObject {
    public String uri;
    public String name;
    public String description;
    public int active;
    @SerializedName("deleted_at")
    public Date deletedAt;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("updated_at")
    public Date updatedAt;
    @SerializedName("tracker_type")
    public String trackerType;
    @SerializedName("fields_type")
    public String fieldsType;

    @SerializedName("fields_info")
    public List<TrackerInfo> fieldsInfo = new ArrayList<>();

    public List<Monitor> monitors = new ArrayList<>();

}
