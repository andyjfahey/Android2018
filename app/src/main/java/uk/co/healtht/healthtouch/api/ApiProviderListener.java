package uk.co.healtht.healthtouch.api;

import java.util.Map;

public interface ApiProviderListener {

    void onDataLoaded(EndPointProvider provider, Object providerData);

    void onDataLoadedError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode);

    void onNoData(EndPointProvider endPointProvider);
}
