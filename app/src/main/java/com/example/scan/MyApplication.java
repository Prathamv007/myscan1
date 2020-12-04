package com.example.scan;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by Singh on 13,November,2020
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
