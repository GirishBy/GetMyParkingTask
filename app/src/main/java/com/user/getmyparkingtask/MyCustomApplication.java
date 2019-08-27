package com.user.getmyparkingtask;

import android.app.Application;
import android.util.Log;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyCustomApplication extends Application {
    public static final String TAG = "TEST";

    @Override
    public void onCreate() {
        super.onCreate();
        Map config = new HashMap();
        config.put("cloud_name", getResources().getString(R.string.cloud_name));
        MediaManager.init(this);
        Log.i(TAG, "onCreate: Application class");
    }
}
