package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class TrackerUser extends ProtoObject {
    public String uri;

    public boolean enabled;
    public boolean overdue;

    @SerializedName("out_threshold")
    public boolean outThreshold;

    public HashMap<String, ThresholdRange> thresholds;
}
