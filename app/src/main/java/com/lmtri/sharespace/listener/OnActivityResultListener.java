package com.lmtri.sharespace.listener;

import android.content.Intent;

/**
 * Created by lmtri on 8/12/2017.
 */

public interface OnActivityResultListener {
    boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
