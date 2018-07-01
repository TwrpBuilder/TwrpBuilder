package com.github.updater;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static com.github.updater.JsonParser.apkName;
import static com.github.updater.JsonParser.apkURL;
import static com.github.updater.JsonParser.changelog;
import static com.github.updater.JsonParser.version;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class Updater {

    private final AlertDialog.Builder dialog;

    public Updater(@NonNull final Context context, final double Version, String url, final boolean Settings) {
        new JsonParser(url);
        final Handler ha = new Handler();

        dialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert).setTitle("New update available");

        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (changelog == null) {
                    ha.postDelayed(this, 1000);
                }
                if (Version < version) {
                    dialog
                            .setMessage(changelog)
                            .setCancelable(false)
                            .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, "Downloading! Please wait...", Toast.LENGTH_SHORT).show();
                                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                                    DownloadManager.Request request = new DownloadManager.Request(apkURL);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    if (downloadManager != null) {
                                        Long reference = downloadManager.enqueue(request);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                } else {
                    if (Settings) {
                        Toast.makeText(context, R.string.No_updates_where_found, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 1000);

    }
}
