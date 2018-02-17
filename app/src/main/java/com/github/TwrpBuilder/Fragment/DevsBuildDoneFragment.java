package com.github.TwrpBuilder.Fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.DateUtils;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.model.User;

/**
 * Created by androidlover5842 on 19/1/18.
 */

public class DevsBuildDoneFragment extends Fragment {

    private FirebaseListAdapter<Pbuild> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ListView lvRunningBuilds;
    private Query query;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
    private Button btAddBack;
    private User user;
    private FirebaseListOptions<Pbuild> options;
    private EditText searchView;
    private Query FilterQuery;
    private FirebaseListAdapter<Pbuild> FilterAdapter;
    FirebaseListOptions<Pbuild> FilterOptions;

    public DevsBuildDoneFragment(){
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devs_inqueue, container, false);
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        lvRunningBuilds = view.findViewById(R.id.Lv_devs);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        searchView=view.findViewById(R.id.sv_builds);
        mUploader = mFirebaseInstance.getReference("InQueue");
        query = FirebaseDatabase.getInstance()
                .getReference("Builds");
        searchView.setVisibility(View.VISIBLE);
        options = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_build_done)
                .setQuery(query,Pbuild.class)
                .build();

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, int i, KeyEvent keyEvent) {
                System.out.println("hi "+textView.getText());
                FilterQuery=FirebaseDatabase.getInstance()
                        .getReference("Builds").orderByChild("model").equalTo(textView.getText().toString());
                FilterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            SearchFilter();
                        }
                        else {
                            adapter.startListening();
                            lvRunningBuilds.setStackFromBottom(true);
                            lvRunningBuilds.setAdapter(adapter);
                            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });

        adapter = new FirebaseListAdapter<Pbuild>(options) {
            @Override
            protected void populateView(View v, final Pbuild model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                TextView tvDeveloper=v.findViewById(R.id.list_developer_email);
                Button btFiles=v.findViewById(R.id.BtFile);
                final Button btDRecovery=v.findViewById(R.id.bt_download_recovery);
                btAddBack=v.findViewById(R.id.bt_add_back);
                tvDate.setText("Date : "+model.getDate());
                tvEmail.setText("Email : "+model.getEmail());
                tvDevice.setText("Model : " + model.getModel());
                tvBoard.setText("Board : "+model.getBoard());
                tvBrand.setText("Brand : " +model.getBrand());
                tvDeveloper.setText("Dev Email: " +model.getDeveloperEmail());

                btFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        storageRef.child("queue/" + model.getBrand() + "/" + model.getBoard() + "/" + model.getModel() + "/"+ Config.TwrpBackFName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                btDRecovery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                        startActivity(browserIntent);

                    }
                });

                btAddBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user = new User(model.getBrand(),model.getBoard(),model.getModel(),model.getCodeName(),model.getEmail(),model.getUid(),model.getFmcToken(), DateUtils.getDate());
                        mUploader.push().setValue(user);
                        Snackbar.make(view,"Add "+ model.getModel()+ " to queue",Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        };

        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"Builds");
        lvRunningBuilds.setStackFromBottom(true);
        lvRunningBuilds.setAdapter(adapter);

        return view;
    }

    private void SearchFilter(){
        adapter.stopListening();
        lvRunningBuilds.setStackFromBottom(false);
        FilterOptions = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_build_done)
                .setQuery(FilterQuery,Pbuild.class)
                .build();
        FilterAdapter=new FirebaseListAdapter<Pbuild>(FilterOptions) {
            @Override
            protected void populateView(View v, final Pbuild model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                TextView tvDeveloper=v.findViewById(R.id.list_developer_email);
                Button btFiles=v.findViewById(R.id.BtFile);
                final Button btDRecovery=v.findViewById(R.id.bt_download_recovery);
                btAddBack=v.findViewById(R.id.bt_add_back);
                tvDate.setText("Date : "+model.getDate());
                tvEmail.setText("Email : "+model.getEmail());
                tvDevice.setText("Model : " + model.getModel());
                tvBoard.setText("Board : "+model.getBoard());
                tvBrand.setText("Brand : " +model.getBrand());
                tvDeveloper.setText("Dev Email: " +model.getDeveloperEmail());

                btFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        storageRef.child("queue/" + model.getBrand() + "/" + model.getBoard() + "/" + model.getModel() + "/"+ Config.TwrpBackFName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                btDRecovery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                        startActivity(browserIntent);

                    }
                });

                btAddBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user = new User(model.getBrand(),model.getBoard(),model.getModel(),model.getCodeName(),model.getEmail(),model.getUid(),model.getFmcToken(), DateUtils.getDate());
                        mUploader.push().setValue(user);
                        Snackbar.make(v,"Add "+ model.getModel()+ " to queue",Snackbar.LENGTH_SHORT).show();
                    }
                });

            }

        };
        lvRunningBuilds.setAdapter(FilterAdapter);
        FilterAdapter.startListening();
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
