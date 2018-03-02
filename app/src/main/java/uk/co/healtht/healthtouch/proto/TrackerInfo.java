package uk.co.healtht.healthtouch.proto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackerInfo extends ProtoObject {
    public String name;
    public String label;
    public String type; // integer, double, enum, etc
    public int min;
    public int max;
    @SerializedName("range_min")
    public int rangeMin;
    @SerializedName("range_max")
    public int rangeMax;
    @SerializedName("default")
    public Object defaultValue; // Can be a number or true/false for enum's
    public String units;
    @SerializedName("decimal_digits")
    public int decimalDigits; // If type is float/double
    public int digits; // If type is float/double
    public float step;

    public Chart chart;

    @SerializedName("value_label")
    public List<String> valueLabel = new ArrayList<>();

}
