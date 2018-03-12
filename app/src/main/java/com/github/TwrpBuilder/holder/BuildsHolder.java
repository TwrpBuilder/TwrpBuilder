package com.github.TwrpBuilder.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.FlasherActivity;
import com.stericson.RootTools.RootTools;

import static com.github.TwrpBuilder.app.InitActivity.isSupport;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class BuildsHolder extends RecyclerView.ViewHolder {
    private TextView tvEmail,tvDevice,tvBoard,tvDate,tvBrand,tvDeveloper,tvNote;
    private Button btDownload,btFlash;
    private Context context;
    private boolean filterQuery;
    private String reference;
    private String colon=" : ";

    public BuildsHolder(View v, String reference, boolean filterQuery, final Context context) {
        super(v);
        this.context=context;
        this.filterQuery=filterQuery;
        this.reference=reference;
        tvEmail = v.findViewById(R.id.list_user_email);
        tvDevice = v.findViewById(R.id.list_user_device);
        tvBoard = v.findViewById(R.id.list_user_board);
        tvDate= v.findViewById(R.id.list_user_date);
        tvBrand = v.findViewById(R.id.list_user_brand);
        tvDeveloper=v.findViewById(R.id.list_developer_email);
        tvNote=v.findViewById(R.id.list_reject_note);
        btDownload=v.findViewById(R.id.bt_download_recovery);
        btFlash=v.findViewById(R.id.bt_flash);

    }

    public void bind(String email,String device,String board,String date,String brand,String developer,String reject,String note,String url){
        setTvBoard(board);
        setTvEmail(email);
        setTvBrand(brand);
        setTvDeveloper(developer);
        setTvNote(note);
        setTvDate(date);
        setTvDevice(device);
        if (reference.equals("Builds"))
        {
            tvDeveloper.setVisibility(View.VISIBLE);
            btDownload.setVisibility(View.VISIBLE);
            // tvDeveloper.setText(getString(R.string.developer) +colon+model.getDeveloperEmail());
            setBtDownload(url);
            if (filterQuery)
            {
                if (RootTools.isAccessGiven() && isSupport)
                {
                    btFlash.setVisibility(View.VISIBLE);
                }

                btFlash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, FlasherActivity.class));
                    }
                });

            }
        }else if (reference.equals("Rejected"))
        {
            tvDeveloper.setVisibility(View.VISIBLE);
            tvNote.setVisibility(View.VISIBLE);
             tvDeveloper.setText(context.getString(R.string.rejected_by)+colon +reject);

        }
    }

    public void setTvBoard(String board) {
        tvBoard.setText(context.getString(R.string.board)+colon +board);
    }

    public void setBtDownload(final String url) {
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });

    }

    public void setBtFlash(Button btFlash) {
        this.btFlash = btFlash;
    }

    public void setTvBrand(String brand) {
        tvBrand.setText(context.getString(R.string.brand)+colon +brand);
    }

    public void setTvDate(String date) {
        tvDate.setText(context.getString(R.string.date)+colon +date);
    }

    public void setTvDeveloper(String developer) {
        tvDeveloper.setText(context.getString(R.string.developer)+colon +developer);
    }

    public void setTvDevice(String device) {
        tvDevice.setText(context.getString(R.string.model)+colon +device);
    }

    public void setTvEmail(String email) {
        tvEmail.setText(context.getString(R.string.email)+colon +email);
    }

    public void setTvNote(String note) {
        tvNote.setText(context.getString(R.string.note)+colon +note);
    }
}
