package github.grace5921.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.adapter.LBuildsSDeviceAdapter;
import github.grace5921.TwrpBuilder.util.Pbuild;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class LBuildsForDeviceFragment extends Fragment {
    private FirebaseDatabase mFirebaseInstance;
    private Handler handler;
    private Runnable runnable;
    private ProgressBar progressBar;
    private TextView textView;
    private DatabaseReference rootRef;
    private FirebaseListAdapter<Pbuild> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_build_for_device,container,false);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        textView=(TextView)view.findViewById(R.id.tv_no_build);
        rootRef = FirebaseDatabase.getInstance().getReference();

        ListView buildList = view.findViewById(R.id.build_list_view);
        progressBar=view.findViewById(R.id.pb_builds);

        Query query = FirebaseDatabase.getInstance().getReference().child("Builds").orderByChild("Model").equalTo(Build.MODEL);
        FirebaseListOptions<Pbuild> options = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_in_queue)
                .setQuery(query, new SnapshotParser<Pbuild>() {
                    @NonNull
                    @Override
                    public Pbuild parseSnapshot(@NonNull DataSnapshot snapshot) {
                        Pbuild model;
                        model = snapshot.getValue(Pbuild.class);
                        return model;
                    }
                })
                .build();

        adapter=new FirebaseListAdapter<Pbuild>(options) {
            @Override
            protected void populateView(View v, final Pbuild model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                Button btDownload=v.findViewById(R.id.bt_download);
                tvDate.setText("Date : "+model.WDate());
                tvEmail.setText("Email : "+model.WEmail());
                tvDevice.setText("Model : " + model.WModel());
                tvBoard.setText("Board : "+model.WBoard());
                tvBrand.setText("Brand : " +model.WBrand());
                btDownload.setVisibility(View.VISIBLE);
                btDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.WUrl()));
                        startActivity(browserIntent);
                    }
                });

            }
        };

        buildList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
