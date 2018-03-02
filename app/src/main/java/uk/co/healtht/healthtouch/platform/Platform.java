package uk.co.healtht.healthtouch.platform;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import java.util.GregorianCalendar;

/**
 * This class contains code specific to different android platforms (i.e. Google Play Store vs Amazon)
 */
public final class Platform
{
	public static final String version = "1.3.0";
	public static final long buildDate = new GregorianCalendar(2017, GregorianCalendar.AUGUST, 17).getTimeInMillis();
	public static final String name = "android";

	public static String getMarketUrl()
	{
		if (isKindle())
		{
			return "http://www.amazon.com/gp/mas/dl/android?p=uk.co.healtht.healthtouch";
		}
		return "market://details?id=uk.co.healtht.healthtouch";
	}

	public static String getStore()
	{
		if (isKindle())
		{
			return "amazon";
		}
		return "google";
	}

	public static boolean hasNetworkConnection(Context context)
	{
		if (context == null)
		{
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public static boolean isDataRoamingEnabled(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null && activeNetwork.isRoaming();
	}

	private static boolean isKindle()
	{
		return TextUtils.equals(Build.MANUFACTURER, "Amazon");
	}
}
