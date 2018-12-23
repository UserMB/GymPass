package com.example.jakubkurtiak.gympass;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// -----------------------------------------
// Firebase push notifications.
// -----------------------------------------

// class that extends FirebaseInstanceIdService to receive registration tokens from Firebase
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";

    //Override method onTokenRefresh  To get the most recent version of registration token
    //Method called each time you get a new token
    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token); //creates log where we can obtain reg token
    }


}
