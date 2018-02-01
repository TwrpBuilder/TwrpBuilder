package com.github.TwrpBuilder.Fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.ActivitySubmitBuild;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.github.TwrpBuilder.util.User;

/**
 * Created by androidlover5842 on 19/1/18.
 */

public class DevsBuildRunningFragment extends Fragment {
    private FirebaseListAdapter<User> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ListView mListView;
    private Query query;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    DevsBuildRunningFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devs_inqueue, container, false);
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        mListView = view.findViewById(R.id.Lv_devs);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("Builds");
        userId = mUploader.push().getKey();
        query = FirebaseDatabase.getInstance()
                .getReference("RunningBuild");

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.list_build_running)
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, final User model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                final TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                Button btFiles=v.findViewById(R.id.BtFile);
                final Button btBuildDone=v.findViewById(R.id.bt_build_done);
                tvDate.setText("Date : "+model.getDate());
                tvEmail.setText("Email : "+model.getEmail());
                tvDevice.setText("Model : " + model.getModel());
                tvBoard.setText("Board : "+model.getBoard());
                tvBrand.setText("Brand : " +model.getBrand());

                btFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        storageRef.child("queue/" + model.getBrand() + "/" + model.getBoard() + "/" + model.getModel() + "/"+ Config.TwrpBackFName ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                String fileName=model.getModel()+"-"+model.getBoard()+"-"+model.getEmail()+".tar.gz";
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                Long reference = downloadManager.enqueue(request);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });


                        Toast.makeText(getContext(),model.getModel(),Toast.LENGTH_SHORT).show();
                    }
                });

                btBuildDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // User user = new User(model.getBrand(),model.getBoard(),model.getModel(),model.getEmail(),model.WUid(),model.WFmcToken(),model.getDate());
                        //mUploader.child(userId).setValue(user);
                        final Intent intent=new Intent(getContext(), ActivitySubmitBuild.class);
                        intent.putExtra("Brand",model.getBrand());
                        intent.putExtra("Board",model.getBoard());
                        intent.putExtra("Model",model.getModel());
                        intent.putExtra("Email",model.getEmail());
                        intent.putExtra("Uid",model.getUid());
                        intent.putExtra("Fmc",model.getFmcToken());
                        mFirebaseInstance.getReference("RunningBuild")
                                .orderByChild("Model")
                                .equalTo(model.getModel())
                                .addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                    intent.putExtra("somekey",child.getKey().toString());
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                                            }
                                        });

                    }
                });

            }
        };
        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"RunningBuild");
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
