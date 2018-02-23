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

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.CustomBackupActivity;
import com.github.TwrpBuilder.app.UploaderActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.ShellExecuter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import eu.chainfire.libsuperuser.Shell;

import static com.github.TwrpBuilder.app.CustomBackupActivity.FromCB;
import static com.github.TwrpBuilder.app.CustomBackupActivity.resultOfB;
import static com.github.TwrpBuilder.app.UploaderActivity.fromI;
import static com.github.TwrpBuilder.app.UploaderActivity.result;

/**
 * Created by androidlover5842 on 31.1.2018.
 */

public class FragmentCustomBackup extends Fragment {
    /*Buttons*/
    public Button mUploadBackup;
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

    private UploaderActivity uploaderActivity;
    private Intent intent;

    private boolean hasUpB;
    private LinearLayout fragment_backup_child_linear;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_custom_backup,container,false);
        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);
        mBuildDescription= view.findViewById(R.id.build_description);
        mProgressBar=view.findViewById(R.id.progress_bar);
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/"+ Config.TwrpBackFName);
        uploaderActivity=new UploaderActivity();
        intent=new Intent(getActivity(), uploaderActivity.getClass());
        mProgressBar.setVisibility(View.VISIBLE);
        textViewBrand=view.findViewById(R.id.tv_brand);
        textViewModel=view.findViewById(R.id.tv_model);
        textViewBoard=view.findViewById(R.id.tv_board);
        textViewSupported=view.findViewById(R.id.tv_supported);
        fragment_backup_child_linear= view.findViewById(R.id.fragment_backup_child_linear);

        textViewBrand.setText("Brand : "+Build.BRAND);
        textViewModel.setText("Model : "+Build.MODEL);
        textViewBoard.setText("Board : "+Build.BOARD);
        textViewSupported.setText("Supported by app :- "+false);

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
                        startActivity(new Intent(getContext(), CustomBackupActivity.class));
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
    }
}
