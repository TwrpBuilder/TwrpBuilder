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

    private String mtk="/dev/recovery";
    private String Sonyboot="/dev/block/bootdevice/by-name/FOTAKernel";
    private String SonyName="/dev/block/platform/*/*/by-name/FOTAKernel";
    private String qcomBoot="/dev/block/bootdevice/by-name/recovery";
    private String qcomName="/dev/block/platform/*/*/by-name/recovery";
    private String oldSocName="/dev/block/platform/*/*/by-name/RECOVERY";
    private String oldBroadComName="/dev/block/platform/*/*/by-name/Recovery";
    private String Output;
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

        System.out.println("Google version "+GoogleVersion);

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

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String name = preferences.getString("recoveryPath", "");
            if (name=="") {
                if (!Shell.SU.run("ls "+Sonyboot).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ Sonyboot).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else if (!Shell.SU.run("ls "+SonyName).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ SonyName).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else if (!Shell.SU.run("ls "+mtk).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ mtk).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                    isOldMtk=true;
                    editor = preferences.edit();
                    editor.putBoolean("isOldMtk",true);
                    editor.apply();
                }
                else if (!Shell.SU.run("ls "+qcomName).isEmpty())
                {

                    Output= Shell.SU.run("ls "+ qcomName).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else if (!Shell.SU.run("ls "+qcomBoot).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ qcomBoot).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else if (!Shell.SU.run("ls "+oldSocName).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ oldSocName).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else if (!Shell.SU.run("ls "+oldBroadComName).isEmpty())
                {
                    Output= Shell.SU.run("ls "+ oldBroadComName).toString().replace("[","").replace("]","");
                    SharedP.putRecoveryString(getBaseContext(),Output,true);
                }
                else
                {
                    isSupport=false;
                    editor = preferences.edit();
                    editor.putBoolean("isSupport",false);
                    editor.apply();
                }

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
