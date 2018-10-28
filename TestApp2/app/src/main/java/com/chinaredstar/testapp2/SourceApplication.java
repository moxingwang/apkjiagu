package com.chinaredstar.testapp2;

import android.app.Application;
import android.util.Log;


public class SourceApplication extends Application {
    private static final String TAG = SourceApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "-------------onCreate");
    }
}
