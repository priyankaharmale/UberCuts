package com.hnweb.ubercuts.fierbase;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by Priyanka on 16/06/2018.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SharedPreferences.Editor editor;
    String TYPE, userid, userrole;
    Intent intent;
    PendingIntent pendingIntent;
    MediaPlayer mMediaPlayer;
    JSONObject jobj;
    String order;
    Boolean LOGGED_IN;
    SharedPreferences sharedPreferences;
    private static final String PREFER_NAME_CUSTOMER = "PusheatCustomer";
    private static final String IS_USER_LOGIN = "userLoggedIn";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String CurrentString = remoteMessage.getData().toString();

        Log.d(TAG, "separated String a= : " + CurrentString);
        //sendNotification(remoteMessage.getData().toString());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String str) {

        String title = null, message = null;
        Log.d(TAG, "messageBody: " + str);

/*
        try {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(str)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)

                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //display multiple notifications in android Using below Code.
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            notificationManager.notify(m, notificationBuilder.build());
            //notificationManager.notify(0, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }*/
    }

}