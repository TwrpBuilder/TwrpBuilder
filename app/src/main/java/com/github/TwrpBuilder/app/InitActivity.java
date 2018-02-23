package com.github.TwrpBuilder.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.SharedP;
import com.stericson.RootTools.RootTools;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by androidlover5842 on 8.2.2018.
 */

public class InitActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    public static boolean isSupport;
    public static boolean isOldMtk;

    private String Output;
    String file[]=new String[]{
            "/dev/recovery",
            "/dev/block/bootdevice/by-name/FOTAKernel",
            "/dev/block/platform/*/*/by-name/FOTAKernel",
            "/dev/block/bootdevice/by-name/recovery",
            "/dev/block/platform/*/*/by-name/recovery",
            "/dev/block/platform/*/*/by-name/RECOVERY",
            "/dev/block/platform/*/*/by-name/Recovery",
            "/dev/block/platform/*/by-name/recovery",
            "/dev/block/platform/*/by-name/Recovery",
            "/dev/block/platform/mtk-msdc.0/11230000.msdc0/by-name/recovery"
    };
    private SharedPreferences.Editor  editor;
    private int GoogleVersion;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        progressBar=findViewById(R.id.pb_init);
        try {
            GoogleVersion = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (GoogleVersion>=11800000) {
            if (RootTools.isAccessGiven()) {
                new getRecoveryMountTask().execute();
            } else {
                startActivity(new Intent(InitActivity.this, LoginActivity.class));
                isSupport = false;
                finish();
            }
        }else
        {
            Toast.makeText(getBaseContext(),"Please update your Google Play services",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public class getRecoveryMountTask extends AsyncTask<String,String,String>{


        @Override
        public String doInBackground(String... voids) {

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String name = preferences.getString("recoveryPath", "");
            if (name=="") {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (String string : file)
                        {
                            Output= Shell.SU.run("ls "+ string).toString().replace("[","").replace("]","");
                            if (!Output.isEmpty())
                            {
                                if (Output.equals(file[1]))
                                {
                                    isOldMtk=true;
                                    editor = preferences.edit();
                                    editor.putBoolean("isOldMtk",true);
                                    editor.apply();
                                    SharedP.putRecoveryString(getBaseContext(), Output, true);
                                }else {
                                    SharedP.putRecoveryString(getBaseContext(), Output, true);
                                }
                                break;
                            }
                        }
                    }
                }).start();
                }
            isOldMtk=preferences.getBoolean("isOldMtk",false);
            isSupport=preferences.getBoolean("isSupport",false);
            return name;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(InitActivity.this, LoginActivity.class));
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}
