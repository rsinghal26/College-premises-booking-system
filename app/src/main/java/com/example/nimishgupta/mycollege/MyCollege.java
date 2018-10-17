package com.example.nimishgupta.mycollege;

import android.app.Application;

import com.firebase.client.Firebase;

public class MyCollege extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
