package com.mridx.pubcash.handlers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mridx.pubcash.R;

import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class PUBCashService extends FirebaseMessagingService {

    private NotificationManager notificationManager;
    private static final String MAINCHANNEL_ID = "01";
    private long[] pattern = {0,1000,0};

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"), remoteMessage/*,remoteMessage.getData().get("click_action")*/);

        String messageTitle = remoteMessage.getData().get("title");
        String messageBody = remoteMessage.getData().get("body");
        String action = remoteMessage.getData().get("action");
        Intent intent = new Intent(this, NotificationHandler.class);
        intent.putExtra("action", action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationID = new Random().nextInt(10);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.RED,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
                //.addAction(R.mipmap.ic_launcher_round, "Open Now", pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build());

    }

    private void sendNotification(String messageTitle, String messageBody,/*, String clickAction*/RemoteMessage remoteMessage) {

        String action = remoteMessage.getData().get("action");
        Intent intent = new Intent(this, NotificationHandler.class);
        intent.putExtra("action", action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationID = new Random().nextInt(10);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.RED,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        //CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        //String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(MAINCHANNEL_ID, "MAIN_CHANNEL", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("This is main channel for notification");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
