package com.github.TwrpBuilder.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by: veli
 * Date: 10/25/16 5:45 PM
 */

abstract public class AbstractGithubAdapter extends BaseAdapter {
    final LayoutInflater mInflater;
    private final Context mContext;

    AbstractGithubAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    abstract protected JSONArray onIndex();

    abstract protected void onUpdate(JSONArray list);

    @Nullable
    abstract protected View onView(int position, View convertView, ViewGroup parent);

    Context getContext() {
        return this.mContext;
    }

    @Override
    public int getCount() {
        return this.onIndex().length();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        try {
            return this.onIndex().getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(JSONArray newList) {
        this.onUpdate(newList);
        this.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.onView(position, convertView, parent);
    }
}
