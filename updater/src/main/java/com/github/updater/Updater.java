package com.github.updater;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
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
    public Updater(final Context context, final int Version, String url, final boolean Settings){
        jsonParser=new JsonParser(url);
        final Handler ha=new Handler();
        new FetchUpdateUri(context);

        dialog=new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert).setTitle("Update");

        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (changelog==null) {
                    ha.postDelayed(this, 1000);
                }
                if (Version<version)
                {
                    dialog
                            .setMessage("Changelog :- \n"+ changelog)
                            .setCancelable(false)
                            .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context,"Please wait starting download",Toast.LENGTH_SHORT).show();
                                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);

                                    DownloadManager.Request request = new DownloadManager.Request(FetchUpdateUri.url);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FetchUpdateUri.name);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    Long reference = downloadManager.enqueue(request);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                }else {
                    if (Settings)
                    {
                        Toast.makeText(context,"No updates were found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 1000);

    }
}
