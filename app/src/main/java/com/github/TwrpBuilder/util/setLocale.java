package com.github.TwrpBuilder.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by androidlover5842 on 23.2.2018.
 */

public class setLocale {
    public setLocale(Context context, String lang) {
        java.util.Locale myLocale = new java.util.Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
