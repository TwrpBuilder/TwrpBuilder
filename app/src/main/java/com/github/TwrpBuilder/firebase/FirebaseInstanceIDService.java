package com.github.TwrpBuilder.firebase;

import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Nullable
    public static final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

}
