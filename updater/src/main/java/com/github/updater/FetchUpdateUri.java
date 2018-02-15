package com.github.updater;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class FetchUpdateUri extends AsyncTask<Void,Void,Void> {

    private Context context;
    public static Uri url;
    public static String name;

   public FetchUpdateUri(Context context){
       this.context=context;
       this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids)  {

        String url = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/releases/latest";
        String data = null;
        try {
            data = readUrl(url);
        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray= jsonObject.getJSONArray("assets");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);

            System.out.println(jsonObj.get("browser_download_url"));
            Uri uri= Uri.parse(jsonObj.get("browser_download_url").toString());
            String fileName=uri.getLastPathSegment();
            this.url=uri;
            this.name=fileName;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
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
}
