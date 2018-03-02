package uk.co.healtht.healthtouch.api;

import java.util.Map;

public class ApiProviderListenerImpl implements ApiProviderListener {
    @Override
    public void onDataLoaded(EndPointProvider provider, Object providerData) {

    }

    @Override
    public void onDataLoadedError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {

    }

    @Override
    public void onNoData(EndPointProvider endPointProvider) {
    }
}
