package com.github.TwrpBuilder.Fragment;


import com.github.TwrpBuilder.adapter.AbstractGithubAdapter;
import com.github.TwrpBuilder.adapter.CreditsAdapter;
import com.github.TwrpBuilder.util.Config;

/**
 * Created by: Sumit
 * Date: 19.10.2016 12:43 AM
 */

public class ContributorsFragment extends AbstractGithubFragment
{
    @Override
    public String onTargetURL()
    {
        return Config.URL_CONTRIBUTORS;
    }

    @Override
    public AbstractGithubAdapter onAdapter()
    {
        return new CreditsAdapter(getActivity());
    }
}
