package uk.co.healtht.healthtouch;

import android.app.Application;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import uk.co.healtht.healthtouch.analytics.Analytics;
import uk.co.healtht.healthtouch.api.ApiProvider;
import uk.co.healtht.healthtouch.api.UiProvider;
import uk.co.healtht.healthtouch.model.db.DBHelper;
import uk.co.healtht.healthtouch.network.synchronization.SyncDb;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.settings.SettingsApplication;
import uk.co.healtht.healthtouch.settings.SettingsDebug;
import uk.co.healtht.healthtouch.settings.SettingsLastUser;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;

public class HTApplication extends Application
{
	private static final int MUST_SYNC_DELAY = 10000;
	private static HTApplication instance;
	private boolean mustSync = false;
	private Handler uiHandler;
	private ApiProvider apiProvider;
	private Analytics analytics;
	private SyncDb syncDb;
	private boolean syncing = false;
	private WeakReference<BaseFragment> loadingFragment;
	private MainActivity mainActivityInstance;

	public static DBHelper dbHelper;
	public static PreferencesManager preferencesManager;

	public HTApplication()
	{
		instance = this;
	}

	private RequestQueue mRequestQueue;

	Runnable initialSyncDone = new Runnable()
	{
		@Override
		public void run()
		{
			mustSync = false;
		}
	};

	@Override
	public void onCreate()
	{
		super.onCreate();

		uiHandler = new Handler();
		SettingsDebug.init(this);

		SettingsUser settingsUser = new SettingsUser(this);
		SettingsApplication settingsApp = new SettingsApplication(this);
		SettingsLastUser settingsLastUser = new SettingsLastUser(this);
		UiProvider uiProvider = new UiProvider();

		preferencesManager.initializeInstance(this, PreferencesManager.PREFERENCES_FILE);
		preferencesManager = PreferencesManager.getInstance();

		if (preferencesManager.getIntValue(PreferencesManager.APP_VERSION) == 0) //hack for prevent app to crash on version 1022
		{
			preferencesManager.setIntValue(PreferencesManager.APP_VERSION, BuildConfig.VERSION_CODE);
		}

		apiProvider = new ApiProvider(this, settingsUser, settingsApp, uiProvider, settingsLastUser);
		analytics = new Analytics();
		syncDb = new SyncDb(this, settingsUser);
		syncing = true;
		sync();

		Crashlytics crashlytics = new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build();
		Fabric.with(this, crashlytics, new Crashlytics());

		dbHelper = DBHelper.getHelper(getApplicationContext());
	}


	public void demandSync()
	{
		Log.d("taguu", "demandSync");
		SettingsUser settingsUser = new SettingsUser(this);
		mustSync = true;
		apiProvider.startSyncImmediately(settingsUser.getEmail(), null);
	}

	public static HTApplication getInstance()
	{
		return instance;
	}

	public void syncWait()
	{
		mustSync = true;
	}

	public void disableSyncDialog()
	{
		mustSync = false;
		uiHandler.removeCallbacks(initialSyncDone);
	}

	public void syncWaitNot()
	{
		uiHandler.removeCallbacks(initialSyncDone);
		if (mustSync)
		{
			uiHandler.postDelayed(initialSyncDone, MUST_SYNC_DELAY);
		}
	}

	public MainActivity getMainActivityInstance()
	{
		return mainActivityInstance;
	}

	public void setMainActivityInstance(MainActivity mainActivityInstance)
	{
		this.mainActivityInstance = mainActivityInstance;
	}

	public boolean isRequiredToSync()
	{
		return Platform.hasNetworkConnection(this) && mustSync;
	}

	public void sync()
	{
		syncDb.transferSyncDataToCache(apiProvider.getProviderCache());
	}

	public Handler getUiHandler()
	{
		return uiHandler;
	}

	public ApiProvider getApiProvider()
	{
		return apiProvider;
	}

	public Analytics getAnalytics()
	{
		return analytics;
	}

	public SyncDb getSyncDb()
	{
		return syncDb;
	}

	public boolean isSyncing()
	{
		return syncing;
	}

	public void setSyncing(boolean syncing)
	{
		this.syncing = syncing;
	}

	public WeakReference<BaseFragment> getLoadingFragment()
	{
		return loadingFragment;
	}

	public void setLoadingFragment(WeakReference<BaseFragment> loadingFragment)
	{
		this.loadingFragment = loadingFragment;
	}


	public <T> void addToRequestQueue(@NonNull Request<T> req, String tag)
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? "DefaultTag" : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(@NonNull Request<T> req)
	{
		req.setTag("DefaultTag");
		getRequestQueue().add(req);
	}


	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack()
			{

				//Code to allow redirects in volly
				@Override
				protected HttpURLConnection createConnection(@NonNull URL url) throws IOException
				{
					HttpURLConnection connection = super.createConnection(url);
					connection.setInstanceFollowRedirects(true);

					return connection;
				}
			});
		}

		return mRequestQueue;
	}

	public void cancelPendingRequests(@NonNull Object tag)
	{
		if (mRequestQueue != null)
		{
			mRequestQueue.cancelAll(tag);
		}
	}

	public void cancelAllPendingRequests()
	{
		mRequestQueue.cancelAll(new RequestQueue.RequestFilter()
		{
			@Override
			public boolean apply(Request<?> request)
			{
				// do I have to cancel this?
				return true; // -> always yes
			}
		});
	}


}
