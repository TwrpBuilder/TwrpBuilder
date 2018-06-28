package com.github.TwrpBuilder.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by androidlover5842 on 24/1/18.
 */

public class FirebaseDBInstance {
    private static FirebaseDatabase mData;

    public static void getDatabase() {
        if (mData == null) {

            mData = FirebaseDatabase.getInstance();
            mData.setPersistenceEnabled(true);
        }
    }

}
