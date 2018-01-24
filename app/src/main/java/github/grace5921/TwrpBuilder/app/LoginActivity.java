package github.grace5921.TwrpBuilder.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import github.grace5921.TwrpBuilder.MainActivity;
import github.grace5921.TwrpBuilder.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView loginRegisterTitle;
    private Button btnSignup, btnLogin, btnReset, btnLogin2,btnSignUp,btnSignIn,btnCreateAccount;
    //private LinearLayout btn_login_singup_linear;
    private CardView btn_login_singup_linear, login_cardView;
    //private TextInputLayout TextInputLayoutPass;
    private ImageView TeamWinLoginLogo;
    private ArrayList<String> jsonArrayList;
    private JSONObject json_data;
    private JSONArray jsonArray;
    public static boolean name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            name = preferences.getBoolean("admin",false);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginRegisterTitle = findViewById(R.id.title_text);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);
        btnLogin2= findViewById(R.id.btn_login_2);
        btn_login_singup_linear=findViewById(R.id.btn_login_singup_linear);
        login_cardView = findViewById(R.id.login_cardview);
        //TextInputLayoutPass=(TextInputLayout)findViewById(R.id.text_input_layout_password);
        btn_login_singup_linear.setVisibility(View.VISIBLE);
        TeamWinLoginLogo= findViewById(R.id.teamwin_login_logo);
        btnSignUp = findViewById(R.id.sign_up_button);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnCreateAccount = findViewById(R.id.create_account_button);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setVisibility(View.VISIBLE);
                btn_login_singup_linear.setVisibility(View.GONE);
                login_cardView.setVisibility(View.VISIBLE);
                loginRegisterTitle.setText("Register to continue");
                inputEmail.setVisibility(View.VISIBLE);
                //TextInputLayoutPass.setVisibility(View.VISIBLE);
                inputPassword.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
                TeamWinLoginLogo.setVisibility(View.VISIBLE);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignIn.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.GONE);
                btnCreateAccount.setVisibility(View.VISIBLE);
                btnLogin2.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                loginRegisterTitle.setText("Login to continue");

            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreateAccount.setVisibility(View.GONE);
                btnLogin2.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.GONE);
                loginRegisterTitle.setText("Register to continue");
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(LoginActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login_singup_linear.setVisibility(View.GONE);
                btnLogin2.setVisibility(View.VISIBLE);
                inputEmail.setVisibility(View.VISIBLE);
                login_cardView.setVisibility(View.VISIBLE);
                //TextInputLayoutPass.setVisibility(View.VISIBLE);
                loginRegisterTitle.setText("Login to continue");
                inputPassword.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                TeamWinLoginLogo.setVisibility(View.VISIBLE);
                btnCreateAccount.setVisibility(View.VISIBLE);

            }
        });
        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    new CheckAdminTask().execute();
                                }
                            }
                        });
            }
        });
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        btn_login_singup_linear.setVisibility(View.VISIBLE);
        login_cardView.setVisibility(View.GONE);
        TeamWinLoginLogo.setVisibility(View.GONE);
        btnLogin2.setVisibility(View.GONE);
        inputEmail.setVisibility(View.GONE);
        //TextInputLayoutPass.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnCreateAccount.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    class CheckAdminTask extends AsyncTask<String, Void, Void> {

        @Override
        public Void doInBackground(String... params) {
            try {

                URL url = new URL("https://twrpbuilder.firebaseapp.com/app/admin.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line );
                }
                br.close();

                String json = responseOutput.toString();
                jsonArray=new JSONArray(json);
                jsonArrayList=new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++)
                {
                    json_data=jsonArray.getJSONObject(i);
                    String addr=json_data.getString("email");
                    jsonArrayList.add(addr);

                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("Email: "+auth.getCurrentUser().getEmail());
            for (int i=0;i<jsonArrayList.size();i++)
            {
                System.out.println(jsonArrayList.get(i));
                if (auth.getCurrentUser().getEmail().equals(jsonArrayList.get(i)))
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("admin",true);
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    name=true;
                    Toast.makeText(getBaseContext(),"Admin: true",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

