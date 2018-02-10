package com.github.updater;

/**
 * Created by androidlover5842 on 10.2.2018.
 */

public class Updater {

    public Updater(){
        new XmlParser("https://twrpbuilder.firebaseapp.com/app/version.xml");
    }
}
