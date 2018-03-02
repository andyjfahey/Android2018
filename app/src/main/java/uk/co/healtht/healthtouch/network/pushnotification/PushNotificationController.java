package uk.co.healtht.healthtouch.network.pushnotification;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PushNotificationController
{
	private Activity activity;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public PushNotificationController(Activity activity)
	{
		this.activity = activity;

		if (checkPlayServices())
		{
			Intent intent = new Intent(activity, RegistrationIntentService.class);
			activity.startService(intent);
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	public boolean checkPlayServices()
	{
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (apiAvailability.isUserResolvableError(resultCode))
			{
				apiAvailability.getErrorDialog(activity, resultCode,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			return false;
		}
		return true;
	}

}
