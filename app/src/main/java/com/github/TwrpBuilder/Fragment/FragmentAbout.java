package com.github.TwrpBuilder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;

/**
 * Created by androidlover5842 on 23.2.2018.
 */

public class FragmentAbout extends Fragment implements View.OnClickListener {
    private CardView cardViewWebsite, cardViewXdaThread, cardViewSource, cardViewTelegram, cardViewBugReport;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_about,container,false);
        cardViewWebsite=v.findViewById(R.id.website);
        cardViewXdaThread=v.findViewById(R.id.xdaThread);
        cardViewSource=v.findViewById(R.id.source);
        cardViewTelegram=v.findViewById(R.id.telegram);
        cardViewBugReport = v.findViewById(R.id.bug_report);

        cardViewWebsite.setOnClickListener(this);
        cardViewXdaThread.setOnClickListener(this);
        cardViewSource.setOnClickListener(this);
        cardViewTelegram.setOnClickListener(this);
        cardViewBugReport.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(@NonNull View view)
    {
        int id=view.getId();
       if (id==cardViewWebsite.getId())
       {
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.OfficialWebsite)));
       }
       else if (id==cardViewXdaThread.getId())
       {
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.XdaThread)));
       }
       else if (id==cardViewSource.getId())
       {
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.GithubSource)));
       }
       else if (id==cardViewTelegram.getId())
       {
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.TGramSupport)));
       } else if (id == cardViewBugReport.getId()) {
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.ReportIssue)));
       }
    }
}
