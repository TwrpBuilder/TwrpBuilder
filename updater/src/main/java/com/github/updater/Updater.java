package com.github.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static com.github.updater.JsonParser.changelog;
import static com.github.updater.JsonParser.version;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class Updater {

    private JsonParser jsonParser;
    private AlertDialog.Builder dialog;
    public Updater(final Context context, final int Version, String url){
        //new RssParser("https://twrpbuilder.firebaseapp.com/app/version.xml");
        jsonParser=new JsonParser(url);
        final Handler ha=new Handler();
        dialog=new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert).setTitle("Update");

        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (changelog==null) {
                    ha.postDelayed(this, 10000);
                }
                if (Version<version)
                {
                    System.out.println("Holy " + version);
                    dialog
                            .setMessage("Changelog :- \n"+ changelog)
                            .setCancelable(false)
                            .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context,"Please wait fetching url",Toast.LENGTH_SHORT).show();
                                    new FetchUpdateUri(context);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                }
            }
        }, 10000);

    }
}
