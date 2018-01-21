package github.grace5921.TwrpBuilder.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import github.grace5921.TwrpBuilder.R;

/**
 * Created by sumit on 19/11/16.
 */

public class SettingsActivity extends AppCompatActivity {
    private Button DeleteUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @NonNull
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DeleteUser= findViewById(R.id.delete_account);

        DeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("User Account: ", "User account deleted.");
                                }
                            }
                        });
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class)); //Go back to home page
                finish();

            }
        });

    }
}
