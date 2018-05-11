package com.github.TwrpBuilder.app;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.setLocale;
import com.github.updater.Updater;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

/**
 * Created by androidlover5842 on 23.2.2018.
 */

public class SettingsActivity extends AppCompatActivity {
    private CardView cardView_Lang;
    private TextView textView_currentLang;
    private String myLang=Locale.getDefault().getLanguage();
    private ArrayAdapter<String> supportedLang;
    private LinearLayout linearLayout;
    private AlertDialog.Builder builderSingle;
    private CardView CheckUpdate;
    private AppCompatCheckBox NotificationCheckBox;
    private String[] supportLangs=new String[]{
            "en",
            "ar",
            "tr",
            "ro",
            "es"
    };
    private String[] langList=new String[]{
            "English",
            "Arabic",
            "Turkish",
            "Romanian",
            "Spanish",
            "French"
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar=findViewById(R.id.action_bar_tool);
        toolbar.setTitle(R.string.settings);
        cardView_Lang=findViewById(R.id.settings_lang);
        textView_currentLang=findViewById(R.id.current_lang);
        textView_currentLang.setText(myLang);
        CheckUpdate=findViewById(R.id.settings_update);
        builderSingle = new AlertDialog.Builder(SettingsActivity.this);
        NotificationCheckBox=findViewById(R.id.settings_notification_checkBox);
        linearLayout=findViewById(R.id.settings_view);
        supportedLang=new ArrayAdapter<>(SettingsActivity.this,android.R.layout.select_dialog_singlechoice);

        boolean checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("notification", false);
        NotificationCheckBox.setChecked(checked);

        for (String s: langList)
        {
            supportedLang.add(s);
        }
        builderSingle
                .setTitle(R.string.select_lang)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        CheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, R.string.checking_for_updates,Toast.LENGTH_SHORT).show();
                new Updater(SettingsActivity.this,Config.Version, Config.APP_UPDATE_URL,true);
            }
        });

        cardView_Lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builderSingle.setAdapter(supportedLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strName = supportedLang.getItem(which);
                        if (strName.equals(langList[0])) {
                            new setLocale(getBaseContext(),supportLangs[0]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[0]).commit();
                        }
                        else if (strName.equals(langList[1]))
                        {
                            new setLocale(getBaseContext(),supportLangs[1]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[1]).commit();
                        }else if (strName.equals(langList[2]))
                        {
                            new setLocale(getBaseContext(),supportLangs[2]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[2]).commit();
                        }else if (strName.equals(langList[3]))
                        {
                            new setLocale(getBaseContext(),supportLangs[3]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[3]).commit();
                        }else if (strName.equals(langList[4]))
                        {
                            new setLocale(getBaseContext(),supportLangs[4]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[4]).commit();
			}else if (strName.equals(langList[5]))
                        {
                            new setLocale(getBaseContext(),supportLangs[5]);
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("lang", supportLangs[5]).commit();
			 }
                        Snackbar.make(linearLayout, R.string.restart_change,Snackbar.LENGTH_SHORT).show();

                    }
                });
                builderSingle.show();


            }
        });

        NotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("notification", true).commit();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
                }else {
                    PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean("notification", false).commit();
                    FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
                }
            }
        });
    }


}
