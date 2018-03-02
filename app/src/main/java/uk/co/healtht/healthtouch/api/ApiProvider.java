package uk.co.healtht.healthtouch.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.debug.Debug;
import uk.co.healtht.healthtouch.debug.DebugDataProvider;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.proto.CareMyNetworkReply;
import uk.co.healtht.healthtouch.proto.CareNetworkRequestConfirmReply;
import uk.co.healtht.healthtouch.proto.CareServiceReply;
import uk.co.healtht.healthtouch.proto.FormReply;
import uk.co.healtht.healthtouch.proto.FormRequestReply;
import uk.co.healtht.healthtouch.proto.MedicationReply;
import uk.co.healtht.healthtouch.proto.Monitor;
import uk.co.healtht.healthtouch.proto.NotificationReply;
import uk.co.healtht.healthtouch.proto.PasswordPost;
import uk.co.healtht.healthtouch.proto.Reply;
import uk.co.healtht.healthtouch.proto.ThresholdRange;
import uk.co.healtht.healthtouch.proto.TokenRegistrationRequest;
import uk.co.healtht.healthtouch.proto.TrackerEntriesReply;
import uk.co.healtht.healthtouch.proto.TrackerPostReply;
import uk.co.healtht.healthtouch.proto.TrackerReply;
import uk.co.healtht.healthtouch.proto.TrackerUser;
import uk.co.healtht.healthtouch.proto.UserPost;
import uk.co.healtht.healthtouch.proto.UserReply;
import uk.co.healtht.healthtouch.settings.SettingsApplication;
import uk.co.healtht.healthtouch.settings.SettingsDebug;
import uk.co.healtht.healthtouch.settings.SettingsLastUser;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;

import static uk.co.healtht.healthtouch.api.EndPointMethod.DELETE;
import static uk.co.healtht.healthtouch.api.EndPointMethod.GET;
import static uk.co.healtht.healthtouch.api.EndPointMethod.POST;
import static uk.co.healtht.healthtouch.api.EndPointMethod.PUT;

//import uk.co.healtht.healthtouch.proto.DateResponse;

public class ApiProvider
{
	private static final int SYNC_INTERVAL = 60;
	private LruCache<String, EndPointProvider> providerCache;
	protected CommsProcessor comms;
	protected SettingsUser settingsUser;
	protected SettingsLastUser settingsLastUser;
	private SettingsApplication settingsApp;
	private UiProvider uiProvider;
	protected DefaultDataConverter dataConverter;
	protected DebugDataProvider debugDataProvider;
	protected Context ctx;
	private ApiCache apiCache;

	// TODO: We have a caching problem... some get/post uri are the same. We need to add a extra "get"/"set" to the uri cache, or not cache "post" at all...

	public ApiProvider(Context ctx, SettingsUser settingsUser, SettingsApplication settingsApp, UiProvider uiProvider, SettingsLastUser settingsLastUser)
	{
		this.ctx = ctx;
		this.settingsUser = settingsUser;
		this.settingsApp = settingsApp;
		this.uiProvider = uiProvider;
		this.settingsLastUser = settingsLastUser;

		this.providerCache = new LruCache<>(25);
		this.dataConverter = new DefaultDataConverter();
		this.debugDataProvider = new DebugDataProvider();

		this.comms = new CommsProcessor(ctx, settingsApp.getDeviceId());
		comms.setServer(SettingsDebug.getDebugServer());
		comms.setSession(settingsUser.getSession());

		apiCache = new ApiCache(ctx);
	}

	public SettingsUser getSettingsUser()
	{
		if (settingsUser == null)
		{
			settingsUser = new SettingsUser(ctx);
		}
		return settingsUser;
	}

	public SettingsApplication getSettingsApplication()
	{
		return settingsApp;
	}

	public UiProvider getUiProvider()
	{
		return uiProvider;
	}

	public ApiCache getApiCache()
	{
		return apiCache;
	}

	public boolean isLoggedIn()
	{
		return (settingsUser.getSession() != null);
	}

