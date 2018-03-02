package uk.co.healtht.healthtouch.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.UUID;

public class SettingsApplication extends Settings {

    private static final String APP_SETTINGS_PREFS = "PrefsFileApp";

    public SettingsApplication(Context ctx) {
        super(ctx, APP_SETTINGS_PREFS);
    }

    public String getDeviceId() {
        String deviceId = getFieldAsString("deviceId", null);

        // If not found: Either this is a clean install, or the user clean the app cache
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            setDeviceId(deviceId);
        }

        return deviceId;
    }

    private void setDeviceId(String value) {
        putField("deviceId", value);
    }

    public String getUserLogin() {
        String res = getFieldAsString("userLogin", "");
        if (TextUtils.isEmpty(res)) {
            // See http://stackoverflow.com/questions/2112965/how-to-get-the-android-devices-primary-e-mail-address
            Account[] accounts = AccountManager.get(ctx).getAccounts();
            for (Account account : accounts) {
                if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                    res = account.name;
                    break;
                }
            }
        }

        return res;
    }

    public void setUserLogin(String value) {
        putField("userLogin", value);
    }

    public String getUserPassword() {
        String res = getFieldAsString("userPassword", "");
        return res;
    }

    public void setUserPassword(String value) {
        putField("userPassword", value);
    }

    public String getPrivacyLink() {
        return getFieldAsString("privacyLink", "http://www.healthtouchmobile.com/privacy-policy");
    }

    public void setPrivacyLink(String url) {
        putField("privacyLink", url);
    }

    public String getSupportEmail() {
        return getFieldAsString("supportEmail", "mailto:info@healthtouchmobile.com");
    }

    public void setSupportEmail(String email) {
        putField("supportEmail", email);
    }

    public String getLastPushUri() {
        return getFieldAsString("lastPushUri", "");
    }

    public void setLastPushUri(String uri) {
        putField("lastPushUri", uri);
    }
}
