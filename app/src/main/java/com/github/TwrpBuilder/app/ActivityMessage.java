package com.github.TwrpBuilder.app;

import android.app.*;
import android.os.Bundle;

import com.github.TwrpBuilder.util.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by androidlover5842 on 27/1/18.
 */

public class ActivityMessage extends android.app.Activity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Message message;
    private Bundle bundle;
    private String Model;

    @Override
    protected void onStart() {
        bundle = getIntent().getExtras();
        Model = bundle.getString("Model");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("messages");
        message=new Message("Build is ready for "+ Model);
        databaseReference.push().setValue(message);
        finish();
        super.onStart();
    }
}
