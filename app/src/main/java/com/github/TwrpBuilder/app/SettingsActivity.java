package com.github.TwrpBuilder.app;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;
import com.github.updater.Updater;

import java.util.Locale;

/**
 * Created by androidlover5842 on 23.2.2018.
 */

public class SettingsActivity extends AppCompatActivity {
    private CardView cardView_Lang;
    private TextView textView_currentLang;
    private String myLang=Locale.getDefault().getLanguage();
    private ArrayAdapter<String> supportedLang;
    private String[] supportLangs=new String[]{
            "en",
            "ar",
            "tr"
    };
    private String[] langList=new String[]{
            "English",
            "Arabic",
            "turkish"
    };
    private LinearLayout linearLayout;
    private AlertDialog.Builder builderSingle;
    private CardView CheckUpdate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cardView_Lang=findViewById(R.id.settings_lang);
        textView_currentLang=findViewById(R.id.current_lang);
        textView_currentLang.setText(myLang);
        CheckUpdate=findViewById(R.id.settings_update);
        builderSingle = new AlertDialog.Builder(SettingsActivity.this);
        linearLayout=findViewById(R.id.settings_view);

        supportedLang=new ArrayAdapter<>(SettingsActivity.this,android.R.layout.select_dialog_singlechoice);
        for (String s: langList)
        {
            supportedLang.add(s);
        }
        builderSingle
                .setTitle("Select Language:-")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        CheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Checking for updates...",Toast.LENGTH_SHORT).show();
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
                            setLocale(supportLangs[0]);
                        }
                        else if (strName.equals(langList[1]))
                        {
                            setLocale(supportLangs[1]);
                        }else if (strName.equals(langList[2]))
                        {
                            setLocale(supportLangs[2]);
                        }
                    }
                });
                builderSingle.show();


            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Snackbar.make(linearLayout,"Restart app to apply changes",Snackbar.LENGTH_SHORT).show();
    }


}
