package uk.co.healtht.healthtouch.debug;

import android.util.Log;
import android.widget.Toast;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.settings.SettingsDebug;

public class Debug {

    public static void showErrorToast(final String errorMsg) {
        if (BuildConfig.DEBUG) {
            HTApplication.getInstance().getUiHandler().post(new Runnable() {

                @Override
                public void run() {
                    String msg = "ERROR: " + errorMsg;
                    Log.d("ERROR", msg);

                    Log.e(getClass().getSimpleName(), "ERROR: " + msg);

                    if (SettingsDebug.isShowErrorToasts()) {
                        Toast.makeText(HTApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
