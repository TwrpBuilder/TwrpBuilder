package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class Pbuild {

    private String Brand;
    private String Board;
    private String Model;
    private String CodeName;
    private String Email;
    private String Uid;
    private String FmcToken;
    private String Date;
    private String Url;
    private String DeveloperEmail;

    public Pbuild() {
    }

    public Pbuild(String Brand, String Board, String Model,String CodeName,String Email,String Uid,String FmcToken,String Date,String Url,String DeveloperEmail) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.CodeName=CodeName;
        this.Email = Email;
        this.Uid = Uid;
        this.FmcToken = FmcToken;
        this.Date= Date;
        this.Url=Url;
        this.DeveloperEmail=DeveloperEmail;
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

    public String getCodeName() {
        return CodeName;
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

    public String getDeveloperEmail() {
        return DeveloperEmail;
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

    public void setCodeName(String codeName) {
        CodeName = codeName;
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

    public void setDeveloperEmail(String developerEmail) {
        DeveloperEmail = developerEmail;
    }
}
