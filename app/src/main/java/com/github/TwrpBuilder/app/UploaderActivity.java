package com.github.TwrpBuilder.app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.model.Message;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.util.DateUtils;
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
import static com.github.TwrpBuilder.util.Config.TwrpBackFName;
import static com.github.TwrpBuilder.util.Config.getBuildBoard;
import static com.github.TwrpBuilder.util.Config.getBuildBrand;
import static com.github.TwrpBuilder.util.Config.getBuildModel;
import static com.github.TwrpBuilder.util.Config.getBuildProduct;

/**
 * Created by androidlover5842 on 23/1/18.
 */

public class UploaderActivity extends AppCompatActivity {
    private Button mCancel;
    private TextView ShowOutput;
    @NonNull
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    @NonNull
    private final StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    private DatabaseReference mUploader;
    private DatabaseReference mBuildAdded;
    private UploadTask uploadTask;
    @Nullable
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String Uid;
    @Nullable
    private String userId;
    @Nullable
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
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        mBuildAdded = mFirebaseInstance.getReference("mqueue");
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        Email = mFirebaseAuth.getCurrentUser().getEmail();
        Uid = mFirebaseAuth.getCurrentUser().getUid();
        Uri file = Uri.fromFile(new File(Sdcard + "TwrpBuilder/" + TwrpBackFName));
        StorageReference riversRef = storageRef.child("queue/" + getBuildBrand() + "/" + getBuildBoard() + "/" + getBuildModel() + "/" + file.getLastPathSegment());

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
                Pbuild user = new Pbuild(getBuildBrand(), getBuildBoard(), getBuildModel(),getBuildProduct(), Email, Uid, refreshedToken, DateUtils.getDate());
                Message message=new Message("TwrpBuilder","New build in queue for "+getBuildModel()+" "+getBuildBrand());
                mUploader.child(userId).setValue(user);
                mBuildAdded.push().setValue(message);
                result=true;
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mBuilder.setContentText(getString(R.string.failed_to_upload));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
                ShowOutput.setText(R.string.failed_to_upload);
                finish();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                final double progress = (100 * taskSnapshot.getBytesTransferred()) /
                        (taskSnapshot.getTotalByteCount() > 0 ? taskSnapshot.getTotalByteCount() : 1);
                ShowOutput.setText(String.valueOf(progress + "%"));
                mBuilder =
                        new NotificationCompat.Builder(UploaderActivity.this,"2")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.uploading))
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setContentText("Uploading " + progress + ("%") + "/100%" + ")");
                mNotifyManager.notify(1, mBuilder.build());
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadTask.cancel();
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
                    public void onClick(@NonNull DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
