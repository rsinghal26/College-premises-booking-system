package com.example.nimishgupta.mycollege;

import android.app.Application;

import com.firebase.client.Firebase;

public class LNMIIT extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
