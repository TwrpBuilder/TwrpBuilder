package com.github.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.widget.Toast;

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
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TwrpBuilder/TwrpBuilder/releases/latest")));
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
