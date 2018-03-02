package uk.co.healtht.healthtouch.comms;

import java.util.Map;

public interface CommsResponseListener {

    void onCommsResponse();

    /**
     * Same as onCommsResponse() but instead of running on the UI thread, its called from the Executer Thread.
     *
     * @param event
     * @param resp
     */
    void onCommsExecuterResponse(Object resp, Map<String, String> headers, long replyTime, boolean isFromCache);

    void onCommsError(Map<String, Object> errorObj, String debugMsg, int errorCode);

    void onCommsNoData();
}