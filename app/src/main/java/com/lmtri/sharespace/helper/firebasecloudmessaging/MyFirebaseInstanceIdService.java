package com.lmtri.sharespace.helper.firebasecloudmessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by lmtri on 12/4/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}
