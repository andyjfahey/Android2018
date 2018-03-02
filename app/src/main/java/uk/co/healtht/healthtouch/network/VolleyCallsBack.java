package uk.co.healtht.healthtouch.network;

public interface VolleyCallsBack
{

    void onVolleySuccess(String result, int request_id);

    void onVolleyError(String error, int request_id);
}
