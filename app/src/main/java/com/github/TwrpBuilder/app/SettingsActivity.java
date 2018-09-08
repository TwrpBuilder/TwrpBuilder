package com.github.TwrpBuilder.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.setLocale;
import com.github.updater.Updater;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by androidlover5842 on 23.2.2018.
 */

public class SettingsActivity extends AppCompatActivity {
    @NonNull
    private final String[] supportLangs = new String[]{
            "en",
            "ar",
            "tr",
            "ro",
            "es",
            "fr",
            "it"
    };
    @NonNull
    private final String[] langList = new String[]{
            "English",
            "Arabic",
            "Turkish",
            "Romanian",
            "Spanish",
            "French",
            "Italian"
    };
    private ArrayAdapter<String> supportedLang;
    private LinearLayout linearLayout;
    private AlertDialog.Builder builderSingle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.action_bar_tool);
        toolbar.setTitle(R.string.settings);
        CardView cardView_Lang = findViewById(R.id.settings_lang);
        final TextView textView_currentLang = findViewById(R.id.current_lang);
        textView_currentLang.setText(PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
                .getString("lang", "en"));
        CardView checkUpdate = findViewById(R.id.settings_update);
        builderSingle = new AlertDialog.Builder(SettingsActivity.this);
        AppCompatCheckBox notificationCheckBox = findViewById(R.id.settings_notification_checkBox);
        linearLayout = findViewById(R.id.settings_view);
        supportedLang = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.select_dialog_singlechoice);

        boolean checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("notification", false);
        notificationCheckBox.setChecked(checked);

        for (String s : langList) {
            supportedLang.add(s);
        }
        builderSingle
                .setTitle(R.string.select_lang)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, R.string.checking_for_updates, Toast.LENGTH_SHORT).show();
                new Updater(SettingsActivity.this, Config.getAppVersion(getApplicationContext()), Config.APP_UPDATE_URL, true);
            }
        });

        cardView_Lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderSingle.setAdapter(supportedLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String lang = supportLangs[which];
                        new setLocale(getBaseContext(), lang);
                        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
                                .edit()
                                .putString("lang", lang)
                                .apply();
                        textView_currentLang.setText(lang);
                        Snackbar.make(linearLayout, R.string.restart_change, Snackbar.LENGTH_SHORT).show();
                    }
                });
                builderSingle.show();


            }
        });

        notificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("notification", true).apply();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
                } else {
                    PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("notification", false).apply();
                    FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
                }
            }
        });
    }


}
