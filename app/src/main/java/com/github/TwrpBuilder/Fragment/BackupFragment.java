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

import com.github.TwrpBuilder.app.CustomBackupActivity;
import com.github.TwrpBuilder.util.FWriter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.GZIPOutputStream;

import eu.chainfire.libsuperuser.Shell;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.UploaderActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.ShellExecuter;

import static com.github.TwrpBuilder.app.CustomBackupActivity.FromCB;
import static com.github.TwrpBuilder.app.CustomBackupActivity.resultOfB;
import static com.github.TwrpBuilder.app.InitActivity.isOldMtk;
import static com.github.TwrpBuilder.app.UploaderActivity.fromI;
import static com.github.TwrpBuilder.app.UploaderActivity.result;
import static com.github.TwrpBuilder.util.Config.Sdcard;
import static com.github.TwrpBuilder.util.Config.TwrpBackFName;
import static com.github.TwrpBuilder.util.Config.getBuildBoard;
import static com.github.TwrpBuilder.util.Config.getBuildBrand;
import static com.github.TwrpBuilder.util.Config.getBuildModel;


/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment implements View.OnClickListener {

    /*Buttons*/
    public  Button mUploadBackup;
    private Button mBackupButton;

    /*TextView*/
    private TextView mBuildDescription;
    private TextView textViewBrand,textViewModel,textViewBoard,textViewSupported;

    /*ProgressBar*/
    ProgressBar mProgressBar;

    /*FireBase*/
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public StorageReference riversRef;

    private boolean hasUpB;

    private SharedPreferences preferences;
    private String recoveryPath;
    private LinearLayout fragment_backup_child_linear;
    private String colon=" : ";
    private boolean isSupported;

    BackupFragment(boolean isSupported){
        this.isSupported=isSupported;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);
        mBuildDescription= view.findViewById(R.id.build_description);
        mProgressBar=view.findViewById(R.id.progress_bar);
        fragment_backup_child_linear = view.findViewById(R.id.fragment_backup_child_linear);
        riversRef = storageRef.child("queue/" + getBuildBrand() + "/" + getBuildBoard() + "/" + getBuildModel() + "/"+ TwrpBackFName);
        mProgressBar.setVisibility(View.VISIBLE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        recoveryPath = preferences.getString("recoveryPath", "");
        textViewBrand=view.findViewById(R.id.tv_brand);
        textViewModel=view.findViewById(R.id.tv_model);
        textViewBoard=view.findViewById(R.id.tv_board);
        textViewSupported=view.findViewById(R.id.tv_supported);

        textViewBrand.setText(getString(R.string.brand)+colon+Config.getBuildBrand());
        textViewBoard.setText(getString(R.string.board)+colon+Config.getBuildBoard());
        textViewModel.setText(getString(R.string.model)+colon+Config.getBuildModel());
        if (!isSupported)
        {
            textViewSupported.setText("Running in non-root mode");
        }

        checkRequest();

        mBackupButton.setOnClickListener(this);
        mUploadBackup.setOnClickListener(this);
        return view;
    }

    private void checkRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        mProgressBar.setVisibility(View.GONE);
                        mUploadBackup.setVisibility(View.GONE);
                        mBuildDescription.setVisibility(View.VISIBLE);
                        hasUpB=true;
                        checkBackup();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mProgressBar.setVisibility(View.GONE);
                                if (checkBackup()) {mUploadBackup.setVisibility(View.VISIBLE);}
                            }

                        });
            }
        }).start();

    }

    private boolean checkBackup(){
        boolean thisExist=new File(Sdcard+"TwrpBuilder/"+TwrpBackFName).exists();
        if (thisExist)
        {
            mBackupButton.setVisibility(View.GONE);
        }
        else {
            mBackupButton.setVisibility(View.VISIBLE);
        }
        return thisExist;
    }
    @Override
    public void onResume() {
        super.onResume();
        checkBackup();
        checkRequest();
        if(fromI)
        {
        if (result)
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
        if (!isSupported)
        {
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
                    mBackupButton.setVisibility(View.VISIBLE);
                }
            }

        }

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==mBackupButton.getId())
        {
            if (isSupported) {
                mBackupButton.setVisibility(View.GONE);
                mBuildDescription.setText(getString(R.string.warning_about_recovery_backup));
                mBuildDescription.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                new BackupTask().execute();
            }else {
                mBackupButton.setVisibility(View.GONE);
                startActivity(new Intent(getContext(), CustomBackupActivity.class));
            }
        }
        else if (id==mUploadBackup.getId())
        {
            startActivity(new Intent(getActivity(),UploaderActivity.class));
            mUploadBackup.setVisibility(View.GONE);
        }
    }

    class BackupTask extends AsyncTask<Void,String,Void>
    {
        private boolean failed;
        @Override
        protected Void doInBackground(Void... params) {
            ShellExecuter.mkdir("TwrpBuilder");
            new FWriter("build.prop",Config.buildProp());
            try {
                ShellExecuter.cp("/system/build.prop",Sdcard+"TwrpBuilder/build.prop");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isOldMtk==true)
            {
                Shell.SU.run("dd if=" + recoveryPath +" bs=20000000 count=1 of=" + Sdcard + "TwrpBuilder/recovery.img ; cat /proc/dumchar > " + Sdcard + "TwrpBuilder/mounts ; cd " + Sdcard + "TwrpBuilder && tar -c recovery.img build.prop mounts > " + Sdcard + "TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
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
            if (failed)
            {
                mBuildDescription.setText("Failed to create backup .");
                Snackbar.make(fragment_backup_child_linear, "Failed to backup", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }else {
                if (!hasUpB) {
                    mUploadBackup.setVisibility(View.VISIBLE);
                }
                mBuildDescription.setText("Backed up recovery " + recoveryPath);
                Snackbar.make(fragment_backup_child_linear, "Backup Done", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        private void compressGzipFile(String file, String gzipFile) {
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
                failed=true;
                e.printStackTrace();
            }

        }


    }
}

