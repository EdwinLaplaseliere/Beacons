package com.example.beacons.Notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {

    // setting constant for channel 1
    public static final String CHANNEL_1_ID="channel1";


    /**
     * On create we set the notification channel
     */
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    /**
     * More than one channe can be created, in this case we only created
     * one but we can create more according to the needs
     */
    public void createNotificationChannels(){

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            NotificationChannel channel1= new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channe 1");


    // NotificationManager is gonna allow us to show notifications when a beacon is found in a range
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }


    }


}
