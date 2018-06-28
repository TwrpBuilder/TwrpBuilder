package com.github.TwrpBuilder.adapter;

import android.content.Context;

import org.json.JSONArray;

/**
 * Created by: veli
 * Date: 10/25/16 10:18 PM
 */

abstract public class GithubAdapterIDEA extends AbstractGithubAdapter
{
    private JSONArray mList = new JSONArray();

    GithubAdapterIDEA(Context context)
    {
        super(context);
    }

    @Override
    protected JSONArray onIndex()
    {
        return this.mList;
    }

    @Override
    protected void onUpdate(JSONArray list)
    {
        this.mList = list;
    }
}
