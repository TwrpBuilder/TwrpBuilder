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
import android.widget.LinearLayout;
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

import static com.github.TwrpBuilder.app.InitActivity.isOldMtk;
import static com.github.TwrpBuilder.app.UploaderActivity.fromI;
import static com.github.TwrpBuilder.app.UploaderActivity.result;
import static com.github.TwrpBuilder.util.Config.Sdcard;


/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {

    /*Buttons*/
    public  Button mUploadBackup;
    private Button mBackupButton;

    /*TextView*/
    private TextView mBuildDescription;

    /*ProgressBar*/
    ProgressBar mProgressBar;

    /*FireBase*/
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public StorageReference riversRef;

    private UploaderActivity uploaderActivity;
    private Intent intent;

    private boolean hasUpB;

    private SharedPreferences preferences;
    private String recoveryPath;
    private LinearLayout fragment_backup_child_linear;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);
        mBuildDescription= view.findViewById(R.id.build_description);
        mProgressBar=view.findViewById(R.id.progress_bar);
        fragment_backup_child_linear = view.findViewById(R.id.fragment_backup_child_linear);
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/"+Config.TwrpBackFName);
        uploaderActivity=new UploaderActivity();
        intent=new Intent(getActivity(), uploaderActivity.getClass());
        mProgressBar.setVisibility(View.VISIBLE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        recoveryPath = preferences.getString("recoveryPath", "");

            riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    mProgressBar.setVisibility(View.GONE);
                    mUploadBackup.setVisibility(View.GONE);
                    mBuildDescription.setVisibility(View.VISIBLE);
                    hasUpB=true;
                        if (!Config.checkBackup()) {mBackupButton.setVisibility(View.VISIBLE);}

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mProgressBar.setVisibility(View.GONE);
                        if (!Config.checkBackup()) {mBackupButton.setVisibility(View.VISIBLE);}
                        else {
                            mUploadBackup.setVisibility(View.VISIBLE);
                            mBuildDescription.setVisibility(View.GONE);

                        }
                }

            });

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
            Snackbar.make(fragment_backup_child_linear, R.string.upload_finished, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fromI=false;
        }
        else {
            mUploadBackup.setVisibility(View.VISIBLE);
            Snackbar.make(fragment_backup_child_linear, R.string.failed_to_upload, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            fromI=false;
        }
        }

    }

    class BackupTask extends AsyncTask<Void,String,Void>
    {


        @Override
        protected Void doInBackground(Void... params) {
            ShellExecuter.mkdir("TwrpBuilder");
            try {
                ShellExecuter.cp("/system/build.prop",Sdcard+"TwrpBuilder/build.prop");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isOldMtk==true)
            {
                Shell.SU.run("dd if=" + recoveryPath +" bs=6291456 count=1 of=" + Sdcard + "TwrpBuilder/recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` > " + Sdcard + "TwrpBuilder/mounts ; cd " + Sdcard + "TwrpBuilder && tar -c recovery.img build.prop mounts > " + Sdcard + "TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
            }
            else
            {
                Shell.SU.run("dd if=" + recoveryPath + " of=" + Sdcard + "TwrpBuilder/recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` > " + Sdcard + "TwrpBuilder/mounts ; cd " + Sdcard + "TwrpBuilder && tar -c recovery.img build.prop mounts > " + Sdcard + "TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
            }
            compressGzipFile(Sdcard+"/TwrpBuilder/TwrpBuilderRecoveryBackup.tar",Sdcard+"TwrpBuilder/"+Config.TwrpBackFName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
            if (!hasUpB) {
                mUploadBackup.setVisibility(View.VISIBLE);
            }
            mBuildDescription.setText("Backed up recovery " + recoveryPath);
            Snackbar.make(fragment_backup_child_linear, "Backup Done", Snackbar.LENGTH_LONG)
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

