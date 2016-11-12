package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.app.Activity;
import github.grace5921.TwrpBuilder.util.ShellUtils;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment
{
    ShellUtils mShell;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*this.mShell = ((Activity) getActivity()).getShellSession();*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
