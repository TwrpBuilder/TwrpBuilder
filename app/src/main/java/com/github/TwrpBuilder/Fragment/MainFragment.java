package com.github.TwrpBuilder.Fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.github.TwrpBuilder.R;
import com.stericson.RootTools.RootTools;

import eu.chainfire.libsuperuser.Shell;

import static com.github.TwrpBuilder.app.InitActivity.isSupport;
import static com.github.TwrpBuilder.util.Config.getBuildModel;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class MainFragment extends Fragment{
    private BackupFragment backupFragment;
    private ViewPagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        ViewPager viewPager = view.findViewById(R.id.pager);
        if (isSupport && RootTools.isAccessGiven())
            backupFragment = new BackupFragment(true);
        else
            backupFragment = new BackupFragment(false);


        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(backupFragment, getString(R.string.make_request));
        //adapter.addFragment(new FragmentStatusCommon("Builds","model", getBuildModel()), "Stable Builds");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

}
