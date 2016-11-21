package github.grace5921.TwrpBuilder.util;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 */

@IgnoreExtraProperties
public class User {

    public String Brand;
    public String Board;
    public String Model;
    public String Email;
    public String FmcToken;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String Brand, String Board, String Model,String Email,String FmcToken) {
        this.Brand = Brand;
        this.Board = Board;
        this.Model = Model;
        this.Email = Email;
        this.FmcToken = FmcToken;
    }
}
