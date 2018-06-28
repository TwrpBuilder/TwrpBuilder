package com.github.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.holder.BuildsHolder;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by androidlover5842 on 10.3.2018.
 */

public class FragmentStatusCommon extends Fragment {

    @Nullable
    private FirebaseRecyclerAdapter adapter;
    private String reference;
    private View view;
    @Nullable
    private String filterQuery=null;
    private String equalTo;

    public FragmentStatusCommon(){}

    public FragmentStatusCommon(String reference){
        this.reference=reference;
    }

    public FragmentStatusCommon(String reference, @Nullable String filterQuery, String equalTo) {
        this.reference=reference;
        this.filterQuery=filterQuery;
        this.equalTo=equalTo;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_builds_common, container, false);

        RecyclerView lvBuilds = view.findViewById(R.id.lv_builds);
        Query query;
        if (filterQuery != null) {
            query = FirebaseDatabase.getInstance().getReference().child(reference).orderByChild(filterQuery).equalTo(equalTo);

        } else {
            query = FirebaseDatabase.getInstance()
                    .getReference(reference);
        }
        query.keepSynced(true);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder()
                .setQuery(query, Pbuild.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Pbuild, BuildsHolder>(options) {
            @NonNull
            @Override
            public BuildsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_build_common, parent, false);
                if (filterQuery!=null) {
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

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            ProgressBar();
            }
        };
        ProgressBar();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lvBuilds.setLayoutManager(layoutManager);
        lvBuilds.setAdapter(adapter);
        return view;

    }

    private void ProgressBar(){
        ProgressBar progressBar= view.findViewById(R.id.pb_builds);
        TextView textView= view.findViewById(R.id.tv_no_build);
        if (filterQuery!=null){
            new FirebaseProgressBar(progressBar,textView,adapter,reference,true,filterQuery,equalTo);
        }else {
            new FirebaseProgressBar(progressBar, textView, adapter, reference);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
