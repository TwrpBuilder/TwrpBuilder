package com.github.TwrpBuilder.app;

import android.os.Bundle;

import com.github.TwrpBuilder.model.Message;
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
    public static boolean finished;

    @Override
    protected void onStart() {
        super.onStart();
        bundle = getIntent().getExtras();
        Model = bundle.getString("Model");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("messages");
        message=new Message("Twrp Builder","Build is ready for "+ Model);
        databaseReference.push().setValue(message);
        finished=true;
        finish();
    }

    @Override
    protected void onDestroy() {
        finished=true;
        super.onDestroy();
    }
}
