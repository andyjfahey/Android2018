package uk.co.healtht.healthtouch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.api.ApiProvider;
import uk.co.healtht.healthtouch.api.UiProvider;
import uk.co.healtht.healthtouch.model.entities.HTTrackerMedication;
import uk.co.healtht.healthtouch.model.entities.HTTrackerSync;
import uk.co.healtht.healthtouch.network.pushnotification.GcmData;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.proto.Notification;
import uk.co.healtht.healthtouch.proto.UserReply;
import uk.co.healtht.healthtouch.settings.SettingsUser;
import uk.co.healtht.healthtouch.ui.FragmentLauncher;
import uk.co.healtht.healthtouch.ui.FragmentLauncherImpl;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;
import uk.co.healtht.healthtouch.ui.fragment.HomeFragmentNew;
import uk.co.healtht.healthtouch.ui.fragment.LoginFragment;
import uk.co.healtht.healthtouch.util_helpers.AppConstant;
import uk.co.healtht.healthtouch.util_helpers.AppUtil;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;
import uk.co.healtht.healthtouch.utils.KeyboardUtil;

import static android.view.Gravity.LEFT;

public class MainActivity extends AppCompatActivity implements
		FragmentManager.OnBackStackChangedListener
{

	// This Activity must be single instance, otherwise it will instantiate the App UI more than one.
	// Making the Activity Single instance on the Manifest does't work, as the App may be launched
	// by Thirds party apps (eg facebook deep linking). This is the simpler solution that covers all test cases.
	private static MainActivity singleInstance;
	//	private DrawerLayout drawerLayout;
	//	private MenuFragment menuFragment;
	private FragmentLauncherImpl fragmentLauncher;
	protected ApiProvider apiProvider;

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// HACK: Saving the state of the Activity is causing lots of problems:
		// 1 - When the activity is killed, the state of fragments is saved + the back stack. When the activity is re-created, all data is restored,
		// but the fragment views are added in a random order (added by active fragment order, instead of the stack order).
		// 2 - When a Fragment have sub-fragments (ie Event list), all the subfragments are re-created, all the old listeners are lost
		// super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init();
	}

	private void checkSynchroWhenNewDate()
	{
		apiProvider = HTApplication.getInstance().getApiProvider();
		if (apiProvider != null && apiProvider.getLastUser().getEmail() != null)
		{
			apiProvider.startSynchroIfNewDate(apiProvider.getLastUser().getEmail());
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState)
	{
		super.onPostCreate(savedInstanceState, persistentState);
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void launchFirstFragment()
	{
		// This is our "launching" fragment

		boolean isLoggedIn = false;

		//By doing so we want user to logged out to use refactored app
		if (HTApplication.preferencesManager.getIntValue(PreferencesManager.APP_VERSION) == 1024) //hack for prevent app to crash on version 1.2.1
		{
			final ApiProvider apiProvider = HTApplication.getInstance().getApiProvider();
			if (apiProvider.getSettingsUser() != null && !StringUtil.isEmpty(apiProvider.getSettingsUser().getEmail()))
			{
				HTApplication.preferencesManager.setStringValue(PreferencesManager.USER_EMAIL_ID, apiProvider.getSettingsUser().getEmail());
			}
			HTApplication.preferencesManager.setIntValue(PreferencesManager.APP_VERSION, BuildConfig.VERSION_CODE + 1);
		}
		else
		{
			isLoggedIn = HTApplication.getInstance().getApiProvider().isLoggedIn();
		}

		//temp comment, futher should be uncommented
		//drawerLayout.setDrawerLockMode(isLoggedIn ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		if (fragmentLauncher.getBackStackEntryCount() > 0)
		{
			fragmentLauncher.popBackStackToRoot(true);
		}
		Bundle data = new Bundle();
		data.putBoolean("showMessages", (getIntent() != null && (getIntent().hasExtra("gcm") || getIntent().hasExtra("tracker") || getIntent().hasExtra("medication"))));
		fragmentLauncher.startFragment(isLoggedIn ? HomeFragmentNew.class : LoginFragment.class, data, null, 0, true);
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		// getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DITHER, WindowManager.LayoutParams.FLAG_DITHER);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		handleIntent(intent);
	}

	protected void init()
	{
		referenceSelf();
		getLogoutPreferences();
		setContentView(R.layout.activity_main);


		fragmentLauncher = new FragmentLauncherImpl(this);
		initViews();

		toggleTestMode();
		ActivityManager actMan = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		Crash.log("MainActivity:onCreate - memoryClass=" + actMan.getMemoryClass());

		//		fragmentLauncher.setDrawerLayout(drawerLayout);
		initUiProvider();
		launchFirstFragment();
		checkDebugState();
		handleIntent(getIntent());
		handleNotificationIntent(getIntent());
	}

	protected void getLogoutPreferences()
	{
		final ApiProvider apiProvider = HTApplication.getInstance().getApiProvider();
		final SettingsUser settingsUser = apiProvider.getSettingsUser();
		getThreadedLogoutPreferences(settingsUser, apiProvider);
	}

	protected void referenceSelf()
	{
		if (singleInstance != null)
		{
			singleInstance.finish();
		}
		singleInstance = this;
	}

	protected void toggleTestMode()
	{
		if ("test".equals(getIntent().getAction()))
		{
			// TODO SettingsDebug.setShowFps(false, false);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			fragmentLauncher.inTestMode = true;
		}
	}

	protected void initViews()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//		drawerLayout = (DrawerLayout) findViewById(R.id.sliding_pane_layout);
		//		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		//		menuFragment = new MenuFragment();
		//		getSupportFragmentManager().beginTransaction().add(R.id.fragment_menu_container, menuFragment).commit();
	}

	protected void initUiProvider()
	{
		UiProvider.UiProviderListener uiListener = new UiProvider.UiProviderListener()
		{
			@Override
			public void onUiChange(UiProvider.UiEvent event, Object providerData)
			{
				if (providerData != null)
				{
					UserReply msg = (UserReply) providerData;

					if (msg.message instanceof String)
					{
						fragmentLauncher.showNotification(null, (String) msg.message, null);
					}
				}

				launchFirstFragment();
			}
		};
		UiProvider uiProvider = HTApplication.getInstance().getApiProvider().getUiProvider();
		uiProvider.addListener(UiProvider.UiEvent.LOGIN, uiListener);
		uiProvider.addListener(UiProvider.UiEvent.LOGOUT, uiListener);
	}

	protected void checkDebugState()
	{
		if (BuildConfig.DEBUG)
		{
			checkVersion();
			checkCrashlytics();
		}
	}

	protected void checkVersion()
	{
		try
		{
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			if (!Platform.version.equals(pinfo.versionName))
			{
				fragmentLauncher.showNotification("Version error", "Platform.version = " + Platform.version + ", Manifest = " + pinfo.versionName, null);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	protected void checkCrashlytics()
	{
		try
		{
			getResources().getAssets().open("crashlytics-build.properties").close();
		}
		catch (Throwable e)
		{
			// If we ended up here, we don't have the file...
			e.printStackTrace();
			fragmentLauncher.showNotification("Crashlytics", "Crashlytics config not FOUND!!", null);
		}
	}

	private void handleNotificationIntent(Intent intent)
	{
		boolean isLoggedIn = HTApplication.getInstance().getApiProvider().isLoggedIn();

		if (!isLoggedIn) return;
		if (intent.getExtras() == null) return;

		if (intent.hasExtra("tracker"))
		{
			HTTrackerSync tracker = (HTTrackerSync) intent.getSerializableExtra("tracker");
			Bundle data = new Bundle();
			data.putSerializable("tracker", tracker);
			Class clz = AppUtil.getClassFromClassName(AppConstant.FRAGMENT_PACKAGE + tracker.getSimpleName() + "Fragment");
			fragmentLauncher.startFragment(clz, data, null, 0, false);
			intent.removeExtra("tracker");
		}

		if (intent.hasExtra("medication"))
		{
			HTTrackerMedication  htTrackerMedication = (HTTrackerMedication) intent.getSerializableExtra("medication");
			Bundle data = new Bundle();
			data.putSerializable("medication", htTrackerMedication);
			Class clz = AppUtil.getClassFromClassName(AppConstant.FRAGMENT_PACKAGE + "MedicationEditorFragment");
			fragmentLauncher.startFragment(clz, data, null, 0, false);
			intent.removeExtra("medication");
		}
	}

	private void getThreadedLogoutPreferences(final SettingsUser settingsUser, final ApiProvider apiProvider)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SharedPreferences sharedPreferences = HTApplication.getInstance().getSharedPreferences(
						settingsUser.getEmail() + "prefs", Context.MODE_PRIVATE);
				boolean stayLogged = sharedPreferences.getBoolean("stayLogged", true);
				if (!stayLogged)
				{
					apiProvider.logout();
				}
			}
		}).start();
	}

	private void handleIntent(Intent intent)
	{
		boolean isLoggedIn = HTApplication.getInstance().getApiProvider().isLoggedIn();

		if (intent.hasExtra("gcm") && isLoggedIn)
		{
			handleGcm(intent);
		}

		checkDeviceTime();
	}

	private void handleGcm(Intent intent)
	{
		GcmData gcmData = intent.getParcelableExtra("gcm");
		Notification notification = createGcmNotification(gcmData);

		//TODO, Handle GCM notification here
		//        if (gcmData.data.push_type.equals("update_medication") || gcmData.data.push_type.equals("form_sent")) {
		//            handleDeepGcm(notification);
		//        } else if ((gcmData.data.push_type.equals("message") || gcmData.data.push_type.equals("update_thresholds")
		//                || gcmData.data.push_type.equals("update_reminders")) && !(intent.getExtras() != null
		//                && intent.hasExtra("tracker"))) {
		//            fragmentLauncher.startFragment(MessageListFragment.class, null, null, 0, true);
		//        }
	}

	protected Notification createGcmNotification(GcmData gcmData)
	{
		Notification notification = new Notification();
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			date = format.parse(gcmData.data.created_at);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		notification.createdAt = date;
		notification.link = gcmData.data.link;
		notification.message = gcmData.data.message;
		notification.type = gcmData.data.type;
		notification.uri = gcmData.data.resourceURI;
		return notification;
	}

	//    protected void handleDeepGcm(Notification notification) {
	//        ApiProvider apiProvider = HTApplication.getInstance().getApiProvider();
	//        BaseFragment topFragment = fragmentLauncher.getTopFragment();
	//
	//        if (topFragment instanceof MessageListFragment){
	//            ((MessageListFragment) topFragment).setMessageSeen(notification, apiProvider, apiProvider.getNotifications());
	//        }
	//
	//        DeepLink link = DeepLink.fromUrl(notification.link);
	//
	//        if (topFragment != null) {
	//            DeepLink.openLink(topFragment, link, null);
	//        } else {
	//            fragmentLauncher.pendingDeepLink = link;
	//        }
	//
	//    }

	private void checkDeviceTime()
	{
		long buildTime = Platform.buildDate;
		final long DATE_OFFSET = 25L * 60L * 60L * 1000L; // 25 Hours
		if (buildTime - DATE_OFFSET > System.currentTimeMillis())
		{
			fragmentLauncher.showNotification(null, getString(R.string.error_wrong_device_date), null);

			String msg = "WRONG TIME: " + new Date(System.currentTimeMillis()) + " Build time = " + new Date(buildTime);
			HTApplication.getInstance().getAnalytics().track("Error", "Message", msg);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		Crash.log("MainActivity:onStart");

		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Crash.log("MainActivity:onStop");

		getSupportFragmentManager().removeOnBackStackChangedListener(this);
		HTApplication.getInstance().getAnalytics().flush();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Crash.log("MainActivity:onResume");
		HTApplication.getInstance().setMainActivityInstance(this);

		updateTitleAndMenu();
		fragmentLauncher.updateOptionsMenu();

		checkSynchroWhenNewDate();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		HTApplication.getInstance().setMainActivityInstance(null);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		fragmentLauncher.isDestroyed = true;

		Crash.log("MainActivity:onDestroy");

		if (singleInstance == this)
		{
			singleInstance = null;
		}

		HTApplication.getInstance().getAnalytics().flush();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		long now = SystemClock.uptimeMillis();
		if (now < fragmentLauncher.touchAllowedAfterTime)
		{
			// Note: This code only works because setMotionEventSplittingEnabled() was called on all ViewGroups.
			// See BaseFragment.onViewCreated().
			return true;
		}

		boolean res = true;
		try
		{
			// Bug on DrawerLayout - Sometime throws NPE
			res = super.dispatchTouchEvent(ev);
		}
		catch (Throwable e)
		{
			Crash.logException(e);
		}

		return res;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSupportNavigateUp()
	{
		onBackPressed();
		return true;
	}

	//	@Override
	//	public boolean onMenuOpened(int featureId, Menu menu)
	//	{
	//		if (drawerLayout != null)
	//		{
	//			drawerLayout.closeDrawer(LEFT);
	//		}
	//
	//		return super.onMenuOpened(featureId, menu);
	//	}

	//	@Override
	//	public boolean onKeyUp(int keycode, KeyEvent event)
	//	{
	//		if (keycode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0)
	//		{
	//			if (drawerLayout.isDrawerVisible(LEFT))
	//			{
	//				drawerLayout.closeDrawer(LEFT);
	//			}
	//			else
	//			{
	//				drawerLayout.openDrawer(LEFT);
	//			}
	//		}
	//
	//		return super.onKeyUp(keycode, event);
	//	}

	@Override
	public void onBackPressed()
	{
		// Give a chance to the current fragment to handle the back key
		//		if (drawerLayout.isDrawerVisible(LEFT))
		//		{
		//			drawerLayout.closeDrawer(LEFT);
		//		}
		if (!fragmentLauncher.getTopFragment().onBackPressed())
		{

			// If we have only one Fragment, we close the activity
			if (fragmentLauncher.getBackStackEntryCount() <= 1)
			{
				finish();
			}
			else
			{
				BaseFragment topFrag = fragmentLauncher.getTopFragment();
				topFrag.finish(BaseFragment.RESULT_CANCEL);

				BaseFragment newTopFrag = fragmentLauncher.getTopFragment();
				Crash.log("Back: " + newTopFrag.getClass().getName() + " -> " + newTopFrag.getArguments());
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// HACK: Facebook native dialogs don't work properly. Assume that any Result is always
		// intended to the most top Fragment, we don't call super
		// If the activity is killed, while launching FB or other App, we may receive a activity result, but have no fragment
		BaseFragment topFrag = fragmentLauncher.getTopFragment();
		if (topFrag != null)
		{
			int code = requestCode & 0xFFFF;

			// Note: The menuFragment doesn't belong to the fragment stack, but may get a result
			//			if (drawerLayout.isDrawerVisible(LEFT))
			//			{
			//				menuFragment.onActivityResult(code, resultCode, data);
			//			}

			topFrag.onActivityResult(code, resultCode, data);
		}
	}

	private void updateTitleAndMenu()
	{
		BaseFragment topFrag = fragmentLauncher.getTopFragment();
		if (topFrag != null)
		{
			fragmentLauncher.setTitle(topFrag);
		}
	}

	@Override
	public void onBackStackChanged()
	{
		updateTitleAndMenu();
		fragmentLauncher.updateOptionsMenu();
		KeyboardUtil.hideKeyboard(this);

		// If we have no fragments left, Close the activity
		if (fragmentLauncher.pendingLaunchFragments == 0 && fragmentLauncher.getBackStackEntryCount() <= 0)
		{
			finish();
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		try
		{
			super.startActivityForResult(intent, requestCode);
		}
		catch (Throwable e)
		{
			// fixes Google Maps bug: http://stackoverflow.com/a/20905954/2075875
			// http://stackoverflow.com/questions/17549030/android-nullpointerexception-in-instrumentation-execstartactivity
			Crash.logException(e);
		}
	}

	//	public void openDrawer()
	//	{
	//		drawerLayout.openDrawer(LEFT);
	//	}

	public BaseFragment getTopFragment()
	{
		return fragmentLauncher.getTopFragment();
	}

	public FragmentLauncher getFragmentLauncher()
	{
		return fragmentLauncher;
	}

}

