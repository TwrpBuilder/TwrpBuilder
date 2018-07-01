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

class JsonParser extends AsyncTask<Void, Void, Void> {

    public static double version;
    static String changelog;
    static Uri apkURL;
    static String apkName;
    private final String uri;

    JsonParser(String uri) {
        this.uri = uri;
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
        try {
            String data = readUrl(uri);
            JSONObject jsonObject = new JSONObject(data);
            version = Double.valueOf(jsonObject.get("tag_name").toString().substring(1));
            changelog = String.copyValueOf(jsonObject.get("body").toString().toCharArray());
            JSONArray jsonArray = jsonObject.getJSONArray("assets");
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            apkURL = Uri.parse(jsonObj.get("browser_download_url").toString());
            apkName = apkURL.getLastPathSegment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
