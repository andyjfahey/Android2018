package uk.co.healtht.healthtouch.network.pushnotification;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.util_helpers.LogUtils;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;

public class RegistrationIntentService extends IntentService
{
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public RegistrationIntentService(String name)
	{
		super(name);
	}

	public RegistrationIntentService()
	{
		super("RegistrationService");
	}
	//	public static final String TOKEN_SENT = "IsTokenSent";
	//	public static final String TOKEN_VALUE = "TokenSentValue";
	//	private static final String TAG = "RegistrationService";
	//	private SharedPreferences sharedPreferences;

	//	Response.Listener<String> responseListener = new Response.Listener<String>()
	//	{
	//		@Override
	//		public void onResponse(String response)
	//		{
	//			sharedPreferences.edit().putBoolean(TOKEN_SENT, true).apply();
	//		}
	//	};
	//
	//	Response.ErrorListener errorListener = new Response.ErrorListener()
	//	{
	//		@Override
	//		public void onErrorResponse(VolleyError error)
	//		{
	//			error.printStackTrace();
	//			sharedPreferences.edit().putBoolean(TOKEN_SENT, false).apply();
	//		}
	//	};
	//
	//	Response.Listener<String> unregisterResponseListener = new Response.Listener<String>()
	//	{
	//		@Override
	//		public void onResponse(String response)
	//		{
	//			sharedPreferences.edit().putBoolean(TOKEN_SENT, false).apply();
	//		}
	//	};
	//
	//	Response.ErrorListener unregisterErrorListener = new Response.ErrorListener()
	//	{
	//		@Override
	//		public void onErrorResponse(VolleyError error)
	//		{
	//			error.printStackTrace();
	//		}
	//	};

	//	public RegistrationIntentService()
	//	{
	//		super(TAG);
	//	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		//		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		try
		{
			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(getString(R.string.gcm_sender_id),
					GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

			HTApplication.preferencesManager.setStringValue(PreferencesManager.DEVICE_TOKEN, token);
			//			List<HTDeviceRegistration> htDeviceRegistrations = new HTDeviceRegistrationDelegate().getAllWhereDeleteAtIsNull();
			//			HTDeviceRegistration htDeviceRegistration = null;

			//			if (htDeviceRegistrations != null && htDeviceRegistrations.size() > 0)
			//			{
			//				htDeviceRegistration = htDeviceRegistrations.get(0);
			//			}
			//
			//			if (htDeviceRegistration == null)
			//			{
			//				htDeviceRegistration = new HTDeviceRegistration();
			//				htDeviceRegistration.entity = htDeviceRegistration.getClass().getSimpleName();
			//				htDeviceRegistration.created_at = new Date(System.currentTimeMillis());
			//			}
			//
			//			htDeviceRegistration.updated_at = new Date(System.currentTimeMillis());
			//			htDeviceRegistration.synced = false;
			//			htDeviceRegistration.token = token;
			//
			//			new HTDeviceRegistrationDelegate().add(htDeviceRegistration);


			//			boolean sent = sharedPreferences.getBoolean(TOKEN_SENT, false);
			//			if (sent)
			//			{
			//				sendUnregisterToServer();
			//			}

			LogUtils.d("Token", token);
			//			sendRegistrationToServer(token);
		}
		catch (Exception ex)
		{
			Crash.logException(ex);
			//			sharedPreferences.edit().putBoolean(TOKEN_SENT, false).apply();
		}
	}

	//	private void sendUnregisterToServer()
	//	{
	//		HTApplication application = HTApplication.getInstance();
	//		final ApiProvider apiProvider = application.getApiProvider();
	//		final TokenRegistrationRequest request = new TokenRegistrationRequest();
	//		request.deviceToken = sharedPreferences.getString(TOKEN_VALUE, "");
	//
	//		if (request.deviceToken.equals(""))
	//		{
	//			return;
	//		}
	//
	//		request.deviceType = "android";
	//		request.userId = apiProvider.getSettingsUser().getUserUri().replace("/users/", "");
	//		apiProvider.unregisterToken(unregisterResponseListener, unregisterErrorListener, request);
	//	}
	//
	//	private void sendRegistrationToServer(String token)
	//	{
	//		HTApplication application = HTApplication.getInstance();
	//		final ApiProvider apiProvider = application.getApiProvider();
	//
	//		final TokenRegistrationRequest request = new TokenRegistrationRequest();
	//		request.deviceToken = token;
	//		request.deviceType = "android";
	//		request.userId = apiProvider.getSettingsUser().getUserUri().replace("/users/", "");
	//		sharedPreferences.edit().putString(TOKEN_VALUE, token).apply();
	//		apiProvider.registerToken(responseListener, errorListener, request);
	//	}
}
