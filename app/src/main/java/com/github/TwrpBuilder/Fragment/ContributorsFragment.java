package com.github.TwrpBuilder.Fragment;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.TwrpBuilder.adapter.AbstractGithubAdapter;
import com.github.TwrpBuilder.adapter.CreditsAdapter;
import com.github.TwrpBuilder.util.Config;

/**
 * Created by: Sumit
 * Date: 19.10.2016 12:43 AM
 */

public class ContributorsFragment extends AbstractGithubFragment {
    @NonNull
    @Override
    public String onTargetURL() {
        return Config.URL_CONTRIBUTORS;
    }

    @Nullable
    @Override
    public AbstractGithubAdapter onAdapter() {
        return new CreditsAdapter(getActivity());
    }
}
