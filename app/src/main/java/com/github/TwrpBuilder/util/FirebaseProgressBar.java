package com.github.TwrpBuilder.util;

import android.database.DataSetObserver;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.TwrpBuilder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class FirebaseProgressBar {
    private DatabaseReference ref;
    private FirebaseDatabase mFirebaseInstance;

   public void start(final ProgressBar progressBar, final TextView textView, FirebaseListAdapter adapter, final String refId){
        mFirebaseInstance = FirebaseDatabase.getInstance();
        ref = mFirebaseInstance.getReference(refId);


        progressBar.setVisibility(View.VISIBLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // the initial data has been loaded, hide the progress bar
                progressBar.setVisibility(View.GONE);
                if (!dataSnapshot.exists())
                {

                    if (refId=="RunningBuild"){
                        textView.setText(R.string.no_running_builds);
                    }else if (refId=="Builds")
                    {
                        textView.setText(R.string.no_builds_found);
                    }
                    textView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        });


    }
}
