package com.mridx.pubcash.handlers;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppController extends Application {
    private static final String TAG = "AppController";

    public static final String CHENNEL_1_ID = "Channel1";
    public static final String CHENNEL_2_ID = "Channel2";

    private static AppController instance;

    public static AppController getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //createNotificationChennel();
    }

    /*

    private void createNotificationChennel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHENNEL_1_ID,
                    "Chennel1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Chennel 1");

            NotificationChannel channel2 = new NotificationChannel(CHENNEL_2_ID,
                    "Chennel2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Chennel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }
    }
    */
    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}


