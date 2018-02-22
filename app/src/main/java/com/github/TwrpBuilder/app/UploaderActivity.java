package com.github.TwrpBuilder.app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.TwrpBuilder.model.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static com.github.TwrpBuilder.firebase.FirebaseInstanceIDService.refreshedToken;
import static com.github.TwrpBuilder.util.Config.Sdcard;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.DateUtils;
import com.github.TwrpBuilder.model.User;

/**
 * Created by androidlover5842 on 23/1/18.
 */

public class UploaderActivity extends AppCompatActivity {
    private Button mCancel;
    private TextView ShowOutput;
    private ProgressBar mProgressBar;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference riversRef;
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    private DatabaseReference mUploader;
    private DatabaseReference mBuildAdded;
    private UploadTask uploadTask;
    private FirebaseAuth mFirebaseAuth;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Uri file;
    private String Uid;
    private String userId;
    private String Email;
    public static boolean result;
    public static boolean fromI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);
        Toolbar toolbar=findViewById(R.id.action_bar_tool);
        toolbar.setTitle(R.string.uploading);
        mCancel= findViewById(R.id.cancel_upload);
        ShowOutput = findViewById(R.id.show_output);
        mProgressBar= findViewById(R.id.progress_bar);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        mBuildAdded = mFirebaseInstance.getReference("mqueue");
        mFirebaseAuth=FirebaseAuth.getInstance();
        Email=mFirebaseAuth.getCurrentUser().getEmail();
        Uid=mFirebaseAuth.getCurrentUser().getUid();
        file = Uri.fromFile(new File(Sdcard+"TwrpBuilder/"+ Config.TwrpBackFName));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        fromI=true;
        uploadTask = riversRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mBuilder.setContentText(getString(R.string.upload_finished));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                userId = mUploader.push().getKey();
                User user = new User(Build.BRAND, Build.BOARD, Build.MODEL,Build.PRODUCT, Email, Uid, refreshedToken, DateUtils.getDate());
                Message message=new Message("TwrpBuilder","New build in queue for "+Build.BRAND);
                mUploader.child(userId).setValue(user);
                mBuildAdded.push().setValue(message);
                finish();
                result=true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mBuilder.setContentText(getString(R.string.failed_to_upload));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                ShowOutput.setText(R.string.failed_to_upload);
                result=false;
                finish();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                final double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                ShowOutput.setText(String.valueOf(progress + "%"));
                mBuilder =
                        new NotificationCompat.Builder(UploaderActivity.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.uploading))
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setContentText(getString(R.string.uploaded) + progress + ("%") + "/100%" + ")");
                mNotifyManager.notify(1, mBuilder.build());
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadTask.cancel();
                        result=false;
                        finish();
                    }
                });

            }

        });

    }

     @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cancel_warning)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uploadTask.cancel();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
