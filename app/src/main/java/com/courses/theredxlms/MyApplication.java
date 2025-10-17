package com.courses.theredxlms;

import android.app.Application;
import com.google.firebase.FirebaseApp;

/**
 * Custom Application class for initializing services like Firebase
 * when the app first starts.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ADD THIS LINE TO INITIALIZE FIREBASE
        FirebaseApp.initializeApp(this);
    }
}
