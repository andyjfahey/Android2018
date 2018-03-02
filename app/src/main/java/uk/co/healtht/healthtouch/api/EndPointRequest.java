package uk.co.healtht.healthtouch.api;

import java.io.Serializable;

public class EndPointRequest implements Serializable {
    public EndPoint endPoint;
    public Object request;

    public EndPointRequest(){

    }

    public EndPointRequest(EndPoint endPoint, Object request) {
        this.endPoint = endPoint;
        this.request = request;
    }

    @Override
    public String toString() {
        return "EndPointRequest{endPoint:" + endPoint + " request: " + request;
    }
}
