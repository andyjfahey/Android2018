//package uk.co.healtht.healthtouch.utils;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
///**
// * Created by Farouk Touzi.
// */
//public class SharredUtils {
//
//    public static void saveValue(Context context, String value, String text) {
//        SharedPreferences settings = context.getSharedPreferences("TOUCH", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(value, text);
//        editor.commit();
//    }
//
//    public static String getValue(Context context, String key) {
//        SharedPreferences settings;
//        String text;
//        settings = context.getSharedPreferences("TOUCH", Context.MODE_PRIVATE);
//        text = settings.getString(key, null);
//        return text;
//    }
//
//    public static void deletePref(Context context, String key) {
//        SharedPreferences.Editor edit = context.getSharedPreferences("TOUCH", Context.MODE_PRIVATE).edit();
//        edit.remove(key).commit();
//    }
//}
