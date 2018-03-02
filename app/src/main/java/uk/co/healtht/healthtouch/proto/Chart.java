package uk.co.healtht.healthtouch.proto;

public class Chart extends ProtoObject {
    public String grouping; // Can be "day"

    // Can be "value?" or "count" or "avg" or "sum"
    // count - Number of entries in a period of time (eg. Went to toilet 3 times in one day)
    // sum - Sum the value of all entries in a period of time
    // avg - Average
    public String value;

    public String style; // Can be "line" or "bar"
}
