package com.github.updater;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class JsonParser extends AsyncTask<Void,Void,Void>{

    private String uri;
    private String data;
    public static int version;
    public static String changelog;

    JsonParser(String uri){
        this.uri=uri;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            data = readUrl(uri);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray= jsonObject.getJSONArray("TwrpBuilder");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                version= Integer.valueOf(jsonObj.get("version").toString());
                changelog=jsonObj.get("changelog").toString();
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
