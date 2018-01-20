package github.grace5921.TwrpBuilder.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import github.grace5921.TwrpBuilder.R;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class LBuildsSDeviceAdapter extends BaseAdapter {

    ArrayList email,date,model,board,brand,url;
    private Context context;
    TextView tvEmail;
    TextView tvDate;
    TextView tvDevice;
    TextView tvBoard;
    TextView tvBrand;
    Button btDownload;

    public LBuildsSDeviceAdapter( Context context,ArrayList email, ArrayList date, ArrayList model , ArrayList board, ArrayList brand,ArrayList url) {
        // TODO Auto-generated constructor stub
        this.context=context;
        this.email=email;
        this.date=date;
        this.model=model;
        this.board=board;
        this.brand=brand;
        this.url=url;
    }


    @Override
    public int getCount() {

        return board.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_in_queue, parent,false);
        tvEmail = v.findViewById(R.id.list_user_email);
        tvDevice = v.findViewById(R.id.list_user_device);
        tvBoard = v.findViewById(R.id.list_user_board);
        tvDate= v.findViewById(R.id.list_user_date);
        tvBrand = v.findViewById(R.id.list_user_brand);
        btDownload=v.findViewById(R.id.bt_download);
        tvDate.setText("Date : "+date.get(position).toString());
        tvEmail.setText("Email : "+email.get(position).toString());
        tvDevice.setText("Model : " + model.get(position).toString());
        tvBoard.setText("Board : "+board.get(position).toString());
        tvBrand.setText("Brand : " +brand.get(position).toString());
        btDownload.setVisibility(View.VISIBLE);
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.get(position).toString()));
                context.startActivity(browserIntent);
            }
        });

        return v;
    }
}
