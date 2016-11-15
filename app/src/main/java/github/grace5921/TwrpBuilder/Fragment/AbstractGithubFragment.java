package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.adapter.AbstractGithubAdapter;

/**
 * Created by: veli
 * Date: 10/25/16 5:45 PM
 */

abstract public class AbstractGithubFragment extends ListFragment
{
    public abstract String onTargetURL();
    public abstract AbstractGithubAdapter onAdapter();

    private AbstractGithubAdapter mAdapter;
    private String mTargetURL;
    private JSONArray mAwaitedList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        this.mAdapter = this.onAdapter();
        this.mTargetURL = this.onTargetURL();

        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
        updateCache();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_github_releases, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menuitem_github_releases_refresh)
        {
            this.updateCache();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public AbstractGithubAdapter getAdapter()
    {
        return this.mAdapter;
    }

    public JSONArray getList()
    {
        return this.mAwaitedList;
    }

    public String getTargetURL()
    {
        return this.mTargetURL;
    }

    public void pushInfoWithThread(final int info)
    {
        if (!isDetached() && getActivity() != null)
            getActivity().runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pushInfo(info);
                        }
                    }
            );
    }

    public void pushInfo(final int info)
    {
        if (!isDetached() && getActivity() != null)
            getActivity().runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            setEmptyText(getString(info));
                        }
                    }
            );
    }

    public void update()
    {
        if (!isDetached() && getActivity() != null)
            getActivity().runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mAwaitedList != null && mAwaitedList.length() > 0)
                                mAdapter.update(mAwaitedList);

                            if (mAdapter.getCount() == 0)
                                pushInfo(R.string.nothing_to_show);
                        }
                    }
            );
    }

    public void updateCache()
    {
        pushInfo(R.string.connecting_to_github);

        new Thread()
        {
            @Override
            public void run()
            {
                super.run();

                try
                {
                    final StringBuilder result = new StringBuilder();

                    HttpRequest httpRequest = HttpRequest.get(mTargetURL);
                    httpRequest.receive(result);

                    mAwaitedList = new JSONArray(result.toString());

                    update();
                } catch (Exception e)
                {
                    pushInfoWithThread(R.string.something_went_wrong);
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
