package uk.co.healtht.healthtouch.proto;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Notification extends ProtoObject {
    public String uri;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("monitor_id")
    public String monitorId;

    public String type;

    public boolean seen;

    public boolean overdue;

    public String link;  // healthtouch://medications/303,

    @SerializedName("deleted_at")
    public Date deletedAt;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;

    @SerializedName("due_date")
    public Date dueDate;

    public String message;

    public String getRelativeDate() {

        String dateStr;
        long duration  = System.currentTimeMillis() - createdAt.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        TimeZone timeZone = TimeZone.getDefault();
        long timeMilli = ((createdAt.getTime() > System.currentTimeMillis()) ?
                System.currentTimeMillis() : createdAt.getTime()) + timeZone.getRawOffset();

        if (diffInDays >= 7){
            // Nov 10, hh:mm AM/PM
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, hh:mm aa");
            dateStr = formatter.format(new Date(timeMilli));
        }
        else{
            CharSequence relativeTimeSeconds = DateUtils.getRelativeTimeSpanString(
                    timeMilli, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            dateStr = relativeTimeSeconds.toString();
        }
        return dateStr;
        //return DateUtils.getRelativeTimeSpanString(createdAt.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }
}
