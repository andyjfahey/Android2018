package uk.co.healtht.healthtouch.proto;

import java.util.List;

public class CareMyNetworkReply extends Reply {
    public List<CareNetworkRequest> data;
    public Paginator paginator;
}
