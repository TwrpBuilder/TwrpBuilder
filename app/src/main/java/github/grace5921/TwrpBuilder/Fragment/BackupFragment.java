package github.grace5921.TwrpBuilder.Fragment;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.ads.AdsActivity;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.User;

import static github.grace5921.TwrpBuilder.app.FirebaseInstanceIDService.refreshedToken;
import static github.grace5921.TwrpBuilder.util.Config.CheckDownloadedTwrp;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {

    /*Buttons*/
    public static Button mUploadBackup;
    public static Button mDownloadRecovery;
    private Button mBackupButton;
    private Button mCancel;
    /*TextView*/
    private TextView ShowOutput;
    private TextView mBuildDescription;
    private TextView mBuildApproved;
    /*Uri*/
    private Uri file;
    private UploadTask uploadTask;

    /*FireBase*/
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    public static StorageReference riversRef;
    public static StorageReference getRecoveryStatus;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUploader;
    private DatabaseReference mDownloader;

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
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

    /*Notification*/
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    /**/
    private ImageView mRequestApprovedImage;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*Buttons*/

        mBackupButton = (Button) view.findViewById(R.id.BackupRecovery);
        mUploadBackup = (Button) view.findViewById(R.id.UploadBackup);
        mDownloadRecovery = (Button) view.findViewById(R.id.get_recovery);
        mCancel=(Button)view.findViewById(R.id.cancel_upload);

        /*TextView*/

        ShowOutput = (TextView) view.findViewById(R.id.show_output);
        mBuildDescription=(TextView)view.findViewById(R.id.build_description);
        mBuildApproved=(TextView)view.findViewById(R.id.build_approved);
        /*Notification*/
        mNotifyManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        /*Progress Bar*/
        mProgressBar=(ProgressBar)view.findViewById(R.id.progress_bar);

        /*ImageView*/
        mRequestApprovedImage=(ImageView)view.findViewById(R.id.twrp_request_approved);

        /*Define Methods*/

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("Uploader");
        mDownloader=mFirebaseInstance.getReference("Downloader");
        mFirebaseAuth=FirebaseAuth.getInstance();
        Email=mFirebaseAuth.getCurrentUser().getEmail();
        Uid=mFirebaseAuth.getCurrentUser().getUid();
        file = Uri.fromFile(new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar"));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());
        getRecoveryStatus = storageRef.child("output/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + "Twrp.img");

        if(CheckDownloadedTwrp())
        {mDownloadRecovery.setEnabled(false); mBuildDescription.setVisibility(View.GONE);}else{mDownloadRecovery.setEnabled(true); mBuildDescription.setVisibility(View.GONE);}

        /*Buttons Visibility */
        if (Config.checkBackup()) {
            riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    mUploadBackup.setVisibility(View.GONE);
                    mBuildDescription.setVisibility(View.VISIBLE);
                    ShowOutput.setVisibility(View.GONE);
                    try {
                        Intent intent = new Intent(getActivity(), AdsActivity.class);
                        startActivity(intent);
                    }catch (Exception exception)
                    {
                        Toast.makeText(getContext(), R.string.failed_to_load_ads, Toast.LENGTH_LONG).show();

                    }
                    if(mDownloadRecovery.getVisibility()==View.VISIBLE)
                    {mBuildDescription.setVisibility(View.GONE);}else{mBuildDescription.setText(R.string.build_description_text);}


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mUploadBackup.setVisibility(View.VISIBLE);
                }

            });
        }

        getRecoveryStatus.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                mBuildApproved.setVisibility(View.VISIBLE);
                mBuildDescription.setVisibility(View.GONE);
                mBuildApproved.setText(R.string.request_approved);
                mDownloadRecovery.setVisibility(View.VISIBLE);
                mRequestApprovedImage.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }

        });

        /*Find Recovery (Works if device supports /dev/block/platfrom/---/by-name) else gives Exception*/

        try {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
            store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            recovery_output_last_value = parts[7].split("\\]");
            recovery_output_path = recovery_output_last_value[0];
            ShowOutput.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
            store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            ShowOutput.setVisibility(View.VISIBLE);
            try {
                recovery_output_last_value = parts[7].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
            } catch (Exception ExceptionE) {
                Toast.makeText(getContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
            }
        }

        /*Check For Backup */

        if (Config.checkBackup()) {
            ShowOutput.setText(getString(R.string.recovery_mount_point) + recovery_output_path);
        } else {
            if(mDownloadRecovery.getVisibility()==View.VISIBLE)
            {
                mBackupButton.setVisibility(View.GONE);
            }else
            { mBackupButton.setVisibility(View.VISIBLE);
            ShowOutput.setText(R.string.warning_about_recovery_backup);}
        }

        /*On Click Listeners */

        mDownloadRecovery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadStream();
                    }
                }
        );

        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setVisibility(View.GONE);
                        Shell.SU.run("mkdir -p /sdcard/TwrpBuilder ; dd if=" + recovery_output_path + " of=/sdcard/TwrpBuilder/Recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` >  /sdcard/TwrpBuilder/mounts ; getprop ro.build.fingerprint > /sdcard/TwrpBuilder/fingerprint ; tar -c /sdcard/TwrpBuilder/Recovery.img /sdcard/TwrpBuilder/fingerprint /sdcard/TwrpBuilder/mounts > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
                        ShowOutput.setText("Backed up recovery " + recovery_output_path);
                        Snackbar.make(view, R.string.made_recovery_backup, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mUploadBackup.setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();


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
                Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);
                mCancel.setVisibility(View.GONE);
                userId = mUploader.push().getKey();
                User user = new User(Build.BRAND, Build.BOARD,Build.MODEL,Email,Uid,refreshedToken);
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
    private void DownloadStream()  {

        File localFile = new File(Environment.getExternalStorageDirectory(), "TwrpBuilder/Twrp.img");
       /* showHorizontalProgressDialog("Downloading", "Please wait...");*/
        mDownloadRecovery.setEnabled(false);
        getRecoveryStatus.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
               /* hideProgressDialog();*/
                Snackbar.make(getView(), R.string.twrp_downloaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                mBuilder.setContentText(getString(R.string.download_complete));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                mProgressBar.setVisibility(View.GONE);
                mBuildDescription.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);
                mCancel.setVisibility(View.GONE);
                userId = mDownloader.push().getKey();
                User user = new User(Build.BRAND, Build.BOARD,Build.MODEL,Email,Uid,refreshedToken);
                mDownloader.child(userId).setValue(user);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
               /* hideProgressDialog();*/
                Snackbar.make(getView(), R.string.failed_to_download, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                mProgressBar.setVisibility(View.GONE);
                mDownloadRecovery.setEnabled(true);
                mBuildDescription.setVisibility(View.GONE);
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("Download in progress : ", String.valueOf(progress));
                ShowOutput.setVisibility(View.VISIBLE);
                ShowOutput.setText(String.valueOf(progress + "%"));
                mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.downloading))
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setContentText(getString(R.string.downloaded) + progress+"%" + "/100%"+")");
                mNotifyManager.notify(1, mBuilder.build());
                mProgressBar.setVisibility(View.VISIBLE);
                mBuildDescription.setVisibility(View.GONE);
             /*   updateProgress((int) progress);*/

            }
        });
    }
}

