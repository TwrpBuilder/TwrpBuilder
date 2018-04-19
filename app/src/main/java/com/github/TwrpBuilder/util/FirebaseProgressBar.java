package com.github.TwrpBuilder.util;

import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.TwrpBuilder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class FirebaseProgressBar {
    private Query query;
    private FirebaseDatabase mFirebaseInstance;

    public FirebaseProgressBar(final ProgressBar progressBar, final TextView textView, FirebaseRecyclerAdapter adapter, final String refId){
        start(progressBar,textView,adapter,refId,false,null,null);
    }

    public FirebaseProgressBar(final ProgressBar progressBar, final TextView textView, FirebaseRecyclerAdapter adapter, final String refId,boolean filter,String from ,String where){
        start(progressBar,textView,adapter,refId,filter,from,where);
    }

   private void start(final ProgressBar progressBar, final TextView textView, FirebaseRecyclerAdapter adapter, final String refId, final boolean filter,String from,String equalto){
        mFirebaseInstance = FirebaseDatabase.getInstance();


        progressBar.setVisibility(View.VISIBLE);
        if (filter)
        {
            query = mFirebaseInstance.getReference(refId).orderByChild(from).equalTo(equalto);
        }else {
            query = mFirebaseInstance.getReference(refId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (!dataSnapshot.exists())
                {

                    if (filter)
                    {
                        textView.setText(R.string.no_builds_found);
                    }
                    else {
                        if (refId == "RunningBuild") {
                            textView.setText(R.string.no_running_builds);
                        } else if (refId == "Builds") {
                            textView.setText(R.string.no_builds_found);
                        }
                        else if (refId=="Rejected")
                        {
                            textView.setText(R.string.no_rejected);
                        }
                    }
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
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
