package github.grace5921.TwrpBuilder.app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import static github.grace5921.TwrpBuilder.firebase.FirebaseInstanceIDService.refreshedToken;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.DateUtils;
import github.grace5921.TwrpBuilder.util.User;

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
        System.out.println("SHit sharted");
        mCancel= findViewById(R.id.cancel_upload);
        ShowOutput = findViewById(R.id.show_output);
        mProgressBar= findViewById(R.id.progress_bar);
        ShowOutput.setText("Uploading ...");
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        mFirebaseAuth=FirebaseAuth.getInstance();
        Email=mFirebaseAuth.getCurrentUser().getEmail();
        Uid=mFirebaseAuth.getCurrentUser().getUid();
        file = Uri.fromFile(new File("/sdcard/TwrpBuilder/"+ Config.TwrpBackFName));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        fromI=true;
        uploadTask = riversRef.putFile(file);
        /*showHorizontalProgressDialog("Uploading", "Please wait...");*/
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              /*  hideProgressDialog();*/
                Log.d("Status", "uploadStream : " + taskSnapshot.getTotalByteCount());
                mBuilder.setContentText(getString(R.string.upload_finished));
                mBuilder.setOngoing(false);
                mNotifyManager.notify(1, mBuilder.build());
               /* Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);*/
                userId = mUploader.push().getKey();
                User user = new User(Build.BRAND, Build.BOARD, Build.MODEL, Email, Uid, refreshedToken, DateUtils.getDate());
                mUploader.child(userId).setValue(user);
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
                /*hideProgressDialog();*/

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                final double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("uploadDataInMemory progress : ", String.valueOf(progress));
                ShowOutput.setText(String.valueOf(progress + "%"));
                mBuilder =
                        new NotificationCompat.Builder(UploaderActivity.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.uploading))
                                .setAutoCancel(false)
                                .setOngoing(true)
                                .setContentText(getString(R.string.uploaded) + progress + ("%") + "/100%" + ")");
                mNotifyManager.notify(1, mBuilder.build());
                /*updateProgress((int) progress);*/
                /*To cancel upload*/
                mCancel.setText("Cancel Upload");

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
        builder.setMessage("Are you sure you want to cancel?")
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