	public void logout()
	{
		if (isLoggedIn())
		{
			if (BuildConfig.DEBUG)
			{
				Log.v(SettingsDebug.TAG, "[Session] logout()");
			}
			comms.setSession(null);
			settingsUser.setSession(null);
			uiProvider.broadcastEvent(UiProvider.UiEvent.LOGOUT, null);
		}
		else if (BuildConfig.DEBUG)
		{
			Log.v(SettingsDebug.TAG, "[Session] logout() - Ignored");
		}
	}

	public void clearData()
	{

		// Clear data when login with another account
		settingsUser.cleanAll();
		cacheInvalidateAll();
		providerCache.evictAll();
		providerCache = new LruCache<>(25);
	}

	public void setServer(String server)
	{
		comms.setServer(server);
		cacheInvalidateAll();
	}

	public EndPointProvider getLogin(final String user, String pass)
	{
		//		String uri = "/api/v1/users/" + urlEncode(user);
		String uri = "/api/v1/ValidateLogin/";
		EndPointProvider res = providerCache.get(uri);
		if (res == null)
		{
			EndPoint endPoint = new EndPoint(GET, uri, UserReply.class);
			//			EndPoint endPoint = new EndPoint(GET, uri, Reply.class);
			res = new DefaultProvider(this, endPoint);
			res.addListener(new ApiProviderListenerImpl()
			{
				@Override
				public void onDataLoaded(EndPointProvider provider, Object providerData)
				{
					loginDataLoaded(user, provider);
				}
			});
		}

		saveHeaderAuthorization(res, user, pass);

		return res;
	}

	public void loginDataLoaded(String user, EndPointProvider provider)
	{
		Log.d("taguu", "loginDataLoaded");
		//        dispatchPendingRequests();

		Log.e("ApiProvider", "loginDataLoaded");
		scheduleSync(user, null, SYNC_INTERVAL);
		saveSession(provider.getRequestHeaders().get(CommsProcessor.HEADER_AUTHORIZATION), null, true);
		HTApplication.getInstance().sync();
	}

	public void saveHeaderAuthorization(EndPointProvider res, String user, String pass)
	{
		// http://stackoverflow.com/questions/1968416/how-to-do-http-authentication-in-android
		HashMap<String, String> headers = new HashMap<>();
		headers.put(CommsProcessor.HEADER_AUTHORIZATION, getAuthorisation(user, pass));
		res.setRequestHeaders(headers);
	}

	public void startSyncImmediately(String user, String pass)
	{
		Log.d("taguu", "startSyncImmediately");
		AccountManager accountManager = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
		final Account account = new Account(user, ctx.getString(R.string.account_type));
		accountManager.addAccountExplicitly(account, pass, null);
		String provider = ctx.getString(R.string.provider_name);
		Bundle syncSettings = new Bundle();
		syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(account, provider, syncSettings);
	}

