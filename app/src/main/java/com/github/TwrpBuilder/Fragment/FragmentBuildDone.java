package com.github.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.github.TwrpBuilder.util.Pbuild;

/**
 * Created by androidlover5842 on 19/1/18.
 */

public class FragmentBuildDone extends Fragment {
    private Query query;
    private FirebaseListAdapter<Pbuild> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_build_started,container,false);

        ListView lvRunningBuilds= view.findViewById(R.id.lv_build_started);

        query = FirebaseDatabase.getInstance()
                .getReference("Builds");

        FirebaseListOptions<Pbuild> options = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_in_queue)
                .setQuery(query,Pbuild.class)
                .build();

        adapter=new FirebaseListAdapter<Pbuild>(options) {
            @Override
            protected void populateView(View v, final Pbuild model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                TextView tvDeveloper=v.findViewById(R.id.list_developer_email);
                Button btDownload=v.findViewById(R.id.bt_download);
                tvDeveloper.setVisibility(View.VISIBLE);
                tvDate.setText("Date : "+model.getDate());
                tvEmail.setText("Email : "+model.getEmail());
                tvDevice.setText("Model : " + model.getModel());
                tvBoard.setText("Board : "+model.getBoard());
                tvBrand.setText("Brand : " +model.getBrand());
                tvDeveloper.setText("Developer : " +model.getDeveloperEmail());
                btDownload.setVisibility(View.VISIBLE);
                btDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                        startActivity(browserIntent);
                    }
                });

            }
        };

        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        lvRunningBuilds.setStackFromBottom(true);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"Builds");
        lvRunningBuilds.setAdapter(adapter);


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
