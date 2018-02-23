package com.github.TwrpBuilder.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.util.setLocale;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;;

import com.github.TwrpBuilder.MainActivity;
import com.github.TwrpBuilder.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView loginRegisterTitle;
    private Button btnSignup, btnLogin, btnReset, btnLogin2,btnSignUp,btnSignIn,btnCreateAccount;
    private LinearLayout developerTag;
    private CardView btn_login_singup_linear, login_cardView;
    private ImageView TeamWinLoginLogo;
    private SignInButton gSignInButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        lang=PreferenceManager.getDefaultSharedPreferences(this).getString("lang", "");
        if (auth.getCurrentUser() != null) {
            if (!lang.equals(""))
            {
                new setLocale(getBaseContext(),lang);
            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

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
        btn_login_singup_linear.setVisibility(View.VISIBLE);
        TeamWinLoginLogo= findViewById(R.id.teamwin_login_logo);
        btnSignUp = findViewById(R.id.sign_up_button);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnCreateAccount = findViewById(R.id.create_account_button);
        developerTag = findViewById(R.id.developer_tag);
        gSignInButton=findViewById(R.id.google_signIn);

        auth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setVisibility(View.VISIBLE);
                btn_login_singup_linear.setVisibility(View.GONE);
                login_cardView.setVisibility(View.VISIBLE);
                loginRegisterTitle.setText("Register to continue");
                inputEmail.setVisibility(View.VISIBLE);
                inputPassword.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
                TeamWinLoginLogo.setVisibility(View.VISIBLE);
                developerTag.setVisibility(View.GONE);
                doubleBackToExitPressedOnce = false;
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
                doubleBackToExitPressedOnce = false;

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
                doubleBackToExitPressedOnce = false;
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
                developerTag.setVisibility(View.GONE);
                btnLogin2.setVisibility(View.VISIBLE);
                inputEmail.setVisibility(View.VISIBLE);
                login_cardView.setVisibility(View.VISIBLE);
                loginRegisterTitle.setText("Login to continue");
                inputPassword.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                TeamWinLoginLogo.setVisibility(View.VISIBLE);
                btnCreateAccount.setVisibility(View.VISIBLE);
                doubleBackToExitPressedOnce = false;

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

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

        gSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
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
        developerTag.setVisibility(View.VISIBLE);
        login_cardView.setVisibility(View.GONE);
        TeamWinLoginLogo.setVisibility(View.GONE);
        btnLogin2.setVisibility(View.GONE);
        inputEmail.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnCreateAccount.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.GONE);
        this.doubleBackToExitPressedOnce = true;
        inputEmail.setText("");
        inputPassword.setText("");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();

                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(getCurrentFocus(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

