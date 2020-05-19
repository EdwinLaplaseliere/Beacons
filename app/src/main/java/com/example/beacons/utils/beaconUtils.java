package com.example.beacons.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class beaconUtils {

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * This method return a boolean value to check if a Beacon is EddystoneLayoutUID or not
     *
     * @param context
     * @return
     */
    public static boolean isEddystoneLayoutUID(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_uid", true);
    }

    /**
     * his method return a boolean value to check if a Beacon is EddystoneLayoutURL or not
     *
     * @param context
     * @return
     */

    public static boolean isEddystoneLayoutURL(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_url", true);
    }

    /**
     * his method return a boolean value to check if a Beacon is EddystoneLayoutTLM or not
     *
     * @param context
     * @return
     */
    public static boolean isEddystoneLayoutTLM(Context context) {
        return getSharedPreferences(context).getBoolean("scan_parser_layout_eddystone_tlm", false);
    }

    /**
     * iniiBeaconManagaer contains the parsers of 3 types of Beacons layout which are:
     * EddystoneLayoutUID
     * EddystoneLayoutURL
     * EddystoneLayoutTLM
     *
     * @param mBeaconManager
     * @param context
     */
    public void initBeaconManager(BeaconManager mBeaconManager, Context context) {

        if (isEddystoneLayoutUID(context)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        }
        if (isEddystoneLayoutURL(context)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        }
        if (isEddystoneLayoutTLM(context)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        }
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));


        /**
         * These are the 3 types of layouts for this project
         */
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconManager.setAndroidLScanningDisabled(true);
        mBeaconManager.setBackgroundScanPeriod(15000L);
        mBeaconManager.setForegroundBetweenScanPeriod(0L);
        mBeaconManager.setForegroundScanPeriod(1100L);

    }

    /**
     * This method returns a region name
     *
     * @param context
     * @param DEFAULT_PROJECT_NAME
     * @return
     */
    public String getDefaultRegionName(Context context, String DEFAULT_PROJECT_NAME) {
        return getSharedPreferences(context).getString("scan_default_region_text", DEFAULT_PROJECT_NAME);
    }


    /**
     * This method checks if the location is enabled or not
     *
     * @param TAG
     * @param context
     * @return
     */
    public boolean isLocationEnabled(String TAG, Context context) {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean networkLocationEnabled = false;
        boolean gpsLocationEnabled = false;

        try {
            networkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gpsLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
            Log.d(TAG, "Excepción al obtener información de localización");
        }

        return networkLocationEnabled || gpsLocationEnabled;
    }


    /**
     * This method is to compare the times for the deals. So if a deal is set to an specific time,
     * then it will only show it if the time matches to the current time
     * it takes 3 parametersn to compare 3 times
     *
     * @param time
     * @param endtime
     * @param requiringTime
     * @return
     * @throws ParseException
     */
    public Boolean compareTimes(String time, String endtime, String requiringTime) throws ParseException {

        String TimePattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(TimePattern);

        Date time1 = sdf.parse(time);
        Date time2 = sdf.parse(endtime);
        Date time3 = sdf.parse(requiringTime);

        if (time3.before(time2) && time3.after(time1)) {

            return true;
        }


        return false;
    }

    /**
     * This method gets the current time to be compared with the times of the deal
     *
     * @return
     */
    public String getCurrentTime() {

        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return time.format(date);

    }





}
