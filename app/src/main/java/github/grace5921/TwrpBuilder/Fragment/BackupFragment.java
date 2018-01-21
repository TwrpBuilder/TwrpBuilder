package github.grace5921.TwrpBuilder.Fragment;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.DateUtils;
import github.grace5921.TwrpBuilder.util.ShellExecuter;
import github.grace5921.TwrpBuilder.util.User;

import static github.grace5921.TwrpBuilder.firebase.FirebaseInstanceIDService.refreshedToken;
import static github.grace5921.TwrpBuilder.util.Config.CheckDownloadedTwrp;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {

    /*Buttons*/
    public static Button mUploadBackup;
    private Button mBackupButton;
    private Button mCancel;
    /*TextView*/
    private TextView ShowOutput;
    private TextView mBuildDescription;

    /*Uri*/
    private Uri file;
    private UploadTask uploadTask;

    /*FireBase*/
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public static StorageReference riversRef;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUploader;

    /*Strings*/
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;
    private String userId;
    private String Email;
    private String Uid;
    /*Progress Bar*/
    private ProgressBar mProgressBar;

    /*Notification*/
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*Buttons*/

        mBackupButton = view.findViewById(R.id.BackupRecovery);
        mUploadBackup = view.findViewById(R.id.UploadBackup);
        mCancel= view.findViewById(R.id.cancel_upload);

        /*TextView*/

        ShowOutput = view.findViewById(R.id.show_output);
        mBuildDescription= view.findViewById(R.id.build_description);
        /*Notification*/
        mNotifyManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        /*Progress Bar*/
        mProgressBar= view.findViewById(R.id.progress_bar);

        /*Define Methods*/

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        mFirebaseAuth=FirebaseAuth.getInstance();
        Email=mFirebaseAuth.getCurrentUser().getEmail();
        Uid=mFirebaseAuth.getCurrentUser().getUid();
        file = Uri.fromFile(new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar"));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());


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
                }

            });
        }else {
            mBackupButton.setVisibility(View.VISIBLE);
        }

        /*Find Recovery (Works if device supports /dev/block/platfrom/---/by-name) else gives Exception*/

        try {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
            store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("->\\s");
            recovery_output_last_value = parts[1].split("\\]");
            recovery_output_path = recovery_output_last_value[0];
        } catch (Exception e) {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
            store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("->\\s");
            try {
                recovery_output_last_value = parts[1].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
            } catch (Exception ExceptionE) {
                Toast.makeText(getContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
            }
        }
        /*Check For Backup */

        if (Config.checkBackup()) {
            ShowOutput.setText(getString(R.string.recovery_mount_point) + recovery_output_path);
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
                        Snackbar.make(view, R.string.Uploading_please_wait, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action", null).show();
                        //creating a new user
                        uploadStream();
                    }
                }
        );

        return view;
    }

    private void uploadStream() {
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);
        mUploadBackup.setEnabled(false);
        /*showHorizontalProgressDialog("Uploading", "Please wait...");*/
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              /*  hideProgressDialog();*/
                Log.d("Status", "uploadStream : " + taskSnapshot.getTotalByteCount());
                mUploadBackup.setVisibility(View.GONE);
                Snackbar.make(getView(), getString(R.string.upload_finished), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ShowOutput.setText(R.string.build_description_text);
                mBuilder.setContentText(getString(R.string.upload_finished));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                mProgressBar.setVisibility(View.GONE);
               /* Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);*/
                mCancel.setVisibility(View.GONE);
                userId = mUploader.push().getKey();
                User user = new User(Build.BRAND, Build.BOARD,Build.MODEL,Email,Uid,refreshedToken, DateUtils.getDate());
                mUploader.child(userId).setValue(user);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mUploadBackup.setVisibility(View.VISIBLE);
                Snackbar.make(getView(), R.string.failed_to_upload, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                mProgressBar.setVisibility(View.GONE);
                mUploadBackup.setEnabled(true);
                mCancel.setVisibility(View.GONE);
                mBuilder.setContentText(getString(R.string.failed_to_upload));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                ShowOutput.setText(R.string.failed_to_upload);
                /*hideProgressDialog();*/

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                final double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("uploadDataInMemory progress : ", String.valueOf(progress));
                ShowOutput.setVisibility(View.VISIBLE);
                ShowOutput.setText(String.valueOf(progress + "%"));
                mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.uploading))
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setContentText(getString(R.string.uploaded) + progress+("%") + "/100%"+")");
                mNotifyManager.notify(1, mBuilder.build());
                mProgressBar.setVisibility(View.VISIBLE);
                /*updateProgress((int) progress);*/
                /*To cancel upload*/
                mCancel.setVisibility(View.VISIBLE);
                mCancel.setText("Cancel Upload");

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadTask.cancel();
                        mCancel.setVisibility(View.GONE);
                        Snackbar.make(getView(), R.string.upload_cancelled, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }

        });
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
            Shell.SU.run("dd if=" + recovery_output_path + " of=/sdcard/TwrpBuilder/recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` >  /sdcard/TwrpBuilder/mounts ; cd /sdcard/TwrpBuilder && tar -c recovery.img build.prop mounts > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.GONE);
            mUploadBackup.setVisibility(View.VISIBLE);
            ShowOutput.setText("Backed up recovery " + recovery_output_path);
            Snackbar.make(getView(), "Backup Done", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}

