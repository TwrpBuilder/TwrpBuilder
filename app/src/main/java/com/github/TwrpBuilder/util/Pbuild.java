package com.github.TwrpBuilder.util;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class Pbuild {

    private String Brand;
    private String Board;
    private String Model;
    private String Email;
    private String Uid;
    private String FmcToken;
    private String Date;
    private String Url;

    public Pbuild() {
    }

    public Pbuild(String Brand, String Board, String Model,String Email,String Uid,String FmcToken,String Date,String Url) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.Uid = Uid;
        this.FmcToken = FmcToken;
        this.Date= Date;
        this.Url=Url;
    }

    public String getEmail() {
        return Email;
    }

    public String getBoard() {
        return Board;
    }

    public String getBrand() {
        return Brand;
    }

    public String getDate() {
        return Date;
    }

    public String getModel() {
        return Model;
    }

    public String getFmcToken() {
        return FmcToken;
    }

    public String getUid() {
        return Uid;
    }

    public String getUrl() {
        return Url;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setModel(String model) {
        Model = model;
    }

    public void setBoard(String board) {
        Board = board;
    }

    public void setFmcToken(String fmcToken) {
        FmcToken = fmcToken;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
