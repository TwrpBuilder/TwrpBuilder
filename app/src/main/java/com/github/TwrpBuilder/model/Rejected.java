package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 16.2.2018.
 */

public class Rejected {
    private String Brand;
    private String Board;
    private String Model;
    private String Email;
    private String Date;
    private String Note;
    private String Rejector;

    public Rejected(){}
    public Rejected(String Brand, String Board, String Model,String Email,String Date,String Note,String Rejector) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.Date= Date;
        this.Note=Note;
        this.Rejector=Rejector;
    }

    public void setBoard(String board) {
        Board = board;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setModel(String model) {
        Model = model;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setRejector(String rejector) {
        Rejector = rejector;
    }

    public String getBrand() {
        return Brand;
    }

    public String getBoard() {
        return Board;
    }

    public String getModel() {
        return Model;
    }

    public String getEmail() {
        return Email;
    }

    public String getDate() {
        return Date;
    }

    public String getNote() {
        return Note;
    }

    public String getRejector() {
        return Rejector;
    }
}
