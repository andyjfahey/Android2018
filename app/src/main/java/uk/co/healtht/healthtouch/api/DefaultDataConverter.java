package uk.co.healtht.healthtouch.api;

public class DefaultDataConverter implements EndPointProvider.ProtoDataConverter {

    public DefaultDataConverter() {
    }

    @Override
    public Object processData(EndPointProvider provider, Object resp, long replyTime) {
        // TODO: Do any useful conversion between proto and ui Objects
        return resp;
    }
}
