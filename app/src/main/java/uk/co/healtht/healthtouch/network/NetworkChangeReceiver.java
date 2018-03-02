package uk.co.healtht.healthtouch.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.platform.Platform;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = Platform.hasNetworkConnection(context);

        if (isConnected) {
            Log.d("taguu", "onReceive isConnected: " + isConnected);
//            HTApplication.getInstance().getApiProvider().dispatchPendingRequests();
            HTApplication.getInstance().getApiProvider().checkSyncEnabled();
        }
    }
}