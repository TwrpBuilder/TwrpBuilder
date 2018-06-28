package com.github.updater;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

class FetchUpdateUri extends AsyncTask<Void, Void, Void> {

    public static Uri url;
    public static String name;

    FetchUpdateUri() {
        this.execute();
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    @Nullable
    @Override
    protected Void doInBackground(Void... voids) {

        String url = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/releases/latest";
        String data;
        try {
            data = readUrl(url);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("assets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Uri uri = Uri.parse(jsonObj.get("browser_download_url").toString());
                String fileName = uri.getLastPathSegment();
                FetchUpdateUri.url = uri;
                name = fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
