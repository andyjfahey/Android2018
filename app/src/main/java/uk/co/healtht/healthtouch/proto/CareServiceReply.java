package uk.co.healtht.healthtouch.proto;

import java.util.List;

public class CareServiceReply extends Reply {
    public List<CareService> data;
    public Paginator paginator;
}
