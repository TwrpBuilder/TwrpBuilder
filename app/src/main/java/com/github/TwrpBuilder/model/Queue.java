package com.github.TwrpBuilder.model;

/**
 * Created by androidlover5842 on 18/1/18.
 */

public class Queue {
    private String Brand;
    private String Board;
    private String Model;
    private String Email;
    private String Date;

    public Queue(){}
    public Queue(String Brand, String Board, String Model,String Email,String Date) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.Date= Date;
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
}
