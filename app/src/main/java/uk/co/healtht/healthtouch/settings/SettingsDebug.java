package uk.co.healtht.healthtouch.settings;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.WindowManager;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.debug.DebugActivity;
import uk.co.healtht.healthtouch.ui.widget.FpsView;

public class SettingsDebug extends Settings {

    public final static String TAG = "DEBUG";

    private static final String DEBUG_PREFS = "DebugPrefsFile";
    private static final String PREF_SHOW_FPS = "ShowFps";
    private static final String PREF_SHOW_DEBUG_NOTIF = "ShowDebugNotif";
    private static final String PREF_DEBUG_SERVER = "DebugServer";
    private static final String PREF_FAKE_PAYMENT = "FakePayment";
    private static final String PREF_FAKE_BOOKINGS = "FakeBookings";
    private static final String PREF_FULL_NETWORK_LOG = "FullNetworkLog";
    private static final String PREF_SHOW_ERROR_TOASTS = "ShowErrorToasts";

    private static SettingsDebug instance;

    private Application appContext;
    private FpsView fpsView;

    private boolean showFps;
    private boolean isInProxyTestMode;

    public static void init(Application appContext) {
        instance = new SettingsDebug(appContext);

        // instance.showFpsUi(isShowFpsEnabled());
        instance.showDebugNotification(isShowDebugNotificationEnabled());
    }

    public SettingsDebug(Application appContext) {
        super(appContext, DEBUG_PREFS);

        this.appContext = appContext;
        this.showFps = getFieldAsBoolean(PREF_SHOW_FPS, BuildConfig.DEBUG);
    }

    public static boolean isShowFpsEnabled() {
        return instance.showFps;
    }

    public static void setShowFps(boolean doShow, boolean saveState) {
        instance.showFpsUi(doShow);
        if (saveState) {
            instance.putField(PREF_SHOW_FPS, doShow);
        }
    }

    public static boolean isShowDebugNotificationEnabled() {
        return instance.getFieldAsBoolean(PREF_SHOW_DEBUG_NOTIF, BuildConfig.DEBUG);
    }

    public static void setShowDebugNotification(boolean doShow) {
        instance.putField(PREF_SHOW_DEBUG_NOTIF, doShow);
    }

    public static String getDebugServer() {
        return instance.getFieldAsString(PREF_DEBUG_SERVER, "");
    }

    public static void setDebugServer(String debugServer) {
        instance.putField(PREF_DEBUG_SERVER, debugServer);
    }

    public static boolean isFullNetworkLog() {
        return instance.getFieldAsBoolean(PREF_FULL_NETWORK_LOG, false);
    }

    public static void setFullNetworkLog(boolean isFullNetworkLog) {
        instance.putField(PREF_FULL_NETWORK_LOG, isFullNetworkLog);
    }

    public static boolean isShowErrorToasts() {
        return instance.getFieldAsBoolean(PREF_SHOW_ERROR_TOASTS, true);
    }

    public static void setShowErrorToasts(boolean show) {
        instance.putField(PREF_SHOW_ERROR_TOASTS, show);
    }

    // ------------------------- Helper methods -----------------
    private void showFpsUi(boolean doShow) {
        showFps = doShow;
        WindowManager mWM = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        if (doShow) {
            if (fpsView == null) {
                fpsView = new FpsView(appContext);
                fpsView.setBackgroundColor(0xff000000);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
                params.gravity = Gravity.TOP | Gravity.LEFT;
                mWM.addView(fpsView, params);
            }
        }
        else if (fpsView != null) {
            mWM.removeView(fpsView);
            fpsView = null;
        }
    }

    private void showDebugNotification(boolean doShow) {
        NotificationManager notifManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        final int DEBUG_ID = 9028374;

        if (doShow) {
            Intent notifyIntent = new Intent(appContext, DebugActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent intent = PendingIntent.getActivity(appContext, 0, notifyIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext)//
                    .setContentIntent(intent) //
                    .setWhen(System.currentTimeMillis())//
                    .setAutoCancel(false)//
                    .setOngoing(true) //
                    .setSmallIcon(R.mipmap.ic_launcher) //
                    .setContentTitle(appContext.getString(R.string.app_name))//
                    .setContentText("Debug Screen")//
                    .setPriority(NotificationCompat.PRIORITY_MIN);

            notifManager.notify(DEBUG_ID, builder.build());
        }
        else {
            notifManager.cancel(DEBUG_ID);
        }
    }
}
