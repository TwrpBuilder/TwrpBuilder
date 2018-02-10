package com.github.updater;

import android.os.Handler;

import static com.github.updater.JsonParser.changelog;
import static com.github.updater.JsonParser.version;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class Updater {

    private JsonParser jsonParser;

    public Updater(final int Version){
        //new RssParser("https://twrpbuilder.firebaseapp.com/app/version.xml");
        jsonParser=new JsonParser("https://twrpbuilder.firebaseapp.com/app/version.json");

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (changelog==null) {
                    ha.postDelayed(this, 10000);
                }
                if (Version<version)
                {
                    System.out.println("Holy " + version);
                }
            }
        }, 10000);

    }
}
