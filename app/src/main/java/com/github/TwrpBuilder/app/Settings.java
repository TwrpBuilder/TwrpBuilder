package com.github.TwrpBuilder.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.setLocale;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

/**
 * Created by berkantkz on 19.09.2018.
 * # twrpbuilder
 */

public class Settings extends PreferenceActivity {
    @NonNull
    private String[] supportLangs;
    @NonNull
    private String[] langList;
    private ArrayAdapter<String> supportedLang;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.settings);

        supportLangs = getResources().getStringArray(R.array.supportLangs);
        langList = getResources().getStringArray(R.array.langList);
        final View root = Settings.this.getListView();

        findPreference("preference_language").setSummary(langList[PreferenceManager.getDefaultSharedPreferences(Settings.this).getInt("langlist",0)]);

        supportedLang = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);

        for (String s : langList) {
            supportedLang.add(s);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this, R.style.SettingsTheme_Dialog);
        builder
                .setTitle(R.string.select_lang)
                .setAdapter(supportedLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String lang = supportLangs[which];
                        new setLocale(getBaseContext(), lang);
                        PreferenceManager.getDefaultSharedPreferences(Settings.this)
                                .edit()
                                .putString("lang", lang)
                                .putInt("langlist",which)
                                .apply();
                        findPreference("preference_language").setSummary(langList[which]);
                        Snackbar.make(root, R.string.restart_change, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog alert = builder.create();

                //setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        findPreference("preference_language").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                alert.show();
                return false;
            }
        });

        findPreference("preference_disable_notification").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (preference.isEnabled()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
                    Log.d(getResources().getString(R.string.app_name),"Notifications are disabled.");
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
                    Log.d(getResources().getString(R.string.app_name),"Notifications are enabled.");
                }
                return true;
            }
        });

        findPreference("preference_disable_notification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(PreferenceManager.getDefaultSharedPreferences(Settings.this).getBoolean("preference_disable_notification",true)) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
                    Log.d(getResources().getString(R.string.app_name),"Notifications are disabled.");
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
                    Log.d(getResources().getString(R.string.app_name),"Notifications are enabled.");
                }

                return true;
            }
        });
    }
}
