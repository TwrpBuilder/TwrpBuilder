package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twrpbuilder.rootchecker.RootChecker;

import java.util.ArrayList;
import java.util.List;

import github.grace5921.TwrpBuilder.R;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class MainFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        DevsFragment.ViewPagerAdapter adapter = new DevsFragment.ViewPagerAdapter(getChildFragmentManager());
        if(RootChecker.isDeviceRooted()){
            adapter.addFragment(new BackupFragment(), "Make Request");
        }else {
            adapter.addFragment(new NotRooted(), "Make Request");
        }
        adapter.addFragment(new LBuildsForDeviceFragment(), "Builds for this device");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
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
