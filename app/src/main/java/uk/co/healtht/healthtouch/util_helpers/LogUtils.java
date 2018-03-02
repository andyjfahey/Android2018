package uk.co.healtht.healthtouch.util_helpers;

import uk.co.healtht.healthtouch.BuildConfig;

/**
 * Created by Najeeb.Idrees on 5-July-2017.
 */

public class LogUtils
{

	private static final String GLOBAL_TAG = "HealthTouch==>";


	public static void d(final String tag, final String message)
	{
		if (BuildConfig.DEBUG)
		{
			android.util.Log.d(GLOBAL_TAG + tag, message);
		}
	}

	public static void i(final String tag, final String message)
	{
		if (BuildConfig.DEBUG)
		{
			android.util.Log.i(GLOBAL_TAG + tag, message);
		}
	}

	public static void w(final String tag, final String message)
	{
		if (BuildConfig.DEBUG)
		{
			android.util.Log.w(GLOBAL_TAG + tag, message);
		}
	}

	public static void e(final String tag, final String message)
	{
		if (BuildConfig.DEBUG)
		{
			android.util.Log.e(GLOBAL_TAG + tag, message);
		}
	}

}
