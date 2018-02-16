package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 26/1/18.
 */

public class Message {

    private String title;
    private String body;

    public Message() {
    }

    public Message(String title,String body) {
        this.title=title;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}