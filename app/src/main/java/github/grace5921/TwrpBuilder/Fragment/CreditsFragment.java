package github.grace5921.TwrpBuilder.Fragment;


import github.grace5921.TwrpBuilder.adapter.AbstractGithubAdapter;
import github.grace5921.TwrpBuilder.adapter.CreditsAdapter;
import github.grace5921.TwrpBuilder.util.Config;

/**
 * Created by: Sumit
 * Date: 19.10.2016 12:43 AM
 */

public class CreditsFragment extends AbstractGithubFragment
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
