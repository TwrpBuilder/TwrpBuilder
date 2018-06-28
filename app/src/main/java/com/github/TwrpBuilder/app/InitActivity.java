package com.github.TwrpBuilder.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.SharedP;
import com.github.TwrpBuilder.util.setLocale;
import com.stericson.RootTools.RootTools;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by androidlover5842 on 8.2.2018.
 */

public class InitActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    public static boolean isSupport;
    public static boolean isOldMtk;
    public static boolean ROOT_GRANTED;

    @NonNull
    private final
    String[] file = new String[]{
            "RECOVERY",
            "Recovery",
            "FOTAKernel",
            "fotakernel",
            "recovery"
    };
    private int GoogleVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        progressBar = findViewById(R.id.pb_init);
        try {
            GoogleVersion = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new setLocale(getBaseContext(), PreferenceManager.getDefaultSharedPreferences(InitActivity.this)
                .getString("lang", "en"));

        if (GoogleVersion >= 11800000) {
            ROOT_GRANTED = RootTools.isAccessGiven();
            if (ROOT_GRANTED) {
                new getRecoveryMountTask().execute();
            } else {
                startActivity(new Intent(InitActivity.this, LoginActivity.class));
                isSupport = false;
                finish();
            }
        } else {
            Toast.makeText(getBaseContext(), "Please update your Google Play services", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    class getRecoveryMountTask extends AsyncTask<String, String, String> {

        @Nullable
        @Override
        public String doInBackground(String... voids) {

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String name = preferences.getString("recoveryPath", "");
            if (name.equals("")) {
                String output = Shell.SU.run("find /dev/block/platform -type d -name by-name ").toString().replace("[", "").replace("]", "");
                if (output.isEmpty()) {
                    output = Shell.SU.run("ls /dev/recovery").toString().replace("[", "").replace("]", "");
                    if (!output.isEmpty()) {
                        isOldMtk = true;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isOldMtk", true);
                        editor.apply();
                        SharedP.putRecoveryString(getBaseContext(), output, true);
                    }
                } else {
                    for (String f : file) {
                        String o = Shell.SU.run("ls " + output + File.separator + f).toString().replace("[", "").replace("]", "");
                        if (!o.isEmpty()) {
                            SharedP.putRecoveryString(getBaseContext(), o, true);
                            break;
                        }
                    }

                }
            }
            isOldMtk = preferences.getBoolean("isOldMtk", false);
            isSupport = preferences.getBoolean("isSupport", false);
            return name;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(InitActivity.this, LoginActivity.class));
            finish();
        }

    }

}
