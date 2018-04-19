package com.github.TwrpBuilder.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.TwrpBuilder.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class ListContributorsHolder extends RecyclerView.ViewHolder {
    private Context context;
    private String colon = " : ";
    private String gitId, xdaUrl, donationUrl, description, email, photoUrl, name, key;
    private TextView tvGitId, tvXdaUrl, tvDonation, tvDescription, tvName, tvEmail;
    private ImageView imageViewProfile;

    public ListContributorsHolder(View itemView) {
        super(itemView);
        tvGitId = itemView.findViewById(R.id.tv_his_gitId);
        tvXdaUrl = itemView.findViewById(R.id.tv_his_xda);
        tvDescription = itemView.findViewById(R.id.tv_his_description);
        tvName = itemView.findViewById(R.id.tv_his_name);
        tvEmail = itemView.findViewById(R.id.tv_his_email);
        tvDonation = itemView.findViewById(R.id.tv_his_donation);
        imageViewProfile = itemView.findViewById(R.id.img_his_profile);
        this.context=itemView.getContext();

    }

    public void bind(
            String email,
            String name,
            String photoUrl,
            String xdaUrl,
            String gitId,
            String donationUrl,
            String description,
            String key
    ) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.xdaUrl = xdaUrl;
        this.gitId = gitId;
        this.donationUrl = donationUrl;
        this.description = description;
        this.key = key;
        tvGitId.setText("Git ID"+colon+gitId);
        tvName.setText("name"+colon+name);
        tvEmail.setText("email"+colon+email);
        tvXdaUrl.setText("Xda id"+colon+xdaUrl);
        tvDescription.setText("Bio"+colon+description);
        if (donationUrl!=null)
        tvDonation.setText("Donation"+colon+donationUrl);
        else
            tvDescription.setVisibility(View.GONE);
        Glide.with(context.getApplicationContext()).load(photoUrl).into(imageViewProfile);
    }
}
