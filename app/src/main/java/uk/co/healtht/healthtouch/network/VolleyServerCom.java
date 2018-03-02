package uk.co.healtht.healthtouch.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;


public class VolleyServerCom
{
	private VolleyCallsBack volleyCallsBackListener;

	@NonNull
	private static String TAG = "Volly Response";

	private final int socketTimeout = 10000; //10 seconds - change to what you want
	private RetryPolicy retryPolicy;

	private Context context;

	private Map<String, String> headers;

	public VolleyServerCom(Context context, VolleyCallsBack vollyCallBackListener, Map<String, String> reqHeaders)
	{
		this.volleyCallsBackListener = vollyCallBackListener;
		this.context = context;
		this.headers = reqHeaders;

		retryPolicy = new DefaultRetryPolicy(socketTimeout, 2, 2);
	}

	public void GETRequest(String url, final String request_tag, final int request_id)
	{
		LogUtils.i("URL", url);

		if (AppUtil.isConnected(context))
		{
			final JsonRequest jsonObjReq = new JsonObjectRequest(
					Request.Method.GET,
					url,
					(String) null,

					new Response.Listener<JSONObject>()
					{

						@Override
						public void onResponse(@NonNull JSONObject response)
						{
							LogUtils.i(TAG, response.toString());

							volleyCallsBackListener.onVolleySuccess(response.toString(), request_id);
						}
					},

					new Response.ErrorListener()
					{
						@Override
						public void onErrorResponse(@NonNull VolleyError error)
						{

							VolleyLog.d(TAG, "Error: " + error.getMessage());

							if (error.networkResponse != null && error.networkResponse.data != null)
							{
								volleyCallsBackListener.onVolleyError(new String(error.networkResponse.data), request_id);
								VolleyLog.d(TAG, "Error: " + new String(error.networkResponse.data));
							}
							else
							{
								volleyCallsBackListener.onVolleyError(error.toString(), request_id);
							}

						}
					})

			{
				@NonNull
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError
				{
					return headers;
				}
			};

			jsonObjReq.setRetryPolicy(retryPolicy);
			// Adding request to request queue
			HTApplication.getInstance().addToRequestQueue(jsonObjReq, request_tag);
		}
		else
		{
			volleyCallsBackListener.onVolleyError(AppConstant.INTERNET_DISCONNECT, request_id);
		}
	}

	public void POSTRequest(String url, String request_body, String request_tag,
	                        final int request_id)
	{
		LogUtils.i("URL", url);

		if (AppUtil.isConnected(context))
		{
			LogUtils.i("Request==>", request_body);

			JsonRequest jsonObjReq = new JsonObjectRequest(
					Request.Method.POST,
					url,
					request_body,

					new Response.Listener<JSONObject>()
					{

						@Override
						public void onResponse(@NonNull JSONObject response)
						{
							LogUtils.i(TAG, response.toString());

							volleyCallsBackListener.onVolleySuccess(response.toString(), request_id);
						}
					},

					new Response.ErrorListener()
					{

						@Override
						public void onErrorResponse(@NonNull VolleyError error)
						{
							VolleyLog.d(TAG, "Error: " + error.getMessage());


							if (error.networkResponse != null && error.networkResponse.data != null)
							{
								volleyCallsBackListener.onVolleyError(new String(error.networkResponse.data), request_id);
								VolleyLog.d(TAG, "Error: " + new String(error.networkResponse.data));
							}
							else
							{
								volleyCallsBackListener.onVolleyError(error.toString(), request_id);
							}
						}
					})

			{

				@Override
				protected Response<JSONObject> parseNetworkResponse(@NonNull NetworkResponse response)
				{
					return super.parseNetworkResponse(response);
				}

				@NonNull
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError
				{
					Map<String, String> map = new HashMap<>();

					if (headers != null)
					{
						return headers;
					}
					else
					{
						return map;
					}
				}

			};

			jsonObjReq.setRetryPolicy(retryPolicy);
			// Adding request to request queue
			HTApplication.getInstance().addToRequestQueue(jsonObjReq, request_tag);
		}
		else
		{
			volleyCallsBackListener.onVolleyError(AppConstant.INTERNET_DISCONNECT, request_id);
		}
	}

	public void PUTRequest(String url, final String request_body, String request_tag, final int request_id)
	{
		if (AppUtil.isConnected(context))
		{
			LogUtils.i("Request==>", request_body);

			JsonRequest jsonObjReq = new JsonObjectRequest(
					Request.Method.PUT,
					url,
					request_body,

					new Response.Listener<JSONObject>()
					{

						@Override
						public void onResponse(@NonNull JSONObject response)
						{
							LogUtils.i(TAG, response.toString());

							volleyCallsBackListener.onVolleySuccess(response.toString(), request_id);
						}
					},

					new Response.ErrorListener()
					{

						@Override
						public void onErrorResponse(@NonNull VolleyError error)
						{
							VolleyLog.d(TAG, "Error: " + error.getMessage());


							if (error.networkResponse != null && error.networkResponse.data != null)
							{
								volleyCallsBackListener.onVolleyError(new String(error.networkResponse.data), request_id);
								VolleyLog.d(TAG, "Error: " + new String(error.networkResponse.data));
							}
							else
							{
								volleyCallsBackListener.onVolleyError(error.toString(), request_id);
							}
						}
					})

			{

				@Override
				protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
				{

					return super.parseNetworkResponse(response);
				}

				@NonNull
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError
				{
					return headers;
				}

			};

			jsonObjReq.setRetryPolicy(retryPolicy);
			// Adding request to request queue
			HTApplication.getInstance().addToRequestQueue(jsonObjReq, request_tag);
		}
		else
		{
			volleyCallsBackListener.onVolleyError(AppConstant.INTERNET_DISCONNECT, request_id);
		}
	}

	public void DELETERequest(String url, String request_tag, final int request_id)
	{
		if (AppUtil.isConnected(context))
		{
			JsonRequest jsonObjReq = new JsonObjectRequest(
					Request.Method.DELETE,
					url,
					(String) null,

					new Response.Listener<JSONObject>()
					{

						@Override
						public void onResponse(@NonNull JSONObject response)
						{
							LogUtils.i(TAG, response.toString());


							volleyCallsBackListener.onVolleySuccess(response.toString(), request_id);

						}
					},

					new Response.ErrorListener()
					{
						@Override
						public void onErrorResponse(@NonNull VolleyError error)
						{
							VolleyLog.d(TAG, "Error: " + error.getMessage());

							if (error.networkResponse != null && error.networkResponse.data != null)
							{
								volleyCallsBackListener.onVolleyError(new String(error.networkResponse.data), request_id);
								VolleyLog.d(TAG, "Error: " + new String(error.networkResponse.data));
							}
							else
							{
								volleyCallsBackListener.onVolleyError(error.toString(), request_id);
							}

						}
					});

			jsonObjReq.setRetryPolicy(retryPolicy);
			// Adding request to request queue
			HTApplication.getInstance().addToRequestQueue(jsonObjReq, request_tag);

		}
		else
		{
			volleyCallsBackListener.onVolleyError(AppConstant.INTERNET_DISCONNECT, request_id);
		}
	}

	public void cancelPendingRequestsByTag(String tag)
	{
		HTApplication.getInstance().cancelPendingRequests(tag);
	}

	public void cancelAllPendingRequests()
	{
		HTApplication.getInstance().cancelAllPendingRequests();
	}
}
