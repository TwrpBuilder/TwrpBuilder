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
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by androidlover5842 on 10.3.2018.
 */

public class FragmentStatusCommon extends Fragment {

    private Query query;
    private FirebaseListAdapter adapter;
    private String reference;
    private boolean bottom;
    private String colon=" : ";
    public FragmentStatusCommon(){}

    public FragmentStatusCommon(String reference){
        this.reference=reference;
    }

    public FragmentStatusCommon(String reference,boolean bottom){
        this.reference=reference;
        this.bottom=bottom;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_builds_common,container,false);

        ListView lvBuilds= view.findViewById(R.id.lv_builds);

        query = FirebaseDatabase.getInstance()
                .getReference(reference);
        query.keepSynced(true);
        FirebaseListOptions options = new FirebaseListOptions.Builder()
                .setLayout(R.layout.list_build_common)
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
                TextView tvNote=v.findViewById(R.id.list_reject_note);
                Button btDownload=v.findViewById(R.id.bt_download_recovery);

                tvDate.setText(getString(R.string.date)+colon+model.getDate());
                tvEmail.setText(getString(R.string.email)+colon+model.getEmail());
                tvDevice.setText(getString(R.string.model)+colon + model.getModel());
                tvBoard.setText(getString(R.string.board)+colon+model.getBoard());
                tvBrand.setText(getString(R.string.brand) +colon+model.getBrand());
                if (reference.equals("Builds"))
                {
                    tvDeveloper.setVisibility(View.VISIBLE);
                    btDownload.setVisibility(View.VISIBLE);
                    tvDeveloper.setText(getString(R.string.developer) +colon+model.getDeveloperEmail());
                    btDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getUrl()));
                            startActivity(browserIntent);
                        }
                    });

                }else if (reference.equals("Rejected"))
                {
                    tvDeveloper.setVisibility(View.VISIBLE);
                    tvNote.setVisibility(View.VISIBLE);
                    tvDeveloper.setText(getString(R.string.rejected_by)+colon +model.getRejector());
                    tvNote.setText(getString(R.string.note)+colon+model.getNote());

                }

            }
        };

        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,reference);

        if (bottom)
        {
            lvBuilds.setStackFromBottom(true);
        }
        
        lvBuilds.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
