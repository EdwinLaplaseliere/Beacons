package com.example.beacons.User;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beacons.Admin.session.Login;
import com.example.beacons.Fragments.settingfragment;
import com.example.beacons.Notifications.NotificationReceiver;
import com.example.beacons.R;
import com.example.beacons.utils.appUtils;
import com.example.beacons.utils.beaconUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import static com.example.beacons.Notifications.Notifications.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BeaconConsumer,
        RangeNotifier, NavigationView.OnNavigationItemSelectedListener {


    public static ArrayList<String> mPreferences = new ArrayList<String>();

    settingfragment st= new settingfragment();

    // this instace starts the notification process
    private NotificationManagerCompat notificationManager;

    boolean isScanning = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // This variable is gonna be the reference of the Collection that holds the Beacons in the database
    private CollectionReference Beaconref = db.collection("Beacons");

    // beaconsUtil is a class for usefull methods related to beacons
    beaconUtils mBeaconUtils = new beaconUtils();
    // an instance to the app utils class in which some utils methods are
    appUtils mAppUtils = new appUtils();

    FirebaseFirestore ffstore;

    // a constant with the project name
    public static final String DEFAULT_PROJECT_NAME = "AdvConnProject";
    protected final String TAG = MainActivity.this.getClass().getSimpleName();
    ;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    // this is the period the app is gonna scan to find beacons at the time
    private static final long SCAN_PERIOD = 6000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    public static final int REQ_GLOBAL_SETTING = 10078;

    // this 2 instances of the classes BeaconManager and Region, make the Beacon location possible
    private BeaconManager mBeaconManager;
    private Region mRegion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // notifications are gonna be shown here, in this activity
        notificationManager = NotificationManagerCompat.from(this);

        getStarStop().setOnClickListener(this);

        // an instance of beacon manager which will allow the app to find the beacons
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // initBeaconManager is a method which holds the types of beacons the app is able to searcj for
        mBeaconUtils.initBeaconManager(mBeaconManager, this);
        // this determines the region in which the beacons are being found
        mRegion = new Region(mBeaconUtils.getDefaultRegionName(getApplicationContext(), DEFAULT_PROJECT_NAME), null, null, null);

        ffstore = FirebaseFirestore.getInstance();

        // this is for the menu on the left hand side
        //the drawer contains all the options such as login, help and so on
        final DrawerLayout drawerlayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                drawerlayout.openDrawer(GravityCompat.START);

            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

    }


    /**
     * this starts the searching of beacons
     * @param view
     */
    @Override
    public void onClick(View view) {

        if (view.equals(findViewById(R.id.start))) {
            scanStartStopAction();
        }
    }

    /**
     * This method comes from a superclass Beacon Consumer and it starts ranging the beacons calling the
     * startRangingBeaconsInRegion method
     */
    @Override
    public void onBeaconServiceConnect() {

        try {
            // starting to search for beacons in a specific region
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            // a message just for testing purposes
            mAppUtils.showToastMessage(getString(R.string.start_looking_for_beacons), this);

        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al empezar a buscar beacons " + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);

    }


    /**
     * This method takes a collection which could ne empty or not and a Region
     * in which the beacons are being searched
     *
     * @param beacons
     * @param region
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (!(beacons.size() == 0)) {


            for (final Beacon beacon : beacons) {

                // if beacons are found, we need to go through some validations to see if the beacons exists
                // in the database, and what deals it should display or if it should display a deal according to the time of the day
                CollectionReference notebookRef = db.collection("Beacons");

                notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        // the beacon gets checked to see if it exists in the database
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.get("BeaconId").toString().equals(beacon.getId3().toString())) {
                                String compIdFound = documentSnapshot.get("compId").toString();

                                compare(compIdFound);


                            }


                        }


                    }
                });


            }
        }
    }

    /**
     * this is for the bluetooth permissions
     */

    @SuppressLint("MissingPermission")
    private void prepareDetection() {

        if (!mBeaconUtils.isLocationEnabled(TAG, this)) {

            locationRequest();

        } else { // Localización activada, comprobemos el bluetooth

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {

                mAppUtils.showToastMessage(getString(R.string.not_support_bluetooth_msg), this);

            } else if (mBluetoothAdapter.isEnabled()) {

                startDetectingBeacons();

            } else {

                // asking the user to activate the bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            // The user has activated the bluetooth
            if (resultCode == RESULT_OK) {

                startDetectingBeacons();

            } else if (resultCode == RESULT_CANCELED) { // User refuses to enable bluetooth

                mAppUtils.showToastMessage(getString(R.string.no_bluetooth_msg), this);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // here is where the beacon start getting detected
    private void startDetectingBeacons() {

        //a scan period is set
        mBeaconManager.setForegroundScanPeriod(SCAN_PERIOD);

        // Enlazar al servicio de beacons. Obtiene un callback cuando esté listo para ser usado
        mBeaconManager.bind(this);


    }


    /**
     * this method gets executed when the startStop button gets pressed after scanning
     */
    private void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            mAppUtils.showToastMessage(getString(R.string.stop_looking_for_beacons), this);
        } catch (RemoteException e) {
            Log.d(TAG, "an exception has occurred  " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();

        // unlinked the beacon service
        mBeaconManager.unbind(this);


    }

    // this method will ask the user for location permissions. This is in case of a radar
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void askForLocationPermissions() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_access_message);
        builder.setMessage(R.string.grant_location_access);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareDetection();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.funcionality_limited);
                    builder.setMessage(getString(R.string.location_settings) +
                            getString(R.string.cannot_discover_beacons));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    /**
     * Locations request so the app knows where the beacons are with the radar
     */
    private void locationRequest() {

        // ask the user
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.location_disabled);
        dialog.setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                 TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }


    // this is for the notifications
    // a notification is sent through channel 1
    // it gets few deatils such as Deal, a message, in this case is the price,
    // an url which will redirect the user to a website the admin wants
    public void sendOnChannel1(String Deal, String message, String url) {
        int newid=0;
        Intent newintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        PendingIntent content = PendingIntent.getActivity(this, 0, newintent, 0);

        Intent broadcast = new Intent(this, NotificationReceiver.class);
        broadcast.putExtra("ToastMessage", message);
        PendingIntent action = PendingIntent.getBroadcast(this,
                0, broadcast, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.offbeat);

        // the notifications are built here
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(Deal)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText("Summary Text"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.GREEN)
                .setContentIntent(content)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
//                .setVisibility(1)
                .build();


        notificationManager.notify(newid, notification);
newid++;

    }


    public void scanStartStopAction() {
        if (isScanning) {

            stopDetectingBeacons();

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Desactivar bluetooth
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }

            isScanning = false;
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // if locations permission have not been set, the app requests them
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    askForLocationPermissions();

                } else { // location permission granted

                    prepareDetection();
                }

            } else { // versions of android < 6

                prepareDetection();
            }

            isScanning = true;
        }
    }


    private ImageView getStarStop() {
        return (ImageView) findViewById(R.id.start);

    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

 // this will just the user back to the main activity
            case R.id.Home:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivityForResult(intent2, REQ_GLOBAL_SETTING);
                break;
 // this will get the user to log in
            case R.id.session:
                Intent intent3 = new Intent(this, Login.class);
                startActivityForResult(intent3, REQ_GLOBAL_SETTING);
                break;
// This will send the user to set his preferences
            case R.id.settings:
                Intent intent4 = new Intent(this, settings.class);
                startActivityForResult(intent4, REQ_GLOBAL_SETTING);
                break;
// this will redirect the user to the help activity
            case R.id.help:
                Intent intent5 = new Intent(this, Help.class);
                startActivityForResult(intent5, REQ_GLOBAL_SETTING);
                break;

        }

        return true;
    }


    // this is for the deals, after the beacons are found,
    // the preferences of the user get checked and compared with the category of the beacon

    public void compare(final String id) {

        final DocumentReference docref = ffstore.collection("Companies").document(id);
        docref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (!mPreferences.isEmpty()) {
                    for (String s : mPreferences) {
                        if (s.equals(documentSnapshot.getString("Category"))) {

                            getRightDeal(id, 1);

                        }
                    }
                } else {

                    Toast.makeText(MainActivity.this, "Please set your preferences", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    /**
     * this method allows the system to get the right deal, according to the time in the day
     * @param companyId
     */
    public void getRightDeal(final String companyId, final int id) {


        CollectionReference notebookRef = db.collection("Deals");

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (companyId.equals(documentSnapshot.getString("compId"))) {
                        String timesToShowDeal = documentSnapshot.getString("Time");


                        String[] getTwoTimes = timesToShowDeal.split(" to ");
                        String time1 = getTwoTimes[0];
                        String time2 = getTwoTimes[1];

                        try {
                            if (mBeaconUtils.compareTimes(time1, time2, mBeaconUtils.getCurrentTime())) {



       sendOnChannel1(documentSnapshot.getString("Deal"), documentSnapshot.getString("Price_Discount"), documentSnapshot.getString("Deal_Link"));

                            } else {
                                Log.d("found", "time over");

                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }


                    }


                }

            }
        });


    }
}
