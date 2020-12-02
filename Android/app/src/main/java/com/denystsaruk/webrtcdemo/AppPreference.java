/**
 *
 * Blast Mobile App
 * The Appineers
 *
 * Created by Vitaly Team
 * Copyright © 2019 The Appineers. All rights reserved.
 */

package com.denystsaruk.webrtcdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private static SharedPreferences instance = null;

    private static String APP_SHARED_PREFS;
    private static SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    public static class KEY {
        public static final String FCM_TOKEN = "FCM_TOKEN";
    }

    public static void initialize(SharedPreferences pref) {
        instance = pref;
    }

    // boolean
    public static boolean getBool(String key, boolean def) {
        return instance.getBoolean(key, def);
    }
    public static void setBool(String key, boolean value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // int
    public static int getInt(String key, int def) {
        return instance.getInt(key, def);
    }
    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // long
    public static long getLong(String key, long def) {
        return instance.getLong(key, def);
    }
    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    // string
    public static String getStr(String key, String def) {
        return instance.getString(key, def);
    }
    public static void setStr(String key, String value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // remove
    public static void removeKey(String key) {
        SharedPreferences.Editor editor = instance.edit();
        editor.remove(key);
        editor.commit();
    }

    public AppPreference(Context context) {
        APP_SHARED_PREFS = context.getApplicationContext().getPackageName();
        mPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Activity.MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();
    }
}


