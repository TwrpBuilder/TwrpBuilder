package com.github.TwrpBuilder.Fragment;

import android.content.SharedPreferences;
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

import com.twrpbuilder.rootchecker.RootChecker;

import java.util.ArrayList;
import java.util.List;

import com.github.TwrpBuilder.R;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class MainFragment extends Fragment{
    private BackupFragment backupFragment;
    private LBuildsForDeviceFragment lBuildsForDeviceFragment;
    private FragmentCustomBackup fragmentCustomBackup;
    /*Strings*/
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;
    private SharedPreferences.Editor editor;
    private boolean isSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        ViewPager viewPager = view.findViewById(R.id.pager);
        backupFragment=new BackupFragment();
        fragmentCustomBackup=new FragmentCustomBackup();
        lBuildsForDeviceFragment=new LBuildsForDeviceFragment();

        DevsFragment.ViewPagerAdapter adapter = new DevsFragment.ViewPagerAdapter(getChildFragmentManager());
        if(RootChecker.isDeviceRooted()){
            RecoveryPath();
            if (isSupport==true) {
                adapter.addFragment(backupFragment, "Make Request");
            }else {
                adapter.addFragment(fragmentCustomBackup,"Make Request");

            }
        }else {
            adapter.addFragment(fragmentCustomBackup, "Make Request");
        }
        adapter.addFragment(lBuildsForDeviceFragment, "Builds for this device");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private String RecoveryPath() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String name = preferences.getString("recoveryPath", "");
        if (name=="") {
            try {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                recovery_output_last_value = parts[1].split("\\]");
                recovery_output_path = recovery_output_last_value[0];
                editor = preferences.edit();
                editor.putString("recoveryPath", recovery_output_path);
                editor.putBoolean("isSupport",true);
                editor.apply();
                isSupport=true;

            } catch (Exception e) {
                RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
                store_RecoveryPartitonPath_output = String.valueOf(RecoveryPartitonPath);
                parts = store_RecoveryPartitonPath_output.split("->\\s");
                try {
                    recovery_output_last_value = parts[1].split("\\]");
                    recovery_output_path = recovery_output_last_value[0];
                    editor = preferences.edit();
                    editor.putString("recoveryPath", recovery_output_path);
                    editor.putBoolean("isSupport",true);
                    editor.apply();
                    isSupport=true;
                } catch (Exception ExceptionE) {
                    isSupport=false;
                    editor = preferences.edit();
                    editor.putBoolean("isSupport",false);
                    editor.apply();
                    Toast.makeText(getContext(), R.string.device_not_supported, Toast.LENGTH_LONG).show();
                }
            }
        }
        isSupport=preferences.getBoolean("isSupport",false);

        return name;
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
