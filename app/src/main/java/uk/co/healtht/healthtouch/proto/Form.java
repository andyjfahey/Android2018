package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Julius Skripkauskas.
 */
public class Form extends ProtoObject{
    public int id;
    @SerializedName("professional_id")
    public int professionalId;
    public String title;
    public String description;
    public List<FormInput> inputs;

}
