package com.lmtri.sharespace.helper.firebasecloudmessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmtri.sharespace.R;
import com.lmtri.sharespace.ShareSpaceApplication;
import com.lmtri.sharespace.activity.MainActivity;
import com.lmtri.sharespace.api.model.AppointmentNotificationData;
import com.lmtri.sharespace.api.model.HousingAppointment;
import com.lmtri.sharespace.api.model.ShareHousingAppointment;
import com.lmtri.sharespace.api.model.User;
import com.lmtri.sharespace.api.service.user.IGetOtherUserInfoCallback;
import com.lmtri.sharespace.api.service.user.UserClient;
import com.lmtri.sharespace.helper.Constants;
import com.lmtri.sharespace.helper.busevent.appointment.housing.OpenHousingAppointmentNotificationEvent;
import com.lmtri.sharespace.helper.busevent.appointment.sharehousing.OpenShareHousingAppointmentNotificationEvent;

/**
 * Created by lmtri on 12/4/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName(); // "MyAndroidFCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Process Display Message.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//            createNotification(remoteMessage.getNotification().getBody());
//        }
        // Process Data Message.
        if (remoteMessage.getData() != null) {
            final String housingOrShareHousingType = remoteMessage.getData().get("HousingOrShareHousingType");
            final int appointmentType = Integer.parseInt(remoteMessage.getData().get("AppointmentType"));
            final int housingOrShareHousingID = Integer.parseInt(remoteMessage.getData().get("HousingOrShareHousingID"));
            final int senderID = Integer.parseInt(remoteMessage.getData().get("SenderID"));
            final int ownerOrCreatorID = Integer.parseInt(remoteMessage.getData().get("OwnerOrCreatorID"));
            if (Constants.CURRENT_USER.getUserID() == senderID
                    || Constants.CURRENT_USER.getUserID() == ownerOrCreatorID) {
                if (Constants.CURRENT_USER.getUserID() == senderID) {
                    UserClient.getOtherUserInfo(
                            ownerOrCreatorID,
                            new IGetOtherUserInfoCallback() {
                                @Override
                                public void onGetComplete(User otherUser) {
                                    if (otherUser != null) {
                                        AppointmentNotificationData data = new AppointmentNotificationData(
                                                housingOrShareHousingType,
                                                appointmentType,
                                                housingOrShareHousingID,
                                                senderID,
                                                ownerOrCreatorID
                                        );
                                        String body;
                                        if (!TextUtils.isEmpty(otherUser.getLastName())) {
                                            body = otherUser.getLastName() + " " + otherUser.getFirstName();
                                        } else {
                                            body = otherUser.getFirstName();
                                        }
                                        if (appointmentType == Constants.SET_NEW_APPOINTMENT_NOTIFICATION) {
                                            body += " đã gửi bạn yêu cầu lịch hẹn mới. Vui lòng xác nhận.";
                                        } else if (appointmentType == Constants.UPDATE_APPOINTMENT_NOTIFICATION) {
                                            body += " đã cập nhật lịch hẹn của bạn. Vui lòng xác nhận.";
                                        } else if (appointmentType == Constants.ACCEPT_APPOINTMENT_NOTIFICATION) {
                                            body += " đã đồng ý lịch hẹn của bạn.";
                                        } else if (appointmentType == Constants.DELETE_APPOINTMENT_NOTIFICATION) {
                                            body += " đã từ chối/xóa lịch hẹn của bạn.";
                                        }
                                        createNotification(body, data);
                                    }
                                }

                                @Override
                                public void onGetFailure(Throwable t) {

                                }
                            }
                    );
                } else if (Constants.CURRENT_USER.getUserID() == ownerOrCreatorID) {
                    UserClient.getOtherUserInfo(
                            senderID,
                            new IGetOtherUserInfoCallback() {
                                @Override
                                public void onGetComplete(User otherUser) {
                                    if (otherUser != null) {
                                        AppointmentNotificationData data = new AppointmentNotificationData(
                                                housingOrShareHousingType,
                                                appointmentType,
                                                housingOrShareHousingID,
                                                senderID,
                                                ownerOrCreatorID
                                        );
                                        String body;
                                        if (!TextUtils.isEmpty(otherUser.getLastName())) {
                                            body = otherUser.getLastName() + " " + otherUser.getFirstName();
                                        } else {
                                            body = otherUser.getFirstName();
                                        }
                                        if (appointmentType == Constants.SET_NEW_APPOINTMENT_NOTIFICATION) {
                                            body += " đã gửi bạn yêu cầu lịch hẹn mới. Vui lòng xác nhận.";
                                        } else if (appointmentType == Constants.UPDATE_APPOINTMENT_NOTIFICATION) {
                                            body += " đã cập nhật lịch hẹn của bạn. Vui lòng xác nhận.";
                                        } else if (appointmentType == Constants.ACCEPT_APPOINTMENT_NOTIFICATION) {
                                            body += " đã đồng ý lịch hẹn của bạn.";
                                        } else if (appointmentType == Constants.DELETE_APPOINTMENT_NOTIFICATION) {
                                            body += " đã từ chối/xóa lịch hẹn của bạn.";
                                        }
                                        createNotification(body, data);
                                    }
                                }

                                @Override
                                public void onGetFailure(Throwable t) {

                                }
                            }
                    );
                }
            }
        }
    }

    private void createNotification(String messageBody, AppointmentNotificationData data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(Constants.ACTION_OPEN_APPOINTMENT_NOTIFICATION);

        Gson gson = new Gson();
        intent.putExtra(Constants.APPOINTMENT_NOTIFICATION_DATA, gson.toJson(data));

        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Thông báo")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }

//    public static class NotificationActionService extends IntentService {
//        public NotificationActionService() {
//            super(NotificationActionService.class.getSimpleName());
//        }
//
//        public void onCreate() {
//            super.onCreate();
//            Log.d("Server", ">>>onCreate()");
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            super.onStartCommand(intent, startId, startId);
//            Log.i("LocalService", "Received start id " + startId + ": " + intent);
//
//            return START_STICKY;
//        }
//
//        @Override
//        protected void onHandleIntent(Intent intent) {
//
//            Intent startMainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
//            startMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            getApplication().startActivity(startMainActivityIntent);
//
//            String action = intent.getAction();
//            if (ACTION_OPEN_APPOINTMENT_NOTIFICATION.equalsIgnoreCase(action)) {
//                Gson gson = new Gson();
//                try {
//                    AppointmentNotificationData data = gson.fromJson(
//                            intent.getStringExtra(Constants.APPOINTMENT_NOTIFICATION_DATA),
//                            AppointmentNotificationData.class
//                    );
//                    if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.HOUSING_APPOINTMENT_TYPE)) {
//                        ShareSpaceApplication.BUS.post(new OpenHousingAppointmentNotificationEvent(data));
//                    } else if (data.getHousingOrShareHousingType().equalsIgnoreCase(Constants.SHARE_HOUSING_APPOINTMENT_TYPE)) {
//                        ShareSpaceApplication.BUS.post(new OpenShareHousingAppointmentNotificationEvent(data));;
//                    }
//                } catch (JsonSyntaxException e) {
//                    e.printStackTrace();
//                }
//                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
//            }
//        }
//    }
}
