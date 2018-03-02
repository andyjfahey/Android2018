package uk.co.healtht.healthtouch.comms;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.debug.ObjectSignature;
import uk.co.healtht.healthtouch.settings.SettingsDebug;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Map;

public class GsonRequest extends Request<Object>
{

	private final Gson gson;
	private CommsResponseListener respListener;
	private final Class<?> clazz;
	private final Map<String, String> headers;
	private Object reqData;
	private Cache cache;

	public GsonRequest(Gson gson, int method, String url, CommsResponseListener respListener, Class<?> clazz, Map<String, String> headers)
	{
		super(method, url, null);

		// Don't retry posting, or we may buy a ticket more than once.
		int numRetries = (method == Method.GET) ? 2 : 0;
		// Some bookings use a third party provider, and they can be really slow...
		int initialTimeoutMs = (method == Method.POST) ? 30000 : 10000;
		setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, numRetries, 2.0f));

		// Our server doesn't set any caching header, so we need to disable this, otherwise, we keep a cache on disk/memory, but we never create Objects from it
		setShouldCache(false);

		this.gson = gson;
		this.respListener = respListener;
		this.clazz = clazz;
		this.headers = headers;

		LogUtils.i(url, " " + clazz.getName());
		for (String key : headers.keySet())
		{
			LogUtils.i("Key = " + key, " - " + headers.get(key));
		}
	}

	public void setRequestData(Object reqData)
	{
		this.reqData = reqData;
	}

	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

	@Override
	protected Response<Object> parseNetworkResponse(NetworkResponse response)
	{
		String json = null;
		try
		{
			String charSet = HttpHeaderParser.parseCharset(response.headers);
			if (HTTP.DEFAULT_CONTENT_CHARSET.equals(charSet))
			{
				// The default HTTP encoding is "ISO-8859-1". The server just "forgot" to tell us the char-set, so we assume the default JSON encoding.
				// See http://stackoverflow.com/questions/9254891/what-does-content-type-application-json-charset-utf-8-really-mean
				charSet = "UTF-8";
			}

			json = new String(response.data, charSet);
			if (BuildConfig.DEBUG)
			{
				final int maxResponseLogSize = 4096 - 250;
				String reqDataStr = gson.toJson(reqData);
				int totalChunksNeeded = (SettingsDebug.isFullNetworkLog() ? (json.length() / (maxResponseLogSize + 1)) + 1 : 1);
				int splitStart = 0;
				for (int i = 0; i < totalChunksNeeded; i++)
				{
					int end = splitStart + (Math.min(json.length() - splitStart, maxResponseLogSize));
					String chunk = json.substring(splitStart, end);
					Log.v(SettingsDebug.TAG, "[Network] Request " + getMethodAsString() + " (" + (i + 1) + "/" + totalChunksNeeded + "): " + getUrl() + " : " + reqDataStr
							+ " : Response: " + chunk);
					splitStart = end;
					reqDataStr = "";
				}

				if (SettingsDebug.isFullNetworkLog())
				{
					Log.v(SettingsDebug.TAG, "[Network] Request Headers: " + headers);
					Log.v(SettingsDebug.TAG, "[Network] Response Headers: " + response.headers);
				}
			}

			Object result = gson.fromJson(json, clazz);
			if (BuildConfig.DEBUG && SettingsDebug.isShowErrorToasts())
			{
				Object map = gson.fromJson(json, Object.class);
				String res = ObjectSignature.checkObjectMapping(result, map);
				if (res != null)
				{
					System.out.println("map = " + map);
					// Debug.showErrorToast("DECODING: " + result.getClass().getSimpleName() + "->" + res);
				}
			}

			long replyTime = 0L;
			Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
			if (cache == null)
			{
				String headerValue = response.headers.get("Date");
				if (headerValue != null)
				{
					replyTime = HttpHeaderParser.parseDateAsEpoch(headerValue);
				}
			}
			else
			{
				cache.put(getCacheKey(), cacheEntry);
				replyTime = cacheEntry.serverDate;
			}

			if (replyTime <= 0L)
			{
				replyTime = System.currentTimeMillis();
			}

			respListener.onCommsExecuterResponse(result, response.headers, replyTime, false);

			return (Response.success(result, cacheEntry));
		}
		catch (Throwable e)
		{
			if (BuildConfig.DEBUG)
			{
				System.out.println("Broken JSON: " + json);
				e.printStackTrace();
			}
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(Object response)
	{
		respListener.onCommsResponse();
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError)
	{
		// NOTE: This method is called on the "worker" thread
		Object result = parseCacheEntry(getFromCache());
		if (result != null)
		{
			respListener.onCommsExecuterResponse(result, null, System.currentTimeMillis(), true);
		}
		return super.parseNetworkError(volleyError);
	}

	public Cache.Entry getFromCache()
	{
		if (cache != null)
		{
			return cache.get(getCacheKey());
		}
		return null;
	}

	public Object parseCacheEntry(Cache.Entry cacheEntry)
	{
		if (cacheEntry != null && cacheEntry.data != null)
		{
			try
			{
				String json = new String(cacheEntry.data, "UTF-8");
				if (BuildConfig.DEBUG)
				{
					Log.v(SettingsDebug.TAG, "[Network] Cache: " + getUrl() + " : " + gson.toJson(reqData) + " : Response: " + json);
				}
				return gson.fromJson(json, clazz);
			}
			catch (Exception ex)
			{
				Crash.logException(ex);
			}
		}
		return null;
	}

	@Override
	public void deliverError(VolleyError error)
	{
		// NOTE: This method is called on the UI thread
		if (cache != null)
		{
			respListener.onCommsResponse();
		}

		try
		{
			ArrayList<String> errorParams = new ArrayList<>();
			int errorCode = 0;
			String errorAsString = null;

			if (error == null)
			{
				errorParams.add("description");
				errorParams.add(String.valueOf((Object) null));
			}
			else
			{
				errorParams.add("description");
				errorParams.add(error.getLocalizedMessage());

				String errorDetails = "urlKey=" + getUrl();
				if (error.networkResponse != null)
				{
					errorAsString = error.networkResponse.data == null ? "null" : new String(error.networkResponse.data, "UTF-8");
					errorCode = error.networkResponse.statusCode;

					errorDetails += " code=" + errorCode + " data=" + errorAsString;
				}

				errorParams.add("details");
				errorParams.add(errorDetails);
			}

			if (BuildConfig.DEBUG)
			{
				Log.v(SettingsDebug.TAG, "[Network] Error: (" + errorCode + " - " + getMethodAsString() + ") " + getUrl() + " : " + gson.toJson(reqData) + " : Response: " + errorAsString);
				Log.v(SettingsDebug.TAG, "[Network] Error Headers: " + getHeaders());
			}

			HTApplication.getInstance().getAnalytics().track("Error", errorParams);
		}
		catch (Throwable e)
		{
			Crash.logException(e);
		}


		if (respListener != null)
		{
			String dbgMsg = null;
			int errorCode = 0;
			Map<String, Object> errorObj = null;
			if (error != null && error.networkResponse != null && error.networkResponse.data != null)
			{
				String netResp = new String(error.networkResponse.data);
				errorCode = error.networkResponse.statusCode;
				try
				{
					errorObj = (Map<String, Object>) gson.fromJson(netResp, Object.class);
					if (errorObj != null)
					{
						dbgMsg = (String) errorObj.get("message");
						if (dbgMsg == null)
						{
							dbgMsg = netResp;
						}
					}
				}
				catch (Throwable e)
				{
					Crash.log("netResp = " + netResp);
					Crash.logException(e);
				}
			}
			else
			{
				dbgMsg = ((error != null ? error.getMessage() : null) == null) ? error.toString() : error.getMessage();
				if (dbgMsg != null && dbgMsg.contains("javax.net.ssl") && !dbgMsg.contains("timed out"))
				{
					// Note: Are we having problems with ssl certificates?
					// http://stackoverflow.com/questions/9574870/no-peer-certificate-error-in-android-2-3-but-not-in-4
					Crash.log(dbgMsg);
					Crash.logException(error);
				}
			}

			HTApplication.getInstance().getAnalytics().track("Error", "Message", dbgMsg);

			respListener.onCommsError(errorObj, dbgMsg, errorCode);
		}

		super.deliverError(error);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
	{
		return headers == null ? super.getHeaders() : headers;
	}

	@Override
	public String getBodyContentType()
	{
		return "application/json";
	}

	@Override
	public byte[] getBody() throws AuthFailureError
	{
		if (reqData != null)
		{
			try
			{
				String json = gson.toJson(reqData);
				return json.getBytes("UTF-8");
			}
			catch (Throwable e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getBody();
	}

	private String getMethodAsString()
	{
		switch (getMethod())
		{
			case Method.DELETE:
				return "DELETE";
			case Method.GET:
				return "GET";
			case Method.PATCH:
				return "PATCH";
			case Method.POST:
				return "POST";
			case Method.PUT:
				return "PUT";
			default:
				return "UNKNOWN: " + getMethod();
		}
	}
}