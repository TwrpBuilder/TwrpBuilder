package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.grace5921.TwrpBuilder.R;

/**
 * Created by sumit on 14/11/16.
 */

public class HelpFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_help, container, false);
        return view;
    }
}
