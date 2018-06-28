package com.github.TwrpBuilder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by androidlover5842 on 8.2.2018.
 */

public class SharedP {

    public static void putRecoveryString(Context context, String value, boolean bool) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("recoveryPath", value);
        editor.putBoolean("isSupport", bool);
        editor.apply();
    }
}
