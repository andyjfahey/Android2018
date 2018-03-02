package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;

public class TrackerEntry extends ProtoObject {
    public String uri;
    public String notes;
    @SerializedName("updated_at")
    public Date updatedAt;
    public HashMap<String, Double> fields;
}
