package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Monitor extends ProtoObject {
    public String uri;
    @SerializedName("tracker_uri")
    public String trackerUri;
    public boolean enabled;
    @SerializedName("updated_at")
    public Date updatedAt;

    /**
     * format ex: 0 9 8 5 *
     * [0] - min (0-59)
     * [1] - hour (0-23)
     * [2] - day (1-31)
     * [3] - month (1-12)
     * [4] - day of week (0-6 0=Sunday)
     */
    public String cron;
}
