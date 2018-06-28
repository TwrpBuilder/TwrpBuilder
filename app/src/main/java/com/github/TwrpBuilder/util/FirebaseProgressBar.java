package com.github.TwrpBuilder.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.TwrpBuilder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class FirebaseProgressBar {

    public FirebaseProgressBar(@NonNull final ProgressBar progressBar, @NonNull final TextView textView, @NonNull FirebaseRecyclerAdapter adapter, @NonNull final String refId) {
        start(progressBar,textView,adapter,refId,false,null,null);
    }

    public FirebaseProgressBar(@NonNull final ProgressBar progressBar, @NonNull final TextView textView, @NonNull FirebaseRecyclerAdapter adapter, @NonNull final String refId, boolean filter, @NonNull String from, String where) {
        start(progressBar,textView,adapter,refId,filter,from,where);
    }

    private void start(final ProgressBar progressBar, @NonNull final TextView textView, @NonNull FirebaseRecyclerAdapter adapter, @NonNull final String refId, final boolean filter, @NonNull String from, String equalto) {
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();


        progressBar.setVisibility(View.VISIBLE);
        Query query;
        if (filter)
        {
            query = mFirebaseInstance.getReference(refId).orderByChild(from).equalTo(equalto);
        }else {
            query = mFirebaseInstance.getReference(refId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (!dataSnapshot.exists())
                {

                    if (filter)
                    {
                        textView.setText(R.string.no_builds_found);
                    }
                    else {
                        switch (refId) {
                            case "RunningBuild":
                                textView.setText(R.string.no_running_builds);
                                break;
                            case "Builds":
                                textView.setText(R.string.no_builds_found);
                                break;
                            case "Rejected":
                                textView.setText(R.string.no_rejected);
                                break;
                        }
                    }
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                progressBar.setVisibility(View.GONE);
            }
        });


    }
}
