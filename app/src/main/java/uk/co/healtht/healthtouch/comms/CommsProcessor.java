package uk.co.healtht.healthtouch.comms;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.api.EndPoint;
import uk.co.healtht.healthtouch.api.EndPointMethod;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.utils.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommsProcessor {

    public static final boolean SERVER_IS_PROD = true;

    public static final String SERVER_DEV = "http://apidev.healthtouchmobile.com";
    public static final String SERVER_PROD = "https://api.healthtouchmobile.com";
    public static final String SERVER_LIVE = "https://api.healthtouchapp.com";
    public static final String HEADER_AUTHORIZATION = "AUTHORIZATION";

    private static final String TAG = "Network";
    private static final String DEFAULT_CACHE_DIR = "volley";
    private static final String BOOKINGS_CACHE_DIR = "proto";
    private static final String ICONS_CACHE_DIR = "icons";

    private final Gson gson;
    private final Map<String, String> headers;
    public final RequestQueue queue;
    private DiskBasedCache diskCache;
    private final ImageLoader imageLoader, iconLoader;
    private String serverUrl;

    public CommsProcessor(Context ctx, String deviceId) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
        gson = gsonBuilder.create();

        headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("X-Platform", "ios"); // TODO: headers.put("X-Platform", Platform.getServerPlatform());
//        headers.put("X-API-VERSION", "1");
//        headers.put("X-VENDOR-IDENTIFIER", deviceId);
        headers.put("X-Device-Model", Build.MODEL);
        headers.put("X-OS-Version", Build.VERSION.RELEASE);
        headers.put("X-Bundle-Version", Platform.version);
//
//
//        try {
//            addHeader("X-ID-ANDROID", Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID));
//        }
//        catch (Throwable e) {
//            // It's OK if we can't get the ANDROID_ID
//        }
//
//        addHeader("X-ID-HARDWARE-SERIAL", Build.SERIAL);

        queue = newRequestQueue(ctx);

        File cacheDir = new File(ctx.getFilesDir(), BOOKINGS_CACHE_DIR);
        diskCache = new DiskBasedCache(cacheDir);
        // Make a blocking call to initialize the cache.
        diskCache.initialize();

        ActivityManager actMan = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        long totalImageMem = (long) (0.2 * actMan.getMemoryClass() * 1024 * 1024); // Cache 2/10 of total available
        imageLoader = new ImageLoader(queue, new BitmapLruImageCache((int) (0.9 * totalImageMem)));
        iconLoader = new ImageLoader(queue, new BitmapLruImageCache((int) (0.1 * totalImageMem)));

        setServer(null);
    }

    public void setSession(String sessionId) {
        boolean cleanSession = TextUtils.isEmpty(sessionId);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, cleanSession ? "[Session] cleanSession" : "[Session] setSession");
        }

        if (cleanSession) {
            headers.remove(HEADER_AUTHORIZATION);

            // TODO: This may block the UI thread...
            diskCache.clear();
        } else {
            headers.put(HEADER_AUTHORIZATION, sessionId);
        }
    }

    public Map<String, String> getAuthHeaders() {
        return headers;
    }

    public void setServer(String server) {
        if (TextUtils.isEmpty(server)) {
            if (SERVER_IS_PROD){
                serverUrl = SERVER_PROD;
            }
            else {
                serverUrl = SERVER_DEV;
            }
        } else {
            serverUrl = server;
        }
    }

    public String getServer() {
        return serverUrl;
    }

    public ImageLoader getImageLoader(boolean isIcon) {
        return isIcon ? iconLoader : imageLoader;
    }

    public String fetchData(CommsResponseListener respListener, EndPoint endPoint, Object reqData, boolean cacheOnly, Map<String, String> extraHeaders) {


        String url = serverUrl + endPoint.getUri();

        if (endPoint.getUri().contains("/api/v1/tracking/") && endPoint.getMethod() == EndPointMethod.GET){
            url = url + "?limit=500&page=1";
        }

        Log.e(getClass().getSimpleName(), "url = " + url);

        boolean hasExtraHeaders = (extraHeaders != null && !extraHeaders.isEmpty());
        boolean isPatchPost = (endPoint.getMethod() == EndPointMethod.PATCH);

        Map<String, String> reqHeaders;
        if (hasExtraHeaders || isPatchPost) {
            reqHeaders = new HashMap<>(headers);

            if (hasExtraHeaders) {
                reqHeaders.putAll(extraHeaders);
            }

            if (isPatchPost) {
                reqHeaders.put("X-HTTP-Method-Override", "PATCH");
            }
        } else {
            reqHeaders = headers;
        }

        int method;
        switch (endPoint.getMethod()) {
            case POST: // Fall -Through
            case PATCH:
                method = Method.POST;
                break;

            case PUT:
                method = Method.PUT;
                break;

            case DELETE:
                method = Method.DELETE;
                break;

            default:
                method = Method.GET;
                break;
        }

        Class<?> clazz = endPoint.getClazz();

        GsonRequest req = new GsonRequest(gson, method, url, respListener, clazz, reqHeaders);
        req.setTag(respListener);
        req.setRequestData(reqData);

        if (!cacheOnly) { // Normal network/cache queued request
            queue.add(req);
        } else { // If we only want a cache result, look up synchronously, don't queue.
            Cache.Entry cacheEntry = req.getFromCache();
            Object response = req.parseCacheEntry(cacheEntry);
            boolean cacheMiss = true;
            if (cacheEntry != null && response != null) {
                try {
                    long replyTime = (cacheEntry.serverDate <= 0) ? System.currentTimeMillis() : cacheEntry.serverDate;
                    respListener.onCommsExecuterResponse(response, null, replyTime, true);
                    respListener.onCommsResponse();
                    cacheMiss = false;
                } catch (Throwable e) {
                    // Sometimes the server data structures change, and our cache still has a old data structure
                    Crash.logException(e);
                }
            }

            if (cacheMiss) {
                respListener.onCommsError(null, "Cache miss", 0);
            }
        }

        return url;
    }

    public void cancelRequests(CommsResponseListener respListener) {
        queue.cancelAll(respListener);
    }

    private void addHeader(String headerName, String value) {
        if (value != null && value.trim().length() > 0) {
            headers.put(headerName, value);
        }
    }

    private static RequestQueue newRequestQueue(Context ctx) {
        File mainDir = new File(ctx.getCacheDir(), DEFAULT_CACHE_DIR);
        File iconDir = new File(ctx.getCacheDir(), ICONS_CACHE_DIR);

        HurlStack.UrlRewriter urlRewriter = new HurlStack.UrlRewriter() {
            @Override
            public String rewriteUrl(String originalUrl) {
                // HACK: 302 redirects between HTTP and HTTPS don't work for security reasons. Facebook redirects to HTTP to HTTPS
                // However, we can use a HTTPS link instead of the supplied facebook HTTP, therefore redirect does not change protocol.
                if (originalUrl != null && originalUrl.contains("facebook.com")) {
                    return originalUrl.replace("http://", "https://");
                }
                return originalUrl;
            }
        };
        Network network = new BasicNetwork(new HurlStack(urlRewriter));

        RequestQueue queue = new RequestQueue(new DiskPartionedCache(mainDir, iconDir), network);
        queue.start();

        return queue;
    }
}
