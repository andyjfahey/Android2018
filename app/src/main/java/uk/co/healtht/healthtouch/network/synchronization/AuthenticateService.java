package uk.co.healtht.healthtouch.network.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Julius Skripkauskas.
 */
public class AuthenticateService  extends Service {
    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}