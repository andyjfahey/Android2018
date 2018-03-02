package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Paginator implements Serializable{
    public int total;
    @SerializedName("per_page")
    public int perPage;
    @SerializedName("current_page")
    public int currentPage;
    @SerializedName("last_page")
    public int lastPage;
    public int from;
    public int to;
}
