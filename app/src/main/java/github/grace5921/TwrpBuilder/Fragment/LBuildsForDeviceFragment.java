package github.grace5921.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.FirebaseProgressBar;
import github.grace5921.TwrpBuilder.util.Pbuild;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class LBuildsForDeviceFragment extends Fragment {
    private FirebaseListAdapter<Pbuild> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_build_for_device,container,false);

        ListView buildList = view.findViewById(R.id.build_list_view);

        Query query = FirebaseDatabase.getInstance().getReference().child("Builds").orderByChild("Model").equalTo(Build.MODEL);
        FirebaseListOptions<Pbuild> options = new FirebaseListOptions.Builder<Pbuild>()
                .setLayout(R.layout.list_in_queue)
                .setQuery(query, Pbuild.class)
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


        ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.pb_builds);
        final TextView textView=(TextView)view.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar().start(progressBar,textView,adapter,"Builds");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    textView.setText("No builds found");
                    textView.setVisibility(View.VISIBLE);

                }
                else {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buildList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
