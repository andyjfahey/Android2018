package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Julius Skripkauskas.
 */
public class FormInput extends ProtoObject{
    public int id;
    @SerializedName("form_id")
    public int formId;
    public int required;
    @SerializedName("input_type")
    public String inputType;
    @SerializedName("input_label")
    public String inputLabel;
    @SerializedName("input_name")
    public String inputName;
    @SerializedName("extra_options")
    public Object extraOptions;
}
