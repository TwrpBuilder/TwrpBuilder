package com.github.TwrpBuilder.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import com.github.TwrpBuilder.R;

/**
 * Created by: veli
 * Date: 10/23/16 12:51 PM
 */

public class CreditsAdapter extends GithubAdapterIDEA
{
    public CreditsAdapter(Context context)
    {
        super(context);
    }

    @Override
    protected View onView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.list_credits, parent, false);

        TextView text1 = convertView.findViewById(R.id.list_credits_name);
        TextView text2 = convertView.findViewById(R.id.list_credits_contributions);
        JSONObject release = (JSONObject) getItem(position);

        try
        {
            if (release.has("login"))
                text1.setText(release.getString("login"));

            if (release.has("contributions"))
                text2.setText(getContext().getString(R.string.contribution_counter_info, release.getInt("contributions")));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return convertView;
    }

}