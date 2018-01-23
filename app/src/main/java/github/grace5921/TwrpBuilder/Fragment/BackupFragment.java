package github.grace5921.TwrpBuilder.Fragment;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.app.UploaderActivity;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.ShellExecuter;

import static github.grace5921.TwrpBuilder.app.UploaderActivity.fromI;
import static github.grace5921.TwrpBuilder.app.UploaderActivity.result;


/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {

    /*Buttons*/
    public static Button mUploadBackup;
    private Button mBackupButton;

    /*TextView*/
    private TextView mBuildDescription;
    private TextView ShowOutput;

    /*ProgressBar*/
    ProgressBar mProgressBar;

    /*FireBase*/
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public static StorageReference riversRef;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;

    /*Strings*/
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;

    private UploaderActivity uploaderActivity;
    private Intent intent;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_backup, container, false);
        /*Buttons*/

        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);

        /*TextView*/

        ShowOutput = view.findViewById(R.id.show_output);
        mBuildDescription= view.findViewById(R.id.build_description);

        /**/
        mProgressBar=view.findViewById(R.id.progress_bar);

        /*Define Methods*/

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/TwrpBuilderRecoveryBackup.tar");

        uploaderActivity=new UploaderActivity();
        intent=new Intent(getActivity(), uploaderActivity.getClass());

        /*Buttons Visibility */
        if (Config.checkBackup()) {
            riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    mUploadBackup.setVisibility(View.GONE);
                    mBuildDescription.setVisibility(View.VISIBLE);
                    ShowOutput.setVisibility(View.GONE);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mUploadBackup.setVisibility(View.VISIBLE);
                    mBuildDescription.setVisibility(View.GONE);
                }

            });
        }else {
            mBackupButton.setVisibility(View.VISIBLE);
        }

        /*Find Recovery (Works if device supports /dev/block/platfrom/---/by-name) else gives Exception*/

        /*Check For Backup */

        if (Config.checkBackup()) {
            ShowOutput.setText(getString(R.string.recovery_mount_point) + RecoveryPath());
        } else {

        }

        /*On Click Listeners */


        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setVisibility(View.GONE);
                        ShowOutput.setText(getString(R.string.warning_about_recovery_backup));
                        ShowOutput.setVisibility(View.VISIBLE);
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
    }

    private String RecoveryPath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String name = preferences.getString("recoveryPath", "");
        System.out.println("RecoveryPath "+ name);
        if (name == null) {
            try {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                recovery_output_last_value = parts[1].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("recoveryPath", recovery_output_path);
                editor.apply();

            } catch (Exception e) {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                try {
                    recovery_output_last_value = parts[1].split("\\]");
                    recovery_output_path = recovery_output_last_value[0];
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("recoveryPath", recovery_output_path);
                    editor.apply();
                } catch (Exception ExceptionE) {
                    Toast.makeText(getContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
                }
            }
        }

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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
            mUploadBackup.setVisibility(View.VISIBLE);
            ShowOutput.setText("Backed up recovery " + RecoveryPath());
            Snackbar.make(getView(), "Backup Done", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}

