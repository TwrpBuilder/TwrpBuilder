package com.github.TwrpBuilder.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.model.Pbuild;
import com.github.TwrpBuilder.util.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.github.TwrpBuilder.app.ActivityMessage.finished;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class ActivitySubmitBuild extends AppCompatActivity {

    @Nullable
    private String Brand,Board,Model,CodeName,Email,Fmc,Uid,key;
    private EditText edGetUri;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
    @Nullable
    private Pbuild pbuild;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submit_url);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        mUploader = mFirebaseInstance.getReference("Builds");
        Bundle bundle = getIntent().getExtras();
        Brand = bundle.getString("Brand");
        Board = bundle.getString("Board");
        Model = bundle.getString("Model");
        CodeName = bundle.getString("CodeName");
        Email = bundle.getString("Email");
        Fmc = bundle.getString("Fmc");
        Uid = bundle.getString("Uid");
        key = bundle.getString("somekey");


        edGetUri= findViewById(R.id.ed_url);
        Button btSubmit = findViewById(R.id.bt_submit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edGetUri.getText().toString().matches("")) {
                    Snackbar.make(getCurrentFocus(), R.string.enter_url,Snackbar.LENGTH_SHORT).show();
                }else {
                    if (URLUtil.isValidUrl(edGetUri.getText().toString()))
                    {
                        System.out.println("Url " + edGetUri.getText());
                        pbuild = new Pbuild(Brand, Board, Model,CodeName, Email, Uid, Fmc, DateUtils.getDate(), edGetUri.getText().toString(),firebaseAuth.getCurrentUser().getEmail());
                        mUploader.push().setValue(pbuild).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mFirebaseInstance.getReference("RunningBuild").child(key).removeValue();
                                startActivity(new Intent(ActivitySubmitBuild.this,ActivityMessage.class).putExtra("Model",Model));
                            }
                        });
                    }
                    else {
                        Snackbar.make(getCurrentFocus(),"Please Enter Valid Url",Snackbar.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (finished)
        {
            finish();
        }
    }
}