	public void scheduleSync(final String user, final String pass, final int interval)
	{
		Log.d("taguu", "scheduleSync");

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				AccountManager accountManager = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
				final Account account = new Account(user, ctx.getString(R.string.account_type));
				accountManager.addAccountExplicitly(account, pass, null);
				String provider = ctx.getString(R.string.provider_name);
				ContentResolver.setIsSyncable(account, provider, 1);
				ContentResolver.setSyncAutomatically(account, provider, true);
				/*ContentResolver.addPeriodicSync(
				        account,
                        provider,
                        Bundle.EMPTY,
                        interval);*/
				ContentResolver.requestSync(account,
						provider,
						Bundle.EMPTY);
			}
		}).start();
	}


	private String user;
	private Handler handler;

	public void startSynchroIfNewDate(final String user)
	{
		this.user = user;

		if (handler == null)
		{
			Log.e("ApiProvider", "startSynchroIfNewDate");
			handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					//Do something after 100ms
					//Toast.makeText(ctx, "synchro", Toast.LENGTH_SHORT).show();

					//getLastDate(user);
					//					syncWithWeb();
					handler.postDelayed(this, SYNC_INTERVAL * 1000);
				}
			}, SYNC_INTERVAL * 1000);
		}
	}

	private RequestQueue queue;

	//	private void getLastDate(final String user)
	//	{
	//		Log.e("ApiProvider", "getLastDate");
	//		String baseUrl = HTApplication.getInstance().getApiProvider().getComms().getServer();
	//
	//		String url = baseUrl + "/api/v1/tracking/myLastTrackedDate";
	//		queue = Volley.newRequestQueue(ctx);
	//
	//		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
	//				new Response.Listener<String>()
	//				{
	//					@Override
	//					public void onResponse(String response)
	//					{
	//						Log.d("taguu", "callSynchronization onResponse: " + response);
	//
	//						try
	//						{
	//							JSONObject jsonObject = new JSONObject(response);
	//							String data = jsonObject.getString("data");
	//
	//
	//							String synch_date = SharredUtils.getValue(ctx, "synch_date");
	//							//2016-06-24 00:30:36
	//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	//							if (synch_date != null && data != null)
	//							{
	//								try
	//								{
	//									Date date = format.parse(synch_date);
	//									Date dateServer = format.parse(data);
	//
	//									Log.e("taguu", "data: " + date);
	//									Log.e("taguu", "dateServer: " + dateServer);
	//									Log.e("taguu", "data: " + synch_date);
	//									Log.e("taguu", "dateServer: " + data);
	//									Log.e("taguu", "callSynchronization date.getTime(): " + date.getTime());
	//									Log.e("taguu", "callSynchronization dateServer.getTime(): " + dateServer.getTime());
	//									if (dateServer.getTime() > date.getTime())
	//									{
	//
	//										// If new date do this, else do nothing
	//										AccountManager accountManager = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
	//										final Account account = new Account(user, ctx.getString(R.string.account_type));
	//										accountManager.addAccountExplicitly(account, null, null);
	//										String provider = ctx.getString(R.string.provider_name);
	//										ContentResolver.removePeriodicSync(account,
	//												provider,
	//												Bundle.EMPTY);
	//										ContentResolver.requestSync(account,
	//												provider,
	//												Bundle.EMPTY);
	//									}
	//									SharredUtils.saveValue(ctx, "synch_date", data);
	//
	//								}
	//								catch (ParseException e)
	//								{
	//									e.printStackTrace();
	//								}
	//							}
	//
	//						}
	//						catch (JSONException e)
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//				}, new Response.ErrorListener()
	//		{
	//			@Override
	//			public void onErrorResponse(VolleyError error)
	//			{
	//				Log.e("taguu", "callSynchronization onErrorResponse: " + error);
	//			}
	//		}
	//		)
	//		{
	//			@Override
	//			public Map<String, String> getHeaders() throws AuthFailureError
	//			{
	//				Map<String, String> headers = HTApplication.getInstance().getApiProvider().getComms().getAuthHeaders();
	//
	//				Iterator myVeryOwnIterator = headers.keySet().iterator();
	//				while (myVeryOwnIterator.hasNext())
	//				{
	//					String key = (String) myVeryOwnIterator.next();
	//					String value = (String) headers.get(key);
	//					Log.d("", "Key: " + key + " Value: " + value);
	//				}
	//				return headers;
	//			}
	//		};
	//
	//		RetryPolicy retryPolicy = new DefaultRetryPolicy(
	//				10000,
	//				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
	//				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
	//		);
	//		stringRequest.setRetryPolicy(retryPolicy);
	//		queue.add(stringRequest);
	//
	//	}


	//	public void resetLastDate()
	//	{
	//		Log.e("taguu", "resetLastDate");
	//		SharredUtils.saveValue(ctx, "synch_date", " 2010-01-01 00:00:00");
	//		startSynchroIfNewDate(getLastUser().getEmail());
	//	}

	public EndPointProvider getUser()
	{
		String uri = "/api/v1" + settingsUser.getUserUri();
		EndPointProvider res = providerCache.get(uri);
		if (res == null || !res.isValid())
		{
			EndPoint endPoint = new EndPoint(GET, uri, UserReply.class);
			res = new DefaultProvider(this, endPoint);

			if (!Platform.hasNetworkConnection(ctx))
			{
				res.setValid(false);
			}

			if (res != null && res.getResult() != null)
			{
				providerCache.put(uri, res);
			}
		}
		res = saveNewDataOrGetOldData(uri, res);

		return res;
	}

	public EndPointProvider getUserCacheless()
	{
		String uri = "/api/v1" + settingsUser.getUserUri();
		EndPoint endPoint = new EndPoint(GET, uri, UserReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getPostUser()
	{
		//resetLastDate();
		String uri = "/api/v1/users";
		EndPointProvider res = providerCache.get(uri);
		if (res == null)
		{
			EndPoint endPoint = new EndPoint(POST, uri, UserReply.class);
			res = new DefaultProvider(this, endPoint);
			if (res != null)
			{
				providerCache.put(uri, res);
			}
			res.addListener(new ApiProviderListenerImpl()
			{

				@Override
				public void onDataLoaded(EndPointProvider provider, Object providerData)
				{
					UserPost userPost = (UserPost) provider.getLastRequestedData();
					String authId = getAuthorisation(userPost.username, userPost.password);
					saveSession(authId, (UserReply) providerData, true);
					// Launch synchronization
					HTApplication.getInstance().sync();
				}
			});
		}

		return res;
	}

	public void registerToken(Response.Listener<String> responseListener, Response.ErrorListener errorListener, final TokenRegistrationRequest tokenObject)
	{
		//port this out and replace on production!!!
		//resetLastDate();
		String uri;
		if (CommsProcessor.SERVER_IS_PROD)
		{
			uri = "http://push.healthtouchmobile.com/register";
		}
		else
		{
			uri = "http://pushdev.healthtouchmobile.com/register";
		}
		StringRequest postRequest = new StringRequest(Request.Method.POST, uri, responseListener, errorListener)
		{
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<>();
				params.put("deviceToken", tokenObject.deviceToken);
				params.put("deviceType", tokenObject.deviceType);
				params.put("userId", tokenObject.userId);
				return params;
			}
		};

		comms.queue.add(postRequest);
	}

	public void unregisterToken(Response.Listener<String> responseListener, Response.ErrorListener errorListener, final TokenRegistrationRequest tokenObject)
	{
		// port this out and replace on production!!!
		//resetLastDate();
		String uri;
		if (CommsProcessor.SERVER_IS_PROD)
		{
			uri = "http://push.healthtouchmobile.com/unregister";
		}
		else
		{
			uri = "http://pushdev.healthtouchmobile.com/unregister";
		}
		StringRequest postRequest = new StringRequest(Request.Method.POST, uri, responseListener, errorListener)
		{
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<>();
				params.put("deviceToken", tokenObject.deviceToken);
				params.put("deviceType", tokenObject.deviceType);
				params.put("userId", tokenObject.userId);
				return params;
			}
		};

		comms.queue.add(postRequest);
	}

	public EndPointProvider getUpdateUser()
	{
		String uri = "/api/v1" + settingsUser.getUserUri();
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{
			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getUser().invalidateData();
			}
		});

		return res;
	}

	public EndPointProvider getUpdatePassword(String currentPassword)
	{
		//resetLastDate();
		String uri = "/api/v1/users/changepassword";
		EndPoint endPoint = new EndPoint(POST, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{
			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				PasswordPost userPost = (PasswordPost) provider.getLastRequestedData();
				String userId = settingsApp.getUserLogin();
				String authId = getAuthorisation(userId, userPost.password);

				saveSession(authId, null, false);
			}
		});

		HashMap<String, String> headers = new HashMap<>();
		String userId = settingsApp.getUserLogin();
		headers.put(CommsProcessor.HEADER_AUTHORIZATION, getAuthorisation(userId, currentPassword));
		res.setRequestHeaders(headers);

		return res;
	}

	@SuppressWarnings("unused")
	public EndPointProvider getDeleteUser(String user)
	{
		String uri = "/api/v1/users/" + urlEncode(user);
		EndPoint endPoint = new EndPoint(DELETE, uri, UserPost.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				uiProvider.broadcastEvent(UiProvider.UiEvent.LOGOUT, null);
			}
		});

		return res;
	}

	public EndPointProvider getPostResetPassword()
	{
		//resetLastDate();
		String uri = "/api/v1/users/resetpassword";
		EndPoint endPoint = new EndPoint(POST, uri, Reply.class);
		return new DefaultProvider(this, endPoint);
	}

	public EndPointProvider getTrackers()
	{
		String uri = "/api/v1/trackers?monitors=1";
		EndPointProvider res = providerCache.get(uri);
		if (res == null || !res.isValid())
		{
			EndPoint endPoint = new EndPoint(GET, uri, TrackerReply.class);
			res = new DefaultProvider(this, endPoint);

			if (!Platform.hasNetworkConnection(ctx))
			{
				res.setValid(false);
			}
			if (res != null)
			{
				providerCache.put(uri, res);
			}
		}

		res = saveNewDataOrGetOldData(uri, res);

		return res;
	}

	public EndPointProvider getTrackersCacheless()
	{
		String uri = "/api/v1/trackers?monitors=1";
		EndPoint endPoint = new EndPoint(GET, uri, TrackerReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getTrackerEntries(String trackerType)
	{
		// We use limit and page parameters
		String uri = "/api/v1/tracking/" + trackerType;
		EndPointProvider res = providerCache.get(uri);
		//request newest data unless we're offline
		if (res == null)
		{
			EndPoint endPoint = new EndPoint(GET, uri, TrackerEntriesReply.class);
			res = new DefaultProvider(this, endPoint);
			if (res != null)
			{
				providerCache.put(uri, res);
			}
		}
		return res;
	}

	public EndPointProvider getTrackerEntriesCacheless(String trackerType)
	{
		String uri = "/api/v1/tracking/" + trackerType;
		EndPoint endPoint = new EndPoint(GET, uri, TrackerEntriesReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getPostTracker(String trackerType)
	{
		//resetLastDate();
		String uri = "/api/v1/tracking/" + trackerType;
		EndPoint endPoint = new EndPoint(POST, uri, TrackerPostReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{
			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// TODO: NEEDS TO INVALIDATE CACHE... But what is the track entry?
			}

			@Override
			public void onDataLoadedError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode)
			{
				super.onDataLoadedError(provider, errorObj, debugMsg, errorCode);

				// TODO: This is not good use RequestPending instead
				apiCache.addPendingRequest(provider);
			}
		});

		return res;
	}

	@SuppressWarnings("unchecked")
	public EndPointProvider getPutThreshold(final String trackerUri)
	{
		String uri = "/api/v1" + trackerUri + "/monitoring";
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// TODO: Is this a good way to update the cache?
				EndPointProvider userProvider = getUser();
				UserReply userReply = (UserReply) userProvider.getResult();
				TrackerUser trackerUser = userReply.data.getTrackerUser(trackerUri);
				HashMap<String, Map<String, ThresholdRange>> trackerPost = (HashMap<String, Map<String, ThresholdRange>>) provider.getLastRequestedData();

				trackerUser.thresholds = (HashMap<String, ThresholdRange>) trackerPost.get("thresholds");
				HTApplication application = HTApplication.getInstance();
				application.getSyncDb().saveProvider(provider.getEndPoint().getUri(), provider);
				application.getSyncDb().saveProvider(userProvider.getEndPoint().getUri(), userProvider);
				//                application.sync();
			}
		});

		return res;
	}

	public EndPointProvider getUpdateMonitor(String monitorUri)
	{
		String uri = "/api/v1/trackers" + monitorUri; // /monitors/241
		EndPoint endPoint = new EndPoint(PUT, uri, Monitor.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// TODO: NEEDS TO INVALIDATE CACHE... But what is the entry?
			}
		});

		return res;
	}

	public EndPointProvider getDeleteMonitor(String monitorUri)
	{
		String uri = "/api/v1/trackers" + monitorUri; // /monitors/241
		EndPoint endPoint = new EndPoint(DELETE, uri, Monitor.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// TODO: NEEDS TO INVALIDATE CACHE... But what is the entry?
			}
		});

		return res;
	}

	public EndPointProvider getCreateMonitor(String trackerUri)
	{
		//resetLastDate();
		String uri = "/api/v1" + trackerUri + "/reminders";
		EndPoint endPoint = new EndPoint(POST, uri, Monitor.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// TODO: NEEDS TO INVALIDATE CACHE... But what is the entry?
			}
		});

		return res;
	}

	public EndPointProvider getDeleteTrackerEntry(String entryUri)
	{
		String uri = "/api/v1" + entryUri;
		EndPoint endPoint = new EndPoint(DELETE, uri, Reply.class);

		// TODO: NEEDS TO INVALIDATE CACHE... But what is the track entry?
		return new DefaultProvider(this, endPoint);
	}

	public EndPointProvider getNotifications()
	{
		// String uri = "/api/v1/notifications/all?limit=30";
		String uri = "/api/v1/notifications?limit=30";
		EndPointProvider res = providerCache.get(uri);
		if (res == null || !res.isValid())
		{
			EndPoint endPoint = new EndPoint(GET, uri, NotificationReply.class);
			res = new DefaultProvider(this, endPoint);

			if (!Platform.hasNetworkConnection(ctx))
			{
				res.setValid(false);
			}
			if (res != null)
			{
				providerCache.put(uri, res);
			}
		}

		res = saveNewDataOrGetOldData(uri, res);

		return res;
	}

	public EndPointProvider getNotificationsCacheless()
	{
		String uri = "/api/v1/notifications?limit=30";
		EndPoint endPoint = new EndPoint(GET, uri, NotificationReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getSeenNotification(String notificationUri)
	{
		String uri = "/api/v1" + notificationUri;
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		return new DefaultProvider(this, endPoint);
	}

	public EndPointProvider getDeleteNotification(String notificationUri)
	{
		String uri = "/api/v1" + notificationUri;
		EndPoint endPoint = new EndPoint(DELETE, uri, Reply.class);
		return new DefaultProvider(this, endPoint);
	}

	public EndPointProvider getCreateMedication()
	{
		//resetLastDate();
		String uri = "/api/v1/medications";
		EndPoint endPoint = new EndPoint(POST, uri, MedicationReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getUser().invalidateData(); // The medication list is a field of user
			}
		});

		return res;
	}

	public EndPointProvider getDeleteMedication(String medicationUri)
	{
		String uri = "/api/v1" + medicationUri;
		EndPoint endPoint = new EndPoint(DELETE, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getUser().invalidateData(); // The medication list is a field of user
			}
		});

		return res;
	}

	public EndPointProvider getForm(String formUri)
	{
		String uri = "/api/v1" + formUri;
		EndPointProvider res = providerCache.get(uri);
		if (res == null)
		{
			EndPoint endPoint = new EndPoint(GET, uri, FormReply.class);
			res = new DefaultProvider(this, endPoint);
			if (res != null)
			{
				providerCache.put(uri, res);
			}
		}

		return res;
	}

	public EndPointProvider getFormCacheless(String formUri)
	{
		String uri = "/api/v1" + formUri;
		EndPoint endPoint = new EndPoint(GET, uri, FormReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getPostForm()
	{
		//resetLastDate();
		String uri = "/api/v1/gform/complete";
		EndPoint endPoint = new EndPoint(POST, uri, FormRequestReply.class);
		return new DefaultProvider(this, endPoint);
	}


	public EndPointProvider getUpdateMedication(String medicationUri)
	{
		String uri = "/api/v1" + medicationUri;
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getUser().invalidateData(); // The medication list is a field of user
			}
		});

		return res;
	}

	public EndPointProvider getCareNetworkServices()
	{
		String uri = "/api/v1/services?all=true";
		EndPointProvider res = providerCache.get(uri);
		//Platform.hasNetworkConnection(ctx)
		if (res == null)
		{
			EndPoint endPoint = new EndPoint(GET, uri, CareServiceReply.class);
			res = new DefaultProvider(this, endPoint);
			res.addListener(new ApiProviderListenerImpl()
			{

				@Override
				public void onDataLoaded(EndPointProvider provider, Object providerData)
				{
					// getUser().invalidateData(); // TODO
					HTApplication.getInstance().getSyncDb().saveProvider(provider.getEndPoint().getUri(), provider);
				}
			});
		}

		return res;
	}

	public EndPointProvider getCareNetworkServicesCacheless()
	{
		String uri = "/api/v1/services?all=true";
		EndPoint endPoint = new EndPoint(GET, uri, CareServiceReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getCareNetworkMyList()
	{
		String uri = "/api/v1" + settingsUser.getUserUri() + "/networks";
		EndPointProvider res = providerCache.get(uri);
		if (res == null || !res.isValid())
		{
			EndPoint endPoint = new EndPoint(GET, uri, CareMyNetworkReply.class);
			res = new DefaultProvider(this, endPoint);

			if (!Platform.hasNetworkConnection(ctx))
			{
				res.setValid(false);
			}
			if (res != null)
			{
				providerCache.put(uri, res);
			}
		}

		res = saveNewDataOrGetOldData(uri, res);

		return res;
	}

	public EndPointProvider getCareNetworkMyListCacheless()
	{
		String uri = "/api/v1" + settingsUser.getUserUri() + "/networks";
		EndPoint endPoint = new EndPoint(GET, uri, CareMyNetworkReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		if (res != null)
		{
			providerCache.put(uri, res);
		}
		return res;
	}

	public EndPointProvider getCareNetworkRequest(String serviceUri)
	{
		//resetLastDate();
		String uri = "/api/v1/network/request?service=" + serviceUri;
		EndPoint endPoint = new EndPoint(POST, uri, CareNetworkRequestConfirmReply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getCareNetworkMyList().invalidateData();
			}
		});

		return res;
	}

	public EndPointProvider getCareNetworkDelete(String serviceUri)
	{
		String uri = "/api/v1/network" + serviceUri;
		EndPoint endPoint = new EndPoint(DELETE, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getCareNetworkMyList().invalidateData();
			}
		});

		return res;
	}

	@SuppressWarnings("unused")
	public EndPointProvider getCareNetworkAccept(String serviceUri)
	{
		String uri = "/api/v1/network" + serviceUri + "/accept";
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				// getUser().invalidateData(); // TODO
			}
		});

		return res;
	}

	public EndPointProvider acceptCareNetwork(String uriNetwork)
	{

		Log.e(getClass().getSimpleName(), "id =" + uriNetwork.substring(7));
		String uri = "/api/v1/network/accept?id=" + uriNetwork.substring(7);
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getCareNetworkMyList().invalidateData();
			}
		});

		return res;
	}

	public EndPointProvider rejectCareNetwork(String uriNetwork)
	{

		Log.e(getClass().getSimpleName(), "id =" + uriNetwork.substring(7));
		String uri = "/api/v1/network/reject?id=" + uriNetwork.substring(7);
		EndPoint endPoint = new EndPoint(PUT, uri, Reply.class);
		EndPointProvider res = new DefaultProvider(this, endPoint);
		res.addListener(new ApiProviderListenerImpl()
		{

			@Override
			public void onDataLoaded(EndPointProvider provider, Object providerData)
			{
				getCareNetworkMyList().invalidateData();
			}
		});

		return res;
	}

	//	// TODO
	//	public EndPointProvider getSynchronizationDate()
	//	{
	//		String uri = "/api/v1/tracking/myLastTrackedDate";
	//		EndPointProvider res = providerCache.get(uri);
	//		if (res == null || !res.isValid())
	//		{
	//			EndPoint endPoint = new EndPoint(GET, uri, DateResponse.class);
	//			res = new DefaultProvider(this, endPoint);
	//
	//			if (!Platform.hasNetworkConnection(ctx))
	//			{
	//				res.setValid(false);
	//			}
	//			if (res != null)
	//			{
	//				providerCache.put(uri, res);
	//			}
	//		}
	//
	//		res = saveNewDataOrGetOldData(uri, res);
	//
	//		return res;
	//	}

	//	public EndPointProvider getSynchronizationDateCache()
	//	{
	//		String uri = "/api/v1/tracking/myLastTrackedDate";
	//		EndPoint endPoint = new EndPoint(GET, uri, DateResponse.class);
	//		EndPointProvider res = new DefaultProvider(this, endPoint);
	//		if (res != null)
	//		{
	//			providerCache.put(uri, res);
	//		}
	//		return res;
	//	}

	public void checkSyncEnabled()
	{
		Log.e("ApiProvider", "checkSyncEnabled");
		//check if sync is on, otherwise start sync
		AccountManager am = AccountManager.get(ctx);
		Account[] accounts = am.getAccountsByType(ctx.getString(R.string.account_type));

		if (accounts.length <= 0)
		{
			return;
		}

		Account account = accounts[0];
		if (!ContentResolver.isSyncActive(account, ctx.getString(R.string.provider_name)))
		{
			Log.e("ApiProvider", "checkSyncEnabled scheduleSync");
			scheduleSync(account.name, null, SYNC_INTERVAL);
		}
	}

	/**
	 * Called from the network state monitoring Receiver, and when the app starts.
	 * Check network availability before calling this method, to avoid redundant work.
	 */
	public void dispatchPendingRequests(ApiProviderListenerImpl dispatchPendingListener)
	{
		Map.Entry<String, EndPointRequest> reqEntry = apiCache.peekFirstPendingRequest();
		if (reqEntry != null)
		{
			EndPointRequest req = reqEntry.getValue();
			EndPointProvider res = new DefaultProvider(this, req.endPoint);

			if (settingsUser == null || settingsUser.getEmail() == null || !settingsUser.getEmail().equals(res.endPoint.getUser()))
			{
				HashMap<String, String> headers = new HashMap<>();
				headers.put("AUTHORIZATION", res.endPoint.getAuthHeader());
				res.setRequestHeaders(headers);
				res.setListener(new ArrayList<ApiProviderListener>());
			}
			res.addListener(dispatchPendingListener);

			switch (res.endPoint.getMethod())
			{
				case POST:
					//resetLastDate();
					res.post(req.request);
					apiCache.removeFirstPendingRequest();
					break;

				case PUT:
					//resetLastDate();
					res.put(req.request);
					apiCache.removeFirstPendingRequest();
					break;

				case DELETE:
					//resetLastDate();
					res.delete();
					apiCache.removeFirstPendingRequest();
					break;
				default:
					Debug.showErrorToast("dispatchPendingRequests");
			}
		}
	}

	public EndPointProvider containsListener(ApiProviderListener l)
	{
		for (Map.Entry<String, EndPointProvider> entry : providerCache.snapshot().entrySet())
		{
			if (entry.getValue().containsListener(l))
			{
				return entry.getValue();
			}
		}

		return null;
	}

	private void cacheInvalidateAll()
	{
		for (Map.Entry<String, EndPointProvider> entry : providerCache.snapshot().entrySet())
		{
			entry.getValue().clearData();
		}
	}

	protected void handleDataLoadedError(int errorCode, EndPoint endPoint)
	{
		// Not sure about 403... We can get this if we switch server, and the user uri changes
		if (errorCode == 401 || errorCode == 403)
		{
			if (!endPoint.getUri().endsWith("/changepassword"))
			{
				logout();
			}
		}
	}

	private String urlEncode(String str)
	{
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			Crash.logException(e);
		}

		return str;
	}

	public LruCache<String, EndPointProvider> getProviderCache()
	{
		return providerCache;
	}

	public String getAuthorisation(String user, String pass)
	{
		// http://stackoverflow.com/questions/1968416/how-to-do-http-authentication-in-android
		//        HashMap<String, String> headers = new HashMap<>();
		byte[] bytes = (user + ":" + pass).getBytes();
		return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	public void saveSession(String sessionId, UserReply msg, boolean isLogin)
	{
		settingsUser.setSession(sessionId);
		comms.setSession(sessionId);
		settingsLastUser.copyFromUserSettings(settingsUser);

		if (isLogin)
		{
			uiProvider.broadcastEvent(UiProvider.UiEvent.LOGIN, msg);
		}
	}


	public CommsProcessor getComms()
	{
		return comms;
	}

	public DefaultDataConverter getDataConverter()
	{
		return dataConverter;
	}

	public SettingsLastUser getLastUser()
	{
		return settingsLastUser;
	}


	private EndPointProvider saveNewDataOrGetOldData(String uri, EndPointProvider res)
	{
		SettingsUser settingsUser = new SettingsUser(ctx);
		String postfix = "!:LKKE!";
		String user = settingsUser.getEmail() + postfix;
		if (res != null && res.getResult() != null)
		{
			providerCache.put(uri, res);
			HTApplication.getInstance().getSyncDb().saveProvider(user + uri, res);
		}
		else
		{
			res = HTApplication.getInstance().getSyncDb().getProvider(user + uri);
		}

		return res;
	}
}
