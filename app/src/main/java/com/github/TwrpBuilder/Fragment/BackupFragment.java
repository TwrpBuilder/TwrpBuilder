package com.github.TwrpBuilder.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;
import java.util.zip.GZIPOutputStream;

import eu.chainfire.libsuperuser.Shell;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.CustomBackupActivity;
import com.github.TwrpBuilder.app.UploaderActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.ShellExecuter;

import static com.github.TwrpBuilder.app.CustomBackupActivity.FromCB;
import static com.github.TwrpBuilder.app.CustomBackupActivity.resultOfB;
import static com.github.TwrpBuilder.app.UploaderActivity.fromI;
import static com.github.TwrpBuilder.app.UploaderActivity.result;


/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {

    /*Buttons*/
    public  Button mUploadBackup;
    private Button mBackupButton;
    private Button CustomBackUp;

    /*TextView*/
    private TextView mBuildDescription;

    /*ProgressBar*/
    ProgressBar mProgressBar;

    /*FireBase*/
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public StorageReference riversRef;

    /*Strings*/
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;

    private UploaderActivity uploaderActivity;
    private Intent intent;

    private boolean hasUpB,isSupport;

    private SharedPreferences.Editor editor;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*Buttons*/

        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);
        CustomBackUp=view.findViewById(R.id.bt_custom_backup);

        /*TextView*/

        mBuildDescription= view.findViewById(R.id.build_description);

        /**/
        mProgressBar=view.findViewById(R.id.progress_bar);

        /*Define Methods*/
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/"+Config.TwrpBackFName);

        uploaderActivity=new UploaderActivity();
        intent=new Intent(getActivity(), uploaderActivity.getClass());
        mProgressBar.setVisibility(View.VISIBLE);

        RecoveryPath();

        /*Buttons Visibility */
            riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    mProgressBar.setVisibility(View.GONE);
                    mUploadBackup.setVisibility(View.GONE);
                    mBuildDescription.setVisibility(View.VISIBLE);
                    hasUpB=true;
                    if (isSupport==true) {
                        if (!Config.checkBackup()) {mBackupButton.setVisibility(View.VISIBLE);}
                    }else {
                        if (!Config.checkBackup()) {CustomBackUp.setVisibility(View.VISIBLE);}
                        Snackbar.make(getView(),"Device not supported",Snackbar.LENGTH_SHORT).show();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mProgressBar.setVisibility(View.GONE);
                    if (isSupport==true) {
                        if (!Config.checkBackup()) {mBackupButton.setVisibility(View.VISIBLE);}
                        else {
                            mUploadBackup.setVisibility(View.VISIBLE);
                            mBuildDescription.setVisibility(View.GONE);

                        }
                    }
                else {
                        if (!Config.checkBackup()) {CustomBackUp.setVisibility(View.VISIBLE);}
                        else {
                            mUploadBackup.setVisibility(View.VISIBLE);
                            mBuildDescription.setVisibility(View.GONE);

                        }
                        Snackbar.make(getView(),"Device not supported",Snackbar.LENGTH_SHORT).show();
                    }
                }

            });

        /*On Click Listeners */


        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setVisibility(View.GONE);
                        mBuildDescription.setText(getString(R.string.warning_about_recovery_backup));
                        mBuildDescription.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        new BackupTask().execute();
                    }
                }
        );

        CustomBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomBackupActivity.class));
                CustomBackUp.setVisibility(View.GONE);
            }
        });

        mUploadBackup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                        mUploadBackup.setVisibility(View.GONE);
                    }
                }
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(fromI==true)
        {
        if (result==true)
        {
            mBuildDescription.setVisibility(View.VISIBLE);
            Snackbar.make(getView(), R.string.upload_finished, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fromI=false;
        }
        else {
            mUploadBackup.setVisibility(View.VISIBLE);
            Snackbar.make(getView(), R.string.failed_to_upload, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fromI=false;
        }
        }

        if (FromCB==true)
        {
            if (resultOfB==true)
            {
                if (!hasUpB) {
                    mUploadBackup.setVisibility(View.VISIBLE);
                }else {
                    mBuildDescription.setVisibility(View.VISIBLE);
                }
                resultOfB=false;
            }
            else {
                CustomBackUp.setVisibility(View.VISIBLE);
            }
        }
    }

    private String RecoveryPath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String name = preferences.getString("recoveryPath", "");
        if (name=="") {
            try {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                recovery_output_last_value = parts[1].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
                editor = preferences.edit();
                editor.putString("recoveryPath", recovery_output_path);
                editor.putBoolean("isSupport",true);
                editor.apply();
                isSupport=true;

            } catch (Exception e) {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                try {
                    recovery_output_last_value = parts[1].split("\\]");
                    recovery_output_path = recovery_output_last_value[0];
                    editor = preferences.edit();
                    editor.putString("recoveryPath", recovery_output_path);
                    editor.putBoolean("isSupport",true);
                    editor.apply();
                    isSupport=true;
                } catch (Exception ExceptionE) {
                    isSupport=false;
                    editor = preferences.edit();
                    editor.putBoolean("isSupport",false);
                    editor.apply();
                    mBackupButton.setVisibility(View.GONE);
                    Toast.makeText(getContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
                }
            }
        }
        isSupport=preferences.getBoolean("isSupport",false);

        return name;
    }

    class BackupTask extends AsyncTask<Void,String,Void>
    {


        @Override
        protected Void doInBackground(Void... params) {
            ShellExecuter.mkdir("TwrpBuilder");
            try {
                ShellExecuter.cp("/system/build.prop","/sdcard/TwrpBuilder/build.prop");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Shell.SU.run("dd if=" + RecoveryPath() + " of=/sdcard/TwrpBuilder/recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` >  /sdcard/TwrpBuilder/mounts ; cd /sdcard/TwrpBuilder && tar -c recovery.img build.prop mounts > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
            compressGzipFile("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar","/sdcard/TwrpBuilder/"+Config.TwrpBackFName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
            if (!hasUpB) {
                mUploadBackup.setVisibility(View.VISIBLE);
            }
            mBuildDescription.setText("Backed up recovery " + RecoveryPath());
            Snackbar.make(getView(), "Backup Done", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private static void compressGzipFile(String file, String gzipFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

