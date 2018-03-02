package uk.co.healtht.healthtouch.api;

import android.content.Context;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.comms.CommsResponseListener;
import uk.co.healtht.healthtouch.debug.DebugDataProvider;
import uk.co.healtht.healthtouch.platform.Platform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EndPointProvider implements CommsResponseListener, Serializable {

    public EndPointProvider() {

    }

    /**
     * Used to convert object received from the network (proto object), to a more UI friendly object.
     */
    public interface ProtoDataConverter {
        Object processData(EndPointProvider provider, Object resp, long replyTime);
    }

    transient protected ApiProvider apiProvider;
    transient private List<ApiProviderListener> listeners;
    transient private ApiProviderListener[] listenersTmp = new ApiProviderListener[1];
    transient protected Context ctx;
    transient protected CommsProcessor comms;
    protected EndPoint endPoint;
    transient protected ProtoDataConverter dataConverter;
    protected DebugDataProvider debugDataProvider;
    protected Object providerData, lastReqData;
    private boolean isLoading;
    private boolean isReady = true;
    transient private Map<String, String> requestHeaders, replyHeaders;
    private long timeLoaded; // 0L
    private boolean valid = true;


    public void setLastReqData(Object lastReqData) {
        this.lastReqData = lastReqData;
    }

    public EndPointProvider(CommsProcessor comms, Context ctx, EndPoint endPoint, ProtoDataConverter dataConverter, DebugDataProvider debugDataProvider) {
        this.comms = comms;
        this.ctx = ctx;
        this.endPoint = endPoint;
        this.dataConverter = dataConverter;
        this.debugDataProvider = debugDataProvider;

        listeners = new ArrayList<>();
    }

    public void addListener(ApiProviderListener l) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeListener(ApiProviderListener l) {
        listeners.remove(l);
    }

    public boolean containsListener(ApiProviderListener l) {
        return listeners != null && listeners.contains(l);
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void invalidateData() {
        timeLoaded = 0L;
    }

    public void clearData() {
        invalidateData();

        if (overWriteDataAfterProcessing()) {
            providerData = null;
        }
    }

    /**
     * Note: this returns whether there any current data for any previous passed reqData.
     * Should not use this method if the provider takes URI or reqData attributes for loading different result sets.
     *
     * @return Whether this provider contains recent data.
     */
    public boolean hasValidData() {
        long VALID_DATA_TIME = 5 * 60 * 1000;
        return providerData != null && Math.abs(System.currentTimeMillis() - timeLoaded) < VALID_DATA_TIME;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isReadyToLoad() {
        return isReady;
    }

    public void setReadyToLoad(boolean isReady) {
        this.isReady = isReady;
    }

    public void setRequestHeaders(Map<String, String> extraHeaders) {
        this.requestHeaders = extraHeaders;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getReplyHeaders() {
        return replyHeaders;
    }

    public Object getLastRequestedData() {
        return lastReqData;
    }

    public void cancelLoading() {
        isLoading = false;
        comms.cancelRequests(this);
    }

    public void get() {
        assertEndPointMethod(EndPointMethod.GET);
        fetchCommsData(null, false);
    }

    public void getCachePriority() {
        assertEndPointMethod(EndPointMethod.GET);
        fetchCommsDataCachePriority(null);
    }

    public void post(Object reqData) {
        assertEndPointMethod(EndPointMethod.POST);
        fetchCommsData(reqData, false);
    }

    public void delete() {
        assertEndPointMethod(EndPointMethod.DELETE);
        fetchCommsData(null, false);
    }

    public void patch(Object patchData) {
        assertEndPointMethod(EndPointMethod.PATCH);
        fetchCommsData(patchData, false);
    }

    public void put(Object putData) {
        assertEndPointMethod(EndPointMethod.PUT);
        fetchCommsData(putData, false);
    }

    public void refreshGet() {
        if (!hasValidData()) {
           get();
        }
    }

    private void assertEndPointMethod(EndPointMethod endPointMethod) {
        if (endPoint.getMethod() != endPointMethod) {
            if (BuildConfig.DEBUG) {
                Crash.reportCrash(endPoint.getMethod() + " != " + endPointMethod);
            }

            Crash.logException(new RuntimeException());
        }
    }

    protected void fetchCommsDataCachePriority(Object reqData) {
        if (!handleOfflineRequest()) {
            fetchCommsData(reqData, false);
        }
    }

    protected void fetchCommsData(Object reqData, boolean cacheOnly) {
        // This assumes single-threaded loading per provider. Making two request on same provider with different reqData will lose the second call.
        if (isReady && !isLoading) {
            isLoading = true;
            lastReqData = reqData;

            if (debugDataProvider.shouldFakeRequest(endPoint)) {
                debugDataProvider.fakeRequest(endPoint, this, reqData);
            } else {
                if (Platform.hasNetworkConnection(ctx)) {
                    comms.fetchData(this, endPoint, reqData, cacheOnly, requestHeaders);
                } else {
                    handleOfflineRequest();
                }
            }
        }
    }

    protected boolean handleOfflineRequest() {
        boolean dataState = providerData != null;

        if (dataState) {
            onCommsResponse();
        } else if (!Platform.hasNetworkConnection(ctx)) {
            onCommsNoData();
        }

        return dataState;
    }

    public Object getProviderData() {
        return providerData;
    }

    public void setProviderData(Object providerData) {
        this.providerData = providerData;
    }

    private void setData(Object providerData, boolean isFromCache) {
        if (isFromCache) {
            this.timeLoaded = 0; // Invalid, but still usable
        } else {
            this.timeLoaded = System.currentTimeMillis();
        }
        this.providerData = providerData;
    }

    public Object getResult() {
        return providerData;
    }

    public long getLoadedTime() {
        return timeLoaded;
    }

    @Override
    public void onCommsNoData() {
        isLoading = false;

        for (ApiProviderListener listener : listeners) {
            listener.onNoData(this);
        }
    }

    @Override
    public void onCommsResponse() {
        isLoading = false;

        // Note: Can't use "foreach" loop, as it is possible that calling onDataLoaded() starts a new fragment, that it then adds itself to the listeners list..
        // This causes a concurrent exception.

        for (int i = (listeners.size()-1); i >= 0; i--) {
            listeners.get(i).onDataLoaded(this, providerData);
        }

    }

    @Override
    public void onCommsError(Map<String, Object> errorObj, String debugMsg, int errorCode) {
        isLoading = false;

        // Note: When we call onDataLoadedError on a fragment, it may "finish" as result, and that will remove the listener. We need to use a temporary
        // structure to avoid ConcurrentModificationException
        listenersTmp = listeners.toArray(listenersTmp); // Will increase the array size if needed.

        int count = listeners.size();
        for (int i = 0; i < count; i++) {
            try {
                listenersTmp[i].onDataLoadedError(this, errorObj, debugMsg, errorCode);
            } catch (Throwable e) {
                Crash.logException(e);
            }

            listenersTmp[i] = null;
        }
    }

    protected boolean overWriteDataAfterProcessing() {
        // Some sub classes may wish to keep the same data object, and only update on the UI thread.
        return true;
    }

    @Override
    public void onCommsExecuterResponse(Object resp, Map<String, String> headers, long replyTime, boolean isFromCache) {
        // If we receive a reply from the network, but are faking the reply, we don't want to process it
        if (!debugDataProvider.shouldFakeRequest(endPoint)) {
            this.replyHeaders = headers;
            Object processedData = dataConverter.processData(this, resp, replyTime);
            setData(overWriteDataAfterProcessing() ? processedData : providerData, isFromCache);
        }
    }

    public void setComms(CommsProcessor comms) {
        this.comms = comms;
    }

    public void setApiProvider(ApiProvider apiProvider) {
        this.apiProvider = apiProvider;
    }

    public void setListener(ArrayList<ApiProviderListener> listener) {
        this.listeners = listener;
    }

    public void setListenerTmp(ApiProviderListener[] listenerTmp) {
        this.listenersTmp = listenerTmp;
    }

    public void setDataConverter(DefaultDataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public void setContext(Context context) {
        this.ctx = context;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
