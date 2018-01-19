package github.grace5921.TwrpBuilder.util;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class Pbuild {

    public String Brand;
    public String Board;
    public String Model;
    public  String Email;
    public String Uid;
    public String FmcToken;
    public String Date;
    public String Url;

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

    public String WEmail() {
        return Email;
    }

    public String WBoard() {
        return Board;
    }

    public String WBrand() {
        return Brand;
    }

    public String WDate() {
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

    public String WUrl() {
        return Url;
    }
}
