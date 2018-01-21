package github.grace5921.TwrpBuilder.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.ads.VideoAdsActivity;

/**
 * Created by sumit on 20/11/16.
 */

public class MakeMeHappy extends Fragment {
    private Button LoadAds;
   /* private TextView LoadAdstextView;*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_make_me_happy, container, false);
        LoadAds= view.findViewById(R.id.btn_watch_ads);
/*   LoadAdstextView= view.findViewById(R.id.watch_ads_textview);*/
        LoadAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VideoAdsActivity.class);
                startActivity(intent);

            }
        });
 /*       LoadAdstextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdsActivity.class);
                startActivity(intent);
            }
        });*/
        return view;
    }
}
