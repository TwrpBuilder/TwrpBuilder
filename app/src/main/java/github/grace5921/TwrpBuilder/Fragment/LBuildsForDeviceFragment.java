package github.grace5921.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_build_started,container,false);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        final ListView lvRunningBuilds= view.findViewById(R.id.lv_build_started);
        progressBar=view.findViewById(R.id.pb_builds);
        final ArrayList<String> da = new ArrayList<String>();
        final ArrayList<String> e = new ArrayList<String>();
        final ArrayList<String> bo = new ArrayList<String>();
        final ArrayList<String> ba = new ArrayList<String>();
        final ArrayList<String> ur = new ArrayList<String>();
        final ArrayList<String> mo = new ArrayList<String>();

        mFirebaseInstance.getReference("Builds")
                .orderByChild("Model")
                .equalTo(Build.MODEL)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    String date1=data.child("Date").getValue(String.class);
                                    String email1=data.child("Email").getValue(String.class);
                                    String model1=data.child("Model").getValue(String.class);
                                    String board1=data.child("Board").getValue(String.class);
                                    String brand1=data.child("Brand").getValue(String.class);
                                    String url1=data.child("Url").getValue(String.class);
                                    da.add(date1);
                                    e.add(email1);
                                    bo.add(board1);
                                    ba.add(brand1);
                                    ur.add(url1);
                                    mo.add(model1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

        runnable = new Runnable() {
            @Override
            public void run() {
                if(da.isEmpty()) {
                    Log.i("e","null");
                    progressBar.setVisibility(View.VISIBLE);
                    CheckBuilds();
                }else {
                    progressBar.setVisibility(View.GONE);
                    lvRunningBuilds.setAdapter(new LBuildsSDeviceAdapter(getContext(),e,da,mo,bo,ba,ur));
                }
            }
        };
        handler=new Handler();
        //lvRunningBuilds.setAdapter(adapter);
        handler.postAtTime(runnable, 1000);

        return view;

    }

    public void CheckBuilds(){
        //do what you want
        handler.postDelayed(runnable, 2000);
    }


}
