package uk.co.healtht.healthtouch.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import uk.co.healtht.healthtouch.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

    protected Context ctx;
    private String fileName;
    private JSONObject jsonData;
    private boolean isNew;

    public Settings(Context ctx, String fileName) {
        this.ctx = ctx;
        this.fileName = fileName;

        load();
    }

    public void cleanAll() {
        jsonData = new JSONObject();
        save();
    }

    protected String getFieldAsString(String key, String defValue) {
        try {
            String value = jsonData.getString(key);
            return (TextUtils.isEmpty(value)) ? defValue : value;
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    boolean getFieldAsBoolean(String key, boolean defValue) {
        try {
            return jsonData.getBoolean(key);
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    int getFieldAsInt(String key, int defValue) {
        try {
            return jsonData.getInt(key);
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    JSONObject getFieldAsObject(String key, JSONObject defValue) {
        try {
            return jsonData.getJSONObject(key);
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    void putField(String key, JSONObject value) {
        try {
            jsonData.put(key, value);
        }
        catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    void putField(String key, String value) {
        try {
            if (!TextUtils.equals(value, getFieldAsString(key, null)) || !jsonData.has(key)) {
                jsonData.put(key, value);
                save();
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    long getFieldAsLong(String key, long defValue) {
        try {
            return jsonData.getLong(key);
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    boolean putField(String key, long value) {
        try {
            if (value != getFieldAsLong(key, 0) || !jsonData.has(key)) {
                jsonData.put(key, value);
                save();
                return true;
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return false;
    }

    boolean putField(String key, boolean value) {
        try {
            if (value != getFieldAsBoolean(key, false) || !jsonData.has(key)) {
                jsonData.put(key, value);
                save();
                return true;
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return false;
    }

    boolean putField(String key, int value) {
        try {
            if (value != getFieldAsInt(key, 0) || !jsonData.has(key)) {
                jsonData.put(key, value);
                save();
                return true;
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return false;
    }

    boolean putField(String key, double value) {
        try {
            if (value != getFieldAsDouble(key, 0) || !jsonData.has(key)) {
                jsonData.put(key, value);
                save();
                return true;
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return false;
    }

    double getFieldAsDouble(String key, double defValue) {
        try {
            return jsonData.getDouble(key);
        }
        catch (JSONException e) {
            return defValue;
        }
    }

    private void save() {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(fileName, 0).edit();
        editor.putString("jsonData", jsonData.toString());
        editor.apply();
    }

    private void load() {
        try {
            SharedPreferences settings = ctx.getSharedPreferences(fileName, 0);
            String strJson = settings.getString("jsonData", null);
            jsonData = new JSONObject(strJson);
        }
        catch (Throwable e) {
            // If there was a problem loading, we default to an empty Object
            jsonData = new JSONObject();
            isNew = true;
        }
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean wasNew() {
        boolean wasNew = isNew;
        isNew = false;
        return wasNew;
    }
}
