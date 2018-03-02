package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CareService extends ProtoObject {

    public String uri;
    public String name;
    public String comments;

    @SerializedName("deleted_at")
    public Date deletedAt;

    @SerializedName("updated_at")
    public Date updatedAt;

    @SerializedName("no_users")
    public int noUsers;

    @SerializedName("number_users")
    public int numberUsers;

    public List<CareServiceOwner> owners = new ArrayList<>();

}
