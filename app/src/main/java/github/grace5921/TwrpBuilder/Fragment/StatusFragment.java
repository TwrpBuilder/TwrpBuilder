package github.grace5921.TwrpBuilder.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.grace5921.TwrpBuilder.R;

/**
 * Created by androidlover5842 on 18/1/18.
 */

public class StatusFragment extends Fragment {
    private FragmentTabHost mTabHost;
    private Context context;


    public StatusFragment(Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(),
                R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Building").setIndicator("Building"),
                FragmentBuildStarted.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("In Queue").setIndicator("In Queue"),
                FragmentInQueue.class, null);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

}
