package uk.co.healtht.healthtouch.proto;

import java.util.List;

public class TrackerEntriesReply extends Reply {
    public List<TrackerEntry> data;
    public Paginator paginator;
}
