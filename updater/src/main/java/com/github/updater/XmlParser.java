package com.github.updater;

import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class XmlParser extends AsyncTask<Void,Void,Void> {

    String uri;
    public XmlParser(String url){
        this.uri=url;
        this.execute();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser responseParser = factory.newPullParser();
            responseParser.setInput(new StringReader(FetchXml()));
            int eventType = responseParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType==XmlPullParser.START_TAG)
                {
                    System.out.println("ashisa "+responseParser.getName());
                    if (responseParser.getName().equals("version"))
                    {
                    }
                }
                eventType = responseParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            // Your code
        }

        return null;
    }

    private String FetchXml() throws IOException {
        BufferedReader bufferedReader=null;
        try {
        URL url=new URL(uri);
        bufferedReader=new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer buffer = new StringBuffer();
        int read;
        char[] chars = new char[1024];
        while ((read = bufferedReader.read(chars)) != -1)
            buffer.append(chars, 0, read);

        return buffer.toString();
    } finally {
        if (bufferedReader != null)
            bufferedReader.close();
    }

}
}
