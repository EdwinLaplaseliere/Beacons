package com.example.beacons.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.CheckBoxPreference;
import android.view.Gravity;
import android.widget.Toast;

import static com.example.beacons.User.MainActivity.mPreferences;

public class appUtils {
    /**
     * This method is used to show Toast messages throughout the app
     * @param message
     * @param context
     */
    public void showToastMessage (String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }




}
