package com.lmtri.sharespace;

import android.app.Application;
import android.util.Log;

/**
 * Created by lmtri on 7/24/2017.
 */

public class ShareSpaceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextSingleton.getInstance().initialize(this);
    }
}
