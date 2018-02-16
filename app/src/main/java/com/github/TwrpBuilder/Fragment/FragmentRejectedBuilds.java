package com.github.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.model.Rejected;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.github.TwrpBuilder.model.Pbuild;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by androidlover5842 on 16.2.2018.
 */

public class FragmentRejectedBuilds extends Fragment {
    private Query query;
    private FirebaseListAdapter<Rejected> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_reject_builds,container,false);
        ListView lvRunningBuilds= v.findViewById(R.id.lv_rej_builds);

        query = FirebaseDatabase.getInstance()
                .getReference("Rejected");

        FirebaseListOptions<Rejected> options = new FirebaseListOptions.Builder<Rejected>()
                .setLayout(R.layout.list_reject_builds)
                .setQuery(query,Rejected.class)
                .build();

        adapter=new FirebaseListAdapter<Rejected>(options) {
            @Override
            protected void populateView(View v, final Rejected model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                TextView tvDeveloper=v.findViewById(R.id.list_reject_email);
                TextView tvNote=v.findViewById(R.id.list_reject_note);
                tvDeveloper.setVisibility(View.VISIBLE);
                tvDate.setText("Date : "+model.getDate());
                tvEmail.setText("Email : "+model.getEmail());
                tvDevice.setText("Model : " + model.getModel());
                tvBoard.setText("Board : "+model.getBoard());
                tvBrand.setText("Brand : " +model.getBrand());
                tvDeveloper.setText("Rejected by : " +model.getRejector());
                tvNote.setText("Note: "+model.getNote());
            }
        };

        ProgressBar progressBar= v.findViewById(R.id.pb_builds);
        TextView textView= v.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"Builds");
        lvRunningBuilds.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
