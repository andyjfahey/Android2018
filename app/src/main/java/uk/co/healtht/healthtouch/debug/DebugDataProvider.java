package uk.co.healtht.healthtouch.debug;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.api.EndPoint;
import uk.co.healtht.healthtouch.api.EndPointProvider;

import java.io.Serializable;

public class DebugDataProvider implements Serializable {

    public DebugDataProvider() {
    }

    // --------- DEBUG CODE!! ------
    public boolean shouldFakeRequest(EndPoint endPoint) {
        if (!BuildConfig.DEBUG) {
            // Never fake if we are not in debug
            return false;
        }

        // TODO
        return false;
    }

    public void fakeRequest(EndPoint endPoint, final EndPointProvider provider, Object reqData) {

    }
}
