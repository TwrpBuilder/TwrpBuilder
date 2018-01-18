package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.Queue;
import github.grace5921.TwrpBuilder.util.User;

/**
 * Created by androidlover5842 on 18/1/18.
 */

public class FragmentInQueue extends Fragment {
    private Query query;
    private FirebaseListAdapter<Queue> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_inqueue,container,false);

        ListView lvQueue= view.findViewById(R.id.lv_inqueue);

        query = FirebaseDatabase.getInstance()
                .getReference("InQueue");

        FirebaseListOptions<Queue> options = new FirebaseListOptions.Builder<Queue>()
                .setLayout(R.layout.list_in_queue)
                .setQuery(query,Queue.class)
                .build();

        adapter=new FirebaseListAdapter<Queue>(options) {
            @Override
            protected void populateView(View v, Queue model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                tvDate.setText("Date : "+model.MDate());
                tvEmail.setText("Email : "+model.MEmail());
                tvDevice.setText("Model : " + model.MModel());
                tvBoard.setText("Board : "+model.MBoard());
                tvBrand.setText("Brand : " +model.MBrand());

            }
        };

        lvQueue.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
