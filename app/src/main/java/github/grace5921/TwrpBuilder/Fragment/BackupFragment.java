package github.grace5921.TwrpBuilder.Fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.MainActivity;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.config.Config;
import github.grace5921.TwrpBuilder.util.ShellUtils;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment
{
    private ShellUtils mShell;
    private Button mBackupButton;
    private TextView ShowOutput;
    private Button mUploadBackup;
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");

    private UploadTask uploadTask;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*this.mShell = ((Activity) getActivity()).getShellSession();*/
        mBackupButton=(Button)view.findViewById(R.id.BackupRecovery);
        ShowOutput=(TextView)view.findViewById(R.id.show_output);
        mUploadBackup=(Button)view.findViewById(R.id.UploadBackup);

        try {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
            store_RecoveryPartitonPath_output=String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            recovery_output_last_value = parts[7].split("\\]");
            recovery_output_path=recovery_output_last_value[0];
        }catch (Exception e){
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
            store_RecoveryPartitonPath_output=String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            recovery_output_last_value = parts[7].split("\\]");
            recovery_output_path=recovery_output_last_value[0];
        }


        if(Config.checkBackup()) {
            mBackupButton.setEnabled(false);
            mUploadBackup.setEnabled(true);
            ShowOutput.setText("Recovery mount point "+recovery_output_path);
        }
        else {
            mBackupButton.setEnabled(true);
            mUploadBackup.setEnabled(false);
            ShowOutput.setText("If it takes more then 1 min to backup then send me recovery.img from email");
        }

        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setEnabled(false);
                        Shell.SU.run("mkdir -p /sdcard/TwrpBuilder ; dd if="+recovery_output_path+" of=/sdcard/TwrpBuilder/Recovery.img ; ls -la `find /dev/block/platform/ -type d -name \"by-name\"` >  /sdcard/TwrpBuilder/mounts ; getprop ro.build.fingerprint > /sdcard/TwrpBuilder/fingerprint ; tar -c /sdcard/TwrpBuilder/Recovery.img /sdcard/TwrpBuilder/fingerprint /sdcard/TwrpBuilder/mounts > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar ");
                        ShowOutput.setText("Backed up recovery "+recovery_output_path);
                        Snackbar.make(view, "Made Recovery Backup. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mUploadBackup.setEnabled(true);
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

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

                        mUploadBackup.setEnabled(false);
                        Uri file = Uri.fromFile(new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar"));
                        UploadTask uploadTask = storageRef.putFile(file);
                        StorageReference riversRef = storageRef.child("queue/"+ Build.BRAND+"/"+Build.BOARD+"/"+Build.MODEL+"/"+file.getLastPathSegment());
                        uploadTask = riversRef.putFile(file);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Snackbar.make(view, "Upload Finish. ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println(taskSnapshot.toString());

                            }
                        });

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mUploadBackup.setEnabled(true);
                                Snackbar.make(view, "Something went wrong. ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }

                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });

                    }
                }
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
