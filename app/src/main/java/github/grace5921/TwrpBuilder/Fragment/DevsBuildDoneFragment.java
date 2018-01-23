package github.grace5921.TwrpBuilder.Fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.DateUtils;
import github.grace5921.TwrpBuilder.util.FirebaseProgressBar;
import github.grace5921.TwrpBuilder.util.Pbuild;
import github.grace5921.TwrpBuilder.util.Queue;
import github.grace5921.TwrpBuilder.util.User;

/**
 * Created by androidlover5842 on 19/1/18.
 */

public class DevsBuildDoneFragment extends Fragment {

    private FirebaseListAdapter<Pbuild> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ListView mListView;
    private Query query;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    private Button btAddBack;
    private User user;

    DevsBuildDoneFragment(){
        //Empty Constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devs_inqueue, container, false);
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        mListView = view.findViewById(R.id.Lv_devs);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("InQueue");
        userId = mUploader.push().getKey();
        query = FirebaseDatabase.getInstance()
                .getReference("Builds");

        FirebaseListOptions<Pbuild> options = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_developer_stuff)
                .setQuery(query,Pbuild.class)
                .build();

        adapter = new FirebaseListAdapter<Pbuild>(options) {
            @Override
            protected void populateView(View v, final Pbuild model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                Button btFiles=v.findViewById(R.id.BtFile);
                final Button btStartBuild=v.findViewById(R.id.bt_start_build);
                final Button btBuildDone=v.findViewById(R.id.bt_build_done);
                btAddBack=v.findViewById(R.id.bt_add_back);
                btAddBack.setVisibility(View.VISIBLE);
                tvDate.setText("Date : "+model.WDate());
                tvEmail.setText("Email : "+model.WEmail());
                tvDevice.setText("Model : " + model.WModel());
                tvBoard.setText("Board : "+model.WBoard());
                tvBrand.setText("Brand : " +model.WBrand());
                btStartBuild.setVisibility(View.GONE);
                btBuildDone.setVisibility(View.VISIBLE);
                btBuildDone.setText("Recovery");

                btFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        storageRef.child("queue/" + model.WBrand() + "/" + model.WBoard() + "/" + model.WModel() + "/"+ Config.TwrpBackFName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                String fileName=model.WModel()+"-"+model.WBoard()+"-"+model.WEmail()+".tar";
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


                        Toast.makeText(getContext(),model.WModel(),Toast.LENGTH_SHORT).show();
                    }
                });

                btBuildDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.WUrl()));
                        startActivity(browserIntent);

                    }
                });

                btAddBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user = new User(model.WBrand(),model.WBoard(),model.WModel(),model.WEmail(),model.WUid(),model.WFmcToken(), DateUtils.getDate());
                        mUploader.child(userId).setValue(user);
                    }
                });

            }
        };

        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"Builds");

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
