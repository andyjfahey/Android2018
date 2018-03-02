package uk.co.healtht.healthtouch.api;

import android.content.Context;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.debug.Debug;
import uk.co.healtht.healthtouch.settings.SettingsUser;

import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiCache {
    private NavigableMap<String, EndPointRequest> pendingRequests;
    private DB db;
    private Atomic.Integer reqCounter1;
    private AtomicInteger reqCounter2;

    public ApiCache(Context ctx) {
        File file = null;
        try {
            file = new File(ctx.getFilesDir(), "mapDB.bin");
            db = DBMaker.newFileDB(file)
                    .closeOnJvmShutdown()
                    .cacheHardRefEnable()
                            // .asyncWriteEnable() -> There is an exception -> Writer thread failed -> Check on next version
                    .cacheSize(10000)
                    .make();

            reqCounter1 = db.getAtomicInteger("counter");
            pendingRequests = db.getTreeMap("pendingRequests");
        } catch (Throwable ex) {
            Crash.logException(ex);

            if (file != null) {
                file.delete();
            }

            // If we can't create a BD, we will keep things in memory
            pendingRequests = new TreeMap<>();
            reqCounter2 = new AtomicInteger();
        }
    }

    public int createNewReqId() {
        if (reqCounter1 != null) {
            return reqCounter1.incrementAndGet();
        }

        return reqCounter2.incrementAndGet();
    }

    public String addPendingRequest(EndPointProvider provider) {

//        HTApplication.getInstance().getApiProvider().resetLastDate();
        EndPoint ep = provider.getEndPoint();
        SettingsUser settingsUser = HTApplication.getInstance().getApiProvider().settingsUser;
        Map<String, String> authHeader = HTApplication.getInstance().getApiProvider().getComms().getAuthHeaders();
        ep.setUser(settingsUser.getEmail());
        ep.setAuthHeader(authHeader.get("AUTHORIZATION"));

        EndPointRequest req = new EndPointRequest(ep, provider.getLastRequestedData());
        String reqId = ep.getMethod().toString() + createNewReqId();

        pendingRequests.put(reqId, req);

        commit();
        return reqId;
    }

    public void removePendingRequest(EndPointProvider provider) {
        Set<String> pendingRequestKeys = pendingRequests.keySet();
        ArrayList<String> removeList = new ArrayList<>();

        for (String key : pendingRequestKeys) {
            EndPointRequest request = pendingRequests.get(key);

            if (request.endPoint.getUri().equals(provider.endPoint.getUri())
                    && request.endPoint.getMethod().toString().equals(provider.endPoint.getMethod().toString())) {
                removeList.add(key);
            }
        }

        for (String key : removeList) {
            pendingRequests.remove(key);
        }

        commit();
    }

    public void removeAllPendingRequest() {
        Set<String> pendingRequestKeys = pendingRequests.keySet();
        ArrayList<String> removeList = new ArrayList<>();

        for (String key : pendingRequestKeys) {
            removeList.add(key);
        }

        for (String key : removeList) {
            pendingRequests.remove(key);
        }
        commit();
    }

    public void removePendingRequest(String key) {
        pendingRequests.remove(key);
        commit();
    }

    public HashMap<String, EndPointRequest> getAllPendingRequests() {
        HashMap<String, EndPointRequest> result = new HashMap<>();
        result.putAll(pendingRequests);
        return result;
    }

    public Map.Entry<String, EndPointRequest> peekFirstPendingRequest() {
        return pendingRequests.firstEntry();
    }

    public int getPendingCount() {
        return pendingRequests.size();
    }

    public Map.Entry<String, EndPointRequest> removeFirstPendingRequest() {
        Map.Entry<String, EndPointRequest> res = pendingRequests.pollFirstEntry();
        commit();

        return res;
    }

    private void commit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (db != null) {
                    try {
                        db.commit();
                    } catch (Throwable ex) {
                        Debug.showErrorToast("commit: " + ex.toString());
                    }
                }
            }
        }).start();
    }
}