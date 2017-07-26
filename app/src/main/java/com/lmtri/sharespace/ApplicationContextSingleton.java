package com.lmtri.sharespace;

import android.content.Context;
import android.util.Log;

/**
 * Created by lmtri on 7/24/2017.
 */

public class ApplicationContextSingleton {
    
    private static ApplicationContextSingleton mInstance;
    private Context context;

    public static ApplicationContextSingleton getInstance() {
        if (mInstance == null) {
            mInstance = getSync();
        }
        return mInstance;
    }

    private static synchronized ApplicationContextSingleton getSync() {
        if (mInstance == null) {
            mInstance = new ApplicationContextSingleton();
        }
        return mInstance;
    }

    public void initialize(Context context) {
        this.context = context;
    }

    public Context getApplicationContext() {
        return context;
    }
}
