package com.example.beacons.Notifications;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {


    /**
     * Notification receiver makes sure the notification is show to the user
     * ToastMessage is the key for the notification to be shown.
     * A toast message can be display if we want it. In this case is defined but never used. We only defined
     * it for later usage
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String message=intent.getStringExtra("ToastMessage");
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
    }
}
