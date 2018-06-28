package com.github.TwrpBuilder.holder;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.github.TwrpBuilder.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class ListContributorsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private String colon = " : ";
    private String gitId, xdaUrl, donationUrl, description, email, photoUrl, name, key;
    private TextView tvGitId, tvXdaUrl, tvDonation, tvDescription, tvName, tvEmail;
    private CircleImageView imageViewProfile;

    public ListContributorsHolder(View itemView) {
        super(itemView);
        tvGitId = itemView.findViewById(R.id.tv_his_gitId);
        tvXdaUrl = itemView.findViewById(R.id.tv_his_xda);
        tvDescription = itemView.findViewById(R.id.tv_his_description);
        tvName = itemView.findViewById(R.id.tv_his_name);
        tvEmail = itemView.findViewById(R.id.tv_his_email);
        tvDonation = itemView.findViewById(R.id.tv_his_donation);
        imageViewProfile = itemView.findViewById(R.id.img_his_profile);
        this.context = itemView.getContext();

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
        tvGitId.setText(gitId);
        tvName.setText(name);
        tvEmail.setText(email);
        tvXdaUrl.setText(xdaUrl);
        if (description != null) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        }
        if (donationUrl != null) {
            tvDonation.setText(donationUrl);
            tvDonation.setVisibility(View.VISIBLE);
        }
        Glide.with(context.getApplicationContext())
                .load(photoUrl)
                .apply(new RequestOptions().circleCrop().transform(new FitCenter()))
                .into(imageViewProfile);
    }

    @Override
    public void onClick(View view) {

    }
}
