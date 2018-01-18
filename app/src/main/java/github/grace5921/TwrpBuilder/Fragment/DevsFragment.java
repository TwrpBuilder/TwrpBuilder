package github.grace5921.TwrpBuilder.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.app.Activity;
import github.grace5921.TwrpBuilder.util.User;

/**
 * Created by sumit on 22/11/16.
 */

public class DevsFragment extends Fragment {

    private Context context;
    private FirebaseListAdapter<User> adapter;

    public DevsFragment(Context context)
    {
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devs, container, false);
        final ListView  mListView = (ListView) view.findViewById(R.id.Lv_devs);


        Query query = FirebaseDatabase.getInstance()
                .getReference("Uploader");

        Log.i("Deva",query.toString());


        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.list_developer_stuff)
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                tvDate.setText(model.WtDate());
                tvEmail.setText(model.WEmail());
                tvDevice.setText(model.WModel());
                tvBoard.setText(model.WBoard());
                tvBrand.setText(model.WBrand());
            }
        };
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
