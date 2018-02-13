package com.lmtri.sharespace.helper;

import android.content.Context;
import android.support.annotation.IntRange;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lmtri on 8/21/2017.
 */

public class ToastHelper {
    public static void showCenterToast(Context context, String message, @IntRange(from=0,to=1) int duration) {
        Toast toast = Toast.makeText(
                context,
                message,
                duration
        );
        TextView messageView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    public static void showCenterToast(Context context, int messageId, @IntRange(from=0,to=1) int duration) {
        Toast toast = Toast.makeText(
                context,
                messageId,
                duration
        );
        TextView messageView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setGravity(Gravity.CENTER);
        }
        toast.show();
    }
}
