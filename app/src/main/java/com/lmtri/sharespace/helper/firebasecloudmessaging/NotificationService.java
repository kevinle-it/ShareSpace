package com.lmtri.sharespace.helper.firebasecloudmessaging;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.MainActivity;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;

/**
 * Created by lmtri on 12/26/2017.
 */

public class NotificationService extends IntentService {
    public NotificationService() {
        super(NotificationService.class.getSimpleName());
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
