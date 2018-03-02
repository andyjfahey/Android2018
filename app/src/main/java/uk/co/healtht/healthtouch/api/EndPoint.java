package uk.co.healtht.healthtouch.api;

import java.io.Serializable;

public class EndPoint implements Serializable {
    private String uri;
    private Class<?> clazz;
    private EndPointMethod method;
    private String user;
    private String authHeader;

    public EndPoint(){

    }

    public EndPoint(EndPointMethod method, String uri, Class<?> clazz) {
        this.uri = uri;
        this.clazz = clazz;
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public EndPointMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return method + ":" + uri;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
