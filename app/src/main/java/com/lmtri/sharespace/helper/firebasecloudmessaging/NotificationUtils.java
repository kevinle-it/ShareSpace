package com.lmtri.sharespace.helper.firebasecloudmessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.MainActivity;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;

/**
 * Created by lmtri on 12/26/2017.
 */

public class NotificationUtils {
    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_1 = "action_1";

    public static void displayNotification(Context context, String messageBody, AppointmentNotificationData data) {
//
//        Intent action1Intent = new Intent(context, NotificationActionService.class)
//                .setAction(ACTION_1);
//
//        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
//                action1Intent, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle("Sample Notification")
//                        .setContentText("Notification text goes here")
//                        .addAction(new NotificationCompat.Action(R.drawable.ic_launcher,
//                                "Action 1", action1PendingIntent));
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        Intent intent = new Intent(context, NotificationActionService.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Constants.ACTION_OPEN_APPOINTMENT_NOTIFICATION);

        Gson gson = new Gson();
        intent.putExtra(Constants.APPOINTMENT_NOTIFICATION_DATA, gson.toJson(data));

        PendingIntent resultIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Thông báo")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        public void onCreate() {
            super.onCreate();
            Log.d("Server", ">>>onCreate()");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, startId, startId);
            Log.i("LocalService", "Received start id " + startId + ": " + intent);

            return START_STICKY;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Intent startMainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            startMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getApplication().startActivity(startMainActivityIntent);

            String action = intent.getAction();
            if (Constants.ACTION_OPEN_APPOINTMENT_NOTIFICATION.equalsIgnoreCase(action)) {
                Gson gson = new Gson();
                try {
                    AppointmentNotificationData data = gson.fromJson(
                            intent.getStringExtra(Constants.APPOINTMENT_NOTIFICATION_DATA),
                            AppointmentNotificationData.class
                    );
                    if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.HOUSING_APPOINTMENT_TYPE)) {
                        ShareSpaceApplication.BUS.post(new OpenHousingAppointmentNotificationEvent(data));
                    } else if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.SHARE_HOUSING_APPOINTMENT_TYPE)) {
                        ShareSpaceApplication.BUS.post(new OpenShareHousingAppointmentNotificationEvent(data));;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }
}
