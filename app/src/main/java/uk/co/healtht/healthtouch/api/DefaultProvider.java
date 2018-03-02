package uk.co.healtht.healthtouch.api;

import uk.co.healtht.healthtouch.proto.Reply;
import uk.co.healtht.healthtouch.proto.UserReply;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DefaultProvider extends EndPointProvider implements Serializable {

    DefaultProvider() {
        super();
    }

    DefaultProvider(ApiProvider apiProvider, EndPoint endPoint) {
        super(apiProvider.comms, apiProvider.ctx, endPoint, apiProvider.dataConverter, apiProvider.debugDataProvider);

        this.apiProvider = apiProvider;
    }

    @Override
    public void onCommsResponse() {
        if (providerData instanceof Reply) {
            // WTF - Our Rest server is not that rest... we can get a 200, and still get a failure
            Reply reply = (Reply) providerData;
            if (!reply.status) {
                HashMap<String, Object> error = new HashMap<>(1);
                error.put("message", reply.message);

                if (reply.message instanceof String) {
                    onCommsError(error, (String) reply.message, reply.code);
                }
                return;
            }
        }

        if (providerData instanceof UserReply) {
            apiProvider.settingsUser.setUser(((UserReply) providerData).data);
        }

        super.onCommsResponse();
    }

    public void onCommsError(Map<String, Object> errorObj, String debugMsg, int errorCode) {
        apiProvider.handleDataLoadedError(errorCode, getEndPoint());

        super.onCommsError(errorObj, debugMsg, errorCode);
    }

    public void onCommsNoData() {
        super.onCommsNoData();
    }
}
