package com.lmtri.sharespace;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by lmtri on 7/24/2017.
 */

public class ShareSpaceApplication extends Application {

    public static Bus BUS = new Bus(ThreadEnforcer.ANY);
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextSingleton.getInstance().initialize(this);
    }
}
