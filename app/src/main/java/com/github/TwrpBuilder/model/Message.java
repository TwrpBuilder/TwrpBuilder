package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 26/1/18.
 */

public class Message {

    private final String title;
    private final String body;

    public Message(String title,String body) {
        this.title=title;
        this.body = body;
    }

    public Message(String title,String body,String email,boolean works) {
        this.title=title;
        this.body = body;
        String email1 = email;
        boolean works1 = works;
    }

}