package github.grace5921.TwrpBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import github.grace5921.TwrpBuilder.config.Config;

public class CheckSu extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_check_su);

        if(Config.suAvailable()) {
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(CheckSu.this, MainActivity.class);
                    CheckSu.this.startActivity(mainIntent);
                    CheckSu.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }else
        {
                        finish();
                        moveTaskToBack(true);
                        Toast.makeText(getApplicationContext(), "Device is not rooted or not supported :( .", Toast.LENGTH_SHORT).show();

        }
    }
}