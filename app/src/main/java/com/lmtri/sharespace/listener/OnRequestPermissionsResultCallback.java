package com.lmtri.sharespace.listener;

import android.support.annotation.NonNull;

/**
 * Created by lmtri on 8/11/2017.
 */

public interface OnRequestPermissionsResultCallback {
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
