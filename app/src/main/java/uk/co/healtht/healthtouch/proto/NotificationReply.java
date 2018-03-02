package uk.co.healtht.healthtouch.proto;

import java.util.List;

public class NotificationReply extends Reply {
    public List<Notification> data;
    public Paginator paginator;
}
