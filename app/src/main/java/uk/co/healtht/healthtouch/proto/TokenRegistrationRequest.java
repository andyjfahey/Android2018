package uk.co.healtht.healthtouch.proto;

public class TokenRegistrationRequest extends ProtoObject{
    public String userId;
    public String deviceType = "android";
    public String deviceToken;
}
