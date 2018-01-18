package github.grace5921.TwrpBuilder.util;

/**
 * Created by androidlover5842 on 18/1/18.
 */

public class Queue {
    public String Brand;
    public String Board;
    public String Model;
    public  String Email;
    public String Date;

    Queue(){

    }

    public Queue(String Brand, String Board, String Model,String Email,String Date) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.Date= Date;
    }

    public String MBrand() {
        return Brand;
    }

    public String MBoard() {
        return Board;
    }

    public String MModel() {
        return Model;
    }

    public String MEmail() {
        return Email;
    }

    public String MDate() {
        return Date;
    }
}
