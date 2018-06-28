package com.github.TwrpBuilder.app;

import android.os.Bundle;

import com.github.TwrpBuilder.model.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by androidlover5842 on 27/1/18.
 */

public class ActivityMessage extends android.app.Activity {
    public static boolean finished;

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        String model = bundle.getString("Model");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("messages");
        Message message = new Message("Twrp Builder", "Build is ready for " + model);
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
