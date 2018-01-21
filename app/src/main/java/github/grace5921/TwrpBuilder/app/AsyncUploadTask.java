package github.grace5921.TwrpBuilder.app;

import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
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

import org.w3c.dom.Text;

import java.io.File;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.DateUtils;
import github.grace5921.TwrpBuilder.util.User;

import static github.grace5921.TwrpBuilder.firebase.FirebaseInstanceIDService.refreshedToken;

/**
 * Created by androidlover5842 on 22/1/18.
 */

public class AsyncUploadTask extends AsyncTask<Void,Void,Void> {

    /*Notification*/
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    public  Button mUploadBackup;
    private Uri file;
    private String Uid;
    private String userId;
    private String Email;
    private ProgressBar mProgressBar;
    private Button mCancel;
    private TextView ShowOutput;
    private Context context;

    /*Firebase*/
    private FirebaseDatabase mFirebaseInstance;
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference riversRef;
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://twrpbuilder.appspot.com/");
    private DatabaseReference mUploader;
    private UploadTask uploadTask;
    private FirebaseAuth mFirebaseAuth;
    private View view;
    public static boolean running=false;


    public AsyncUploadTask(Context context,View view,TextView textView,Button button,Button mUploadBackup,ProgressBar mProgressBar){
        this.context=context;
        this.ShowOutput=textView;
        this.mCancel=button;
        this.mProgressBar=mProgressBar;
        this.mUploadBackup=mUploadBackup;
        this.view=view;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        mFirebaseAuth=FirebaseAuth.getInstance();
        Email=mFirebaseAuth.getCurrentUser().getEmail();
        Uid=mFirebaseAuth.getCurrentUser().getUid();
        file = Uri.fromFile(new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar"));
        riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }
    @Override
    public Void doInBackground(final Void... voids) {
            riversRef = storageRef.child("queue/" + Build.BRAND + "/" + Build.BOARD + "/" + Build.MODEL + "/" + file.getLastPathSegment());
            uploadTask = riversRef.putFile(file);
        /*showHorizontalProgressDialog("Uploading", "Please wait...");*/
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              /*  hideProgressDialog();*/
                    Log.d("Status", "uploadStream : " + taskSnapshot.getTotalByteCount());
                    mUploadBackup.setVisibility(View.GONE);
                    mBuilder.setContentText(context.getString(R.string.upload_finished));
                    mBuilder.setOngoing(false);
                    mNotifyManager.notify(1, mBuilder.build());
                    mProgressBar.setVisibility(View.GONE);
               /* Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);*/
                    mCancel.setVisibility(View.GONE);
                    userId = mUploader.push().getKey();
                    User user = new User(Build.BRAND, Build.BOARD,Build.MODEL,Email,Uid,refreshedToken, DateUtils.getDate());
                    mUploader.child(userId).setValue(user);
                    Snackbar.make(view, R.string.upload_finished, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mUploadBackup.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mUploadBackup.setEnabled(true);
                    mCancel.setVisibility(View.GONE);
                    mBuilder.setContentText(context.getString(R.string.failed_to_upload));
                    mBuilder.setOngoing(false);
                    mNotifyManager.notify(1, mBuilder.build());
                    ShowOutput.setText(R.string.failed_to_upload);
                    Snackbar.make(view, R.string.failed_to_upload, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
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
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(context.getString(R.string.uploading))
                                    .setAutoCancel(false)
                                    .setOngoing(true)
                                    .setContentText(context.getString(R.string.uploaded) + progress+("%") + "/100%"+")");
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
                            Snackbar.make(view, R.string.upload_cancelled, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            mCancel.setVisibility(View.GONE);
                        }
                    });

                }

            });
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running=true;
    }
}
