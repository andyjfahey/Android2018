package uk.co.healtht.healthtouch.settings;

import android.content.Context;

import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.proto.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SettingsUser extends Settings {

    private static final String USER_SETTINGS_PREFS = "PrefsFileUser";

    public SettingsUser(Context ctx) {
        super(ctx, USER_SETTINGS_PREFS);
    }

    public SettingsUser(Context ctx, String filename) {
        super(ctx, filename);
    }

    public String getSession() {
        return getFieldAsString("session", null);
    }

    public void setSession(String value) {
        putField("session", value);
    }

    public void setUser(User user) {
//        setEmail(user.email);
//        setUserName(user.name, user.surname);
//        putField("userUri", user.uri);
//        putField("gender", user.gender);
//        // TODO: public String username; -> Need to set the login on AppSettings
//
//        if (user.dateOfBirth != null) {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            try {
//                setDateOfBirth(df.parse(user.dateOfBirth).getTime());
//            } catch (ParseException e) {
//                Crash.logException(e);
//            }
//            // age - Not used
//            // type - Not used
//            //  active - not used
//        }
//
//        putField("address", user.address);
//        putField("city", user.city);
//        putField("nhs", user.nhs);
//        putField("pos]tcode", user.postcode);
    }

    public String getUserUri() {
        return getFieldAsString("userUri", "").trim();
    }

    public String getFirstName() {
        return getFieldAsString("firstName", "").trim();
    }

    public String getLastName() {
        return getFieldAsString("lastName", "").trim();
    }

    public void setUserName(String firstName, String lastName) {
        putField("firstName", firstName);
        putField("lastName", lastName);
    }

    public void setDateOfBirth(long dob) {
        putField("dob", dob);
    }

    public long getDateOfBirth() {
        return getFieldAsLong("dob", System.currentTimeMillis());
    }

    public String getEmail() {
        return getFieldAsString("email", null);
    }

    public void setEmail(String value) {
        putField("email", value);
    }

    public String getGender() {
        return getFieldAsString("gender", null);
    }

    public String getAddress() {
        return getFieldAsString("address", null);
    }

    public String getCity() {
        return getFieldAsString("city", null);
    }

    public String getNhsNumber() {
        return getFieldAsString("nhs", null);
    }

    public String getPostcode() {
        return getFieldAsString("postcode", null);
    }

    public void copyFromUserSettings(SettingsUser settingsUser) {
        setSession(settingsUser.getSession());
        setDateOfBirth(settingsUser.getDateOfBirth());

        User user = new User();
        user.email = settingsUser.getEmail();
        user.name = settingsUser.getFirstName();
        user.surname = settingsUser.getLastName();
        user.uri = settingsUser.getUserUri();
        user.gender = settingsUser.getGender();
        user.address = settingsUser.getAddress();
        user.city = settingsUser.getCity();
        user.nhs = settingsUser.getNhsNumber();
        user.postcode = settingsUser.getPostcode();
        setUser(user);
    }
}
