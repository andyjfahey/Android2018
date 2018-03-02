package uk.co.healtht.healthtouch.settings;

import android.content.Context;

public class SettingsLastUser extends SettingsUser {

    private static final String USER_SETTINGS_PREFS = "PrefsFileLastUser";

    public SettingsLastUser(Context ctx) {
        super(ctx, USER_SETTINGS_PREFS);
    }

}