package github.grace5921.TwrpBuilder.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import github.grace5921.TwrpBuilder.config.Config;
import github.grace5921.TwrpBuilder.util.ShellUtils;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment {
    private ShellUtils mShell;

    /*Buttons*/
    private Button mUploadBackup;
    private Button mDownloadRecovery;
    private Button mBackupButton;

    /*TextView*/
    private TextView ShowOutput;
    private TextView mBuildDescription;
    private TextView mBuildApproved;
    /*Uri*/
    private Uri file;
    private UploadTask uploadTask;

    /*FireBase*/
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    private StorageReference riversRef;
    private StorageReference getRecoveryStatus;

    /*Strings*/
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;

    /*Progress Bar*/
    private ProgressDialog mProgressDialog;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);

        /*Buttons*/

        mBackupButton = (Button) view.findViewById(R.id.BackupRecovery);
        mUploadBackup = (Button) view.findViewById(R.id.UploadBackup);
        mDownloadRecovery = (Button) view.findViewById(R.id.get_recovery);

        /*TextView*/

        ShowOutput = (TextView) view.findViewById(R.id.show_output);
        mBuildDescription=(TextView)view.findViewById(R.id.build_description);
        mBuildApproved=(TextView)view.findViewById(R.id.build_approved);

        /*Define Methods*/

        file = Uri.fromFile(new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar"));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());
        getRecoveryStatus = storageRef.child("output/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + "Twrp.img");

        /*Buttons Visibility */
        if (Config.checkBackup()) {
            riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    mUploadBackup.setVisibility(View.GONE);
                    mBuildDescription.setVisibility(View.VISIBLE);
                    mBuildDescription.setText(R.string.build_description_text);

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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }

        });

        /*Check For Backup */

        if (Config.checkBackup()) {
            ShowOutput.setText("Recovery mount point " + recovery_output_path);
        } else {
            mBackupButton.setVisibility(View.VISIBLE);
            ShowOutput.setText("If it takes more then 1 min to backup then send me recovery.img from email");
        }

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
            try {
                recovery_output_last_value = parts[7].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
            } catch (Exception ExceptionE) {
                Toast.makeText(getContext(), "Your devic is not supported", Toast.LENGTH_LONG).show();
            }
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
                        Snackbar.make(view, "Made Recovery Backup. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mUploadBackup.setVisibility(View.VISIBLE);
                    }
                }
        );

        mUploadBackup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "Uploading Please Wait... ", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action", null).show();
                        //creating a new user
                        uploadStream();
                        mUploadBackup.setVisibility(View.GONE);

                    }
                }
        );


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
    private void showProgressDialog(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.setMessage(message);
        else
            mProgressDialog = ProgressDialog.show(getActivity(), title, message, true, false);
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showHorizontalProgressDialog(String title, String body) {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
        } else {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void updateProgress(int progress) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(progress);
        }
    }
    private void uploadStream() {
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);
        showHorizontalProgressDialog("Uploading", "Please wait...");
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                hideProgressDialog();
                Log.d("Status", "uploadStream : " + taskSnapshot.getTotalByteCount());
                mUploadBackup.setEnabled(false);
                Snackbar.make(getView(), "Upload Finish. ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ShowOutput.setText(R.string.build_description_text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mUploadBackup.setEnabled(true);
                Snackbar.make(getView(), "Failed to upload data . ", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                hideProgressDialog();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("uploadDataInMemory progress : ", String.valueOf(progress));
                ShowOutput.setVisibility(View.VISIBLE);
                ShowOutput.setText(String.valueOf(progress + "%"));
                updateProgress((int) progress);
            }

        });
    }
    private void DownloadStream()  {

        File localFile = new File(Environment.getExternalStorageDirectory(), "TwrpBuilder/Twrp.img");
        showHorizontalProgressDialog("Uploading", "Please wait...");
        getRecoveryStatus.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                hideProgressDialog();
                Snackbar.make(getView(), "File downloaded at \n/sdcard/TwrpBuilder/Twrp.img . ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideProgressDialog();
                Snackbar.make(getView(), "Failed To Downlaod . ", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("uploadDataInMemory progress : ", String.valueOf(progress));
                ShowOutput.setVisibility(View.VISIBLE);
                ShowOutput.setText(String.valueOf(progress + "%"));
                updateProgress((int) progress);

            }
        });
    }
}

