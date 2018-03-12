package com.github.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.FlasherActivity;
import com.github.TwrpBuilder.holder.BuildsHolder;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stericson.RootTools.RootTools;

import java.util.Collections;

import static com.github.TwrpBuilder.app.InitActivity.isSupport;

/**
 * Created by androidlover5842 on 10.3.2018.
 */

public class FragmentStatusCommon extends Fragment {

    private Query query;
    private FirebaseRecyclerAdapter adapter;
    private String reference;
    private boolean bottom;
    private FirebaseRecyclerOptions options;
    private RecyclerView lvBuilds;
    private View view;
    private String filterQuery=null;
    private String equalTo;

    public FragmentStatusCommon(){}

    public FragmentStatusCommon(String reference){
        this.reference=reference;
    }

    public FragmentStatusCommon(String reference,boolean bottom){
        this.reference=reference;
        this.bottom=bottom;
    }
    public FragmentStatusCommon(String reference,String filterQuery,String equalTo){
        this.reference=reference;
        this.filterQuery=filterQuery;
        this.equalTo=equalTo;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_builds_common, container, false);

        lvBuilds = view.findViewById(R.id.lv_builds);
        if (filterQuery != null) {
            query = FirebaseDatabase.getInstance().getReference().child(reference).orderByChild(filterQuery).equalTo(equalTo);

        } else {
            query = FirebaseDatabase.getInstance()
                    .getReference(reference);
        }
        query.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder()
                .setQuery(query, Pbuild.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Pbuild, BuildsHolder>(options) {
            @Override
            public BuildsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_build_common, parent, false);
                if (filterQuery!=null)
                {
                    return new BuildsHolder(view,reference,true,getContext());
                }else {
                    return new BuildsHolder(view,reference,false,getContext());
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull BuildsHolder holder, int position, @NonNull Pbuild model) {
                holder.bind(
                        model.getEmail(),
                        model.getModel(),
                        model.getBoard(),
                        model.getDate(),
                        model.getBrand(),
                        model.getDeveloperEmail(),
                        model.getRejector(),
                        model.getNote(),
                        model.getUrl());
            }
        };
        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,reference);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (bottom)
        {
            llm.setStackFromEnd(true);
        }
        lvBuilds.setLayoutManager(llm);
        lvBuilds.setAdapter(adapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
