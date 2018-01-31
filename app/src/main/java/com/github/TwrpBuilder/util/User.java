package com.github.TwrpBuilder.util;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 */

@IgnoreExtraProperties
public class User {

    private String Brand;
    private String Board;
    private String Model;
    private String Email;
    private String Uid;
    private String FmcToken;
    private String Date;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String Brand, String Board, String Model,String Email,String Uid,String FmcToken,String Date) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.Uid = Uid;
        this.FmcToken = FmcToken;
        this.Date= Date;
    }

    public String getBoard() {
        return Board;
    }

    public String getUid() {
        return Uid;
    }

    public String getFmcToken() {
        return FmcToken;
    }

    public String getBrand() {
        return Brand;
    }

    public String getDate() {
        return Date;
    }

    public String getEmail() {
        return Email;
    }

    public String getModel() {
        return Model;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setFmcToken(String fmcToken) {
        FmcToken = fmcToken;
    }

    public void setBoard(String board) {
        Board = board;
    }

    public void setModel(String model) {
        Model = model;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
