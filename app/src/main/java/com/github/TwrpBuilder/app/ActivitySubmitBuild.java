package com.github.TwrpBuilder.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.DateUtils;
import com.github.TwrpBuilder.util.Pbuild;

import static com.github.TwrpBuilder.app.ActivityMessage.finished;

/**
 * Created by androidlover5842 on 20/1/18.
 */

public class ActivitySubmitBuild extends AppCompatActivity {

    private String Brand,Board,Model,Email,Fmc,Uid,key;
    private Bundle bundle;
    private EditText edGetUri;
    private Button btSubmit;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
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
        bundle = getIntent().getExtras();
        Brand = bundle.getString("Brand");
        Board = bundle.getString("Board");
        Model = bundle.getString("Model");
        Email = bundle.getString("Email");
        Fmc = bundle.getString("Fmc");
        Uid = bundle.getString("Uid");
        key = bundle.getString("somekey");


        edGetUri= findViewById(R.id.ed_url);
        btSubmit= findViewById(R.id.bt_submit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edGetUri.getText().toString().matches("")) {
                    Snackbar.make(getCurrentFocus(),"Please Enter Url",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (URLUtil.isValidUrl(edGetUri.getText().toString()))
                    {
                        System.out.println("Url " + edGetUri.getText());
                        pbuild = new Pbuild(Brand, Board, Model, Email, Uid, Fmc, DateUtils.getDate(), edGetUri.getText().toString(),firebaseAuth.getCurrentUser().getEmail());
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if (finished==true)
        {
            finish();
        }
    }
}
