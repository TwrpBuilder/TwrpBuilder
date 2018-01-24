package com.github.TwrpBuilder.util;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 */

@IgnoreExtraProperties
public class User {

    public String Brand;
    public String Board;
    public String Model;
    public  String Email;
    public String Uid;
    public String FmcToken;
    public String Date;
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

    public String WEmail() {
        return Email;
    }

    public String WBoard() {
        return Board;
    }

    public String WBrand() {
        return Brand;
    }

    public String WtDate() {
        return Date;
    }

    public String WModel() {
        return Model;
    }

    public String WFmcToken() {
        return FmcToken;
    }

    public String WUid() {
        return Uid;
    }
}
