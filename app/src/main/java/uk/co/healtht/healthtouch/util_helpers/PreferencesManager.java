package uk.co.healtht.healthtouch.util_helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import uk.co.healtht.healthtouch.model.db.DBConstant;

/**
 * Created by Najeeb.Idrees on 03/07/2017.
 */

public class PreferencesManager
{

	public static final String PREFERENCES_FILE = "HealthTpuchPreferenceFile";

	public static final String USER_EMAIL_ID = "user_email_id";
	public static final String IS_SYNC_COMPLETED = "is_sync_completed";
	public static final String APP_VERSION = "app_version";
	public static final String DEVICE_TOKEN = "device_token";

	//	public static final String SHOW_CONSENT_SECOND_TIME = "show_consent_second_time";

	//TODO... need to put this in Db table account info
	//	public static final String LAST_ATTEMPTED_SYNC = "last_attempted_sync";
	//	public static final String LAST_SUCCEED_SYNC = "last_succeed_sync";
	//	public static final String SPLASH_URL = "splash_url";

	private static PreferencesManager sInstance;
	private final SharedPreferences mPref;

	private PreferencesManager(@NonNull Context context, String fileName)
	{
		mPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}

	public static synchronized void initializeInstance(@NonNull Context context, String fileName)
	{
		if (sInstance == null)
		{
			sInstance = new PreferencesManager(context, fileName);
		}
	}

	public static synchronized PreferencesManager getInstance()
	{
		if (sInstance == null)
		{
			throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
					" is not initialized, call initializeInstance(..) method first.");
		}
		return sInstance;
	}

	public void setStringValue(String key, String value)
	{
		mPref.edit()
				.putString(key, value)
				.apply();
	}

	public void setIntValue(String key, int value)
	{
		mPref.edit()
				.putInt(key, value)
				.apply();
	}

	public void setBooleanValue(String key, boolean value)
	{
		mPref.edit()
				.putBoolean(key, value)
				.apply();
	}

	public void setObject(String key, Object value)
	{
		Gson gson = new Gson();
		String json = gson.toJson(value);
		mPref.edit().putString(key, json).apply();
	}

	@Nullable
	public String getStringValue(String key)
	{
		return mPref.getString(key, "");
	}

	@Nullable
	public String getStringValueDefaultDBName(String key)
	{
		return mPref.getString(key, DBConstant.FILE_NAME_DB);
	}

	public int getIntValue(String key)
	{
		return mPref.getInt(key, 0);
	}

	public boolean getBooleanValue(String key)
	{
		return mPref.getBoolean(key, false);
	}

	public <T> Object getObject(String key, @NonNull Class<T> objectClass)
	{
		Gson gson = new Gson();
		String json = mPref.getString(key, "");
		return gson.fromJson(json, objectClass);
	}

	public void remove(String key)
	{
		mPref.edit()
				.remove(key)
				.apply();
	}

	public boolean clear()
	{
		return mPref.edit()
				.clear()
				.commit();
	}
}