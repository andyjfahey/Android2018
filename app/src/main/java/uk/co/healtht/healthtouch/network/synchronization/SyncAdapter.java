package uk.co.healtht.healthtouch.network.synchronization;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.api.ApiProvider;
import uk.co.healtht.healthtouch.api.ApiProviderListener;
import uk.co.healtht.healthtouch.api.ApiProviderListenerImpl;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.network.LocalNotificationService;
import uk.co.healtht.healthtouch.platform.Platform;
import uk.co.healtht.healthtouch.proto.Monitor;
import uk.co.healtht.healthtouch.proto.Notification;
import uk.co.healtht.healthtouch.proto.NotificationReply;
import uk.co.healtht.healthtouch.proto.Tracker;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;
import uk.co.healtht.healthtouch.ui.fragment.SettingsRemindersFragment;

import static uk.co.healtht.healthtouch.api.EndPointMethod.GET;

/**
 * Created by Julius Skripkauskas.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    protected static final long REQUEST_DELAY = 3000;
    protected static boolean syncLocked = false;
    protected ApiProvider apiProvider;
    protected SyncDb syncDb;
    // 100 retries overall allowed for this synchronization
    protected int retries = 50;

    SyncListener trackerListProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {

            // TODO: haythem try to post again all failed posts if ok then start all gets

            provider.removeListener(this);
                syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
//                List<Tracker> trackerList = ((TrackerReply) providerData).data;
                List<Tracker> trackerList = new ArrayList<>();

                Context context = HTApplication.getInstance();
                SharedPreferences preferences = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
                //clear all alarm so when we setup new alarms from data we got it wouldn't duplicate
                //also it clears alarms that were removed by some other device or from server
                clearAllAlarms(context, new ArrayList<>(preferences.getStringSet("hash", new HashSet<String>())));

                //iterate through data we got and create alarms for each tracker
                for (Tracker tracker : trackerList) {
                    List<Monitor> monitors = tracker.monitors;

                    for (Monitor monitor : monitors) {
                        SettingsRemindersFragment.createReminderNotification(monitor.cron, tracker, context);
                    }

                    int pendingRequests = apiProvider.getApiCache().getPendingCount();
                    if (pendingRequests == 0) {
                        syncGetData(apiProvider.getTrackerEntriesCacheless(tracker.trackerType), syncProvider);
                    }
                }
                apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);

                if (HTApplication.getInstance().isRequiredToSync()) {
                    syncGetData(apiProvider.getNotificationsCacheless(), notificationsSyncProvider);
                } else {
                    reloadFragments();
                    syncLocked = false;
                }

        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) {
                provider.get();
            } else {
                syncGetData(apiProvider.getNotificationsCacheless(), notificationsSyncProvider);
            }
            retries--;
        }
    };

    SyncListener notificationsSyncProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {
            provider.removeListener(this);
            syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
            List<Notification> notifications = ((NotificationReply) providerData).data;

//            for (Notification notification : notifications) {
//                DeepLink deepLink = DeepLink.fromUrl(notification.link);
//
//                if (deepLink.getType() == DeepLink.Type.GFORM) {
//                    syncGetData(apiProvider.getFormCacheless(deepLink.getUrl().replace("healthtouch:/", "")), syncProvider);
//                }
//            }

            apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);
            reloadFragments();
            SystemClock.sleep(REQUEST_DELAY);
            syncLocked = false;
            HTApplication.getInstance().disableSyncDialog();
        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) {
                provider.get();
            } else {
                SystemClock.sleep(REQUEST_DELAY);
                syncLocked = false;
                HTApplication.getInstance().disableSyncDialog();
            }
            retries--;
        }
    };

    SyncListener syncProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {
            provider.removeListener(this);
            syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
            apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);
        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) provider.get();
            retries--;
        }
    };

    SyncListener serviceSyncProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {
            provider.removeListener(this);
            syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
            apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);
            syncGetData(apiProvider.getTrackersCacheless(), trackerListProvider);
            syncGetData(apiProvider.getNotificationsCacheless(), notificationsSyncProvider);
        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) {
                provider.get();
            } else {
                syncGetData(apiProvider.getTrackersCacheless(), trackerListProvider);
                syncGetData(apiProvider.getNotificationsCacheless(), notificationsSyncProvider);
            }
            retries--;
        }
    };

    SyncListener networkSyncProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {
            provider.removeListener(this);
            syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
            apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);
            syncGetData(apiProvider.getCareNetworkMyListCacheless(), serviceSyncProvider);
        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) {
                provider.get();
            } else {
                syncGetData(apiProvider.getCareNetworkMyListCacheless(), serviceSyncProvider);
            }
            retries--;
        }
    };

    SyncListener userSyncProvider = new SyncListener() {
        @Override
        public void dataLoaded(EndPointProvider provider, Object providerData) {
            provider.removeListener(this);
            syncDb.saveProvider(provider.getEndPoint().getUri(), provider);
            apiProvider.getProviderCache().put(provider.getEndPoint().getUri(), provider);
//            HTApplication.getInstance().getMainActivityInstance().getTopFragment().reload();
            syncGetData(apiProvider.getCareNetworkServicesCacheless(), networkSyncProvider);
        }

        @Override
        public void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            if (retries > 0) {
                provider.get();
            } else {
                syncGetData(apiProvider.getCareNetworkServicesCacheless(), networkSyncProvider);
            }
            retries--;
        }
    };

    ApiProviderListenerImpl dispatchPendingListener = new ApiProviderListenerImpl() {

        @Override
        public void onDataLoaded(EndPointProvider provider, Object providerData) {
            apiProvider.dispatchPendingRequests(dispatchPendingListener);

            if (apiProvider.getApiCache().getPendingCount() <= 0) {
                syncGetData(apiProvider.getUserCacheless(), userSyncProvider);
            }
        }

        @Override
        public void onDataLoadedError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode) {
            super.onDataLoadedError(provider, errorObj, debugMsg, errorCode);

            Log.i("pendingRequests =", "errorCode =" + errorCode);
            if (errorCode != 0) {
                // The server reject this call (the error in not because we are offline).
                if (provider.getEndPoint().getMethod() != GET) {
                    // TODO: Need better error handling?
                    // TODO: Translation
                    String msg = "Error synchronizing data with server. Some data may have been lost.";
                    Toast.makeText(HTApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
                }
                apiProvider.dispatchPendingRequests(dispatchPendingListener);


                int pendingRequests = apiProvider.getApiCache().getPendingCount();
                Log.i("pendingRequests =", "pendingRequests =" + pendingRequests);
                if (pendingRequests <= 0) {
                    syncGetData(apiProvider.getUserCacheless(), userSyncProvider);
                }
            }
        }
    };

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init();
    }

    @SuppressWarnings("unused")
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init();
    }

    private void init() {
        apiProvider = HTApplication.getInstance().getApiProvider();
        syncDb = HTApplication.getInstance().getSyncDb();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        if (apiProvider.isLoggedIn() && !Platform.isDataRoamingEnabled(getContext()) && !syncLocked) {
            //send all pending requests
            syncLocked = true;
            apiProvider.dispatchPendingRequests(dispatchPendingListener);

            if (apiProvider.getApiCache().getPendingCount() <= 0) {
                syncGetData(apiProvider.getUserCacheless(), userSyncProvider);
            }
        }
    }

    //notifications on login only, and then on message screen
    //trackers
    //tracking/type.id
    //networks
    //services/all

    private void syncGetData(EndPointProvider provider, ApiProviderListener listener) {
        if (provider != null) {
            provider.addListener(listener);
            provider.get();
        }
    }

    protected void reloadFragments() {
        MainActivity activity = HTApplication.getInstance().getMainActivityInstance();

        if (activity == null) return;
        BaseFragment baseFragment = activity.getTopFragment();

        if (baseFragment == null) return;
        baseFragment.reload();
    }

    private void clearAllAlarms(Context context, ArrayList<String> removedHash) {
        //remove all old alarms
        for (String hash : removedHash) {
            //removes alarm hash within preferences so if we clear again we won't clear alarms that do not exist
            SettingsRemindersFragment.removeReminderInPreferences(context, hash);
            //remove existing alarm with action and request code that is hash.hashCode()
            //Warning: it won't remove soon to fire alarms in cache
            LocalNotificationService.removeAlarm(context, hash.hashCode());
        }
    }
}
