package com.example.beacons.Admin.Options;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beacons.Admin.session.ManagerDashBoard;
import com.example.beacons.R;
import com.example.beacons.utils.appUtils;
import com.example.beacons.utils.beaconUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity adds beacons to the database. It works kind of the same as
 * the main activity which scans beacons in a range.
 * In this case we are setting the range to be less than 20 centimetres which
 * limits the Business owner to only add beacons close to him.
 * This guaranties that the user will only add his own beacons to the database
 * and no other. He needs to have the beacon as close as 20 cms
 */
public class addBeacon extends AppCompatActivity implements View.OnClickListener, BeaconConsumer,
        RangeNotifier {

    beaconUtils mBeaconUtils = new beaconUtils();
    appUtils mAppUtils = new appUtils();

    public final String BEACON_ID="BeaconId";
    public final String COMPANY_ID="compId";


    public static final String DEFAULT_PROJECT_NAME = "AdvConnProject";
    protected final String TAG = addBeacon.this.getClass().getSimpleName();;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final long SCAN_PERIOD = 6000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    public static final int REQ_GLOBAL_SETTING = 10078;

    // this 2 instances of the classes BeaconManager and Region, make the Beacon location possible
    private BeaconManager mBeaconManager;
    private Region mRegion;

    boolean isScanning=false;

    EditText beaconId;
    FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon);
        getSearchButton().setOnClickListener(this);

        // variable s such as Beacon manager and region are created as well as the main activity
        // it is going to work like a scanner as the beacon needs to be close to the devise scanning
        mBeaconManager = BeaconManager.getInstanceForApplication(this);

        mBeaconUtils.initBeaconManager(mBeaconManager,this);

        mRegion = new Region( mBeaconUtils.getDefaultRegionName(getApplicationContext(),DEFAULT_PROJECT_NAME),null, null, null);

        beaconId=findViewById(R.id.Beaconid);
        fAuth = FirebaseAuth.getInstance();

    }


    /**
     * Geeting the button to search for the beacon. In this case it is gonna be a bluetooth image
     * @return
     */
    public ImageView getSearchButton(){

        return (ImageView) findViewById(R.id.search_beacon);

    }

    /**
     * this method will be called when the user clicks the save button
     * this will allow the app to save the beacon in the database
     * @param v
     */
    public void saveBeacon(View v){
        final String Beacon_id_found=beaconId.getText().toString().trim();
        userId=fAuth.getCurrentUser().getUid();

        final Map<String, Object> Beacon = new HashMap<>();

        Beacon.put(BEACON_ID, Beacon_id_found);
        Beacon.put(COMPANY_ID,userId);



        // the beacon will be saved in the Beacons collection int the database
        DocumentReference docRef=db.collection("Beacons").document();

        docRef.set(Beacon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        stopDetectingBeacons();
                        Toast.makeText(addBeacon.this, "Your Beacon was successfully registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addBeacon.this, "something unexpected happened, please try again later", Toast.LENGTH_SHORT).show();

                    }
                });


    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.equals(findViewById(R.id.search_beacon))){
            scanStartStopAction();

        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // starting to search for beacons in a specific region
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            // a message just for testing purposes
            mAppUtils.showToastMessage(getString(R.string.start_looking_for_beacons),this);

        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al empezar a buscar beacons " + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);

    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (beacons.size() == 0) {
            mAppUtils.showToastMessage(getString(R.string.no_beacons_detected),this);
        }
        int id=1;
        for (Beacon beacon : beacons) {
           double beacon_distance= beacon.getDistance();

            if(beacon_distance>2){
                mAppUtils.showToastMessage("You are probably too far from your Beacon. \t " +
                        "try to get as close as 20 cms from your beacon and wait 30 so the distance is updated",this);

            }else{

                beaconId.setText(String.valueOf(beacon.getId3()));

            }


        }





    }

    public void scanStartStopAction() {
        if (isScanning) {


            stopDetectingBeacons();

            beaconId.setText("");

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Desactivar bluetooth
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }

            isScanning=false;
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Si los permisos de localización todavía no se han concedido, solicitarlos
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    askForLocationPermissions();

                } else { // Permisos de localización concedidos

                    prepareDetection();
                }

            } else { // Versiones de Android < 6

                prepareDetection();
            }

            isScanning=true;
        }
    }

    private void startDetectingBeacons() {

        // Fijar un periodo de escaneo
        mBeaconManager.setForegroundScanPeriod(SCAN_PERIOD);

        // Enlazar al servicio de beacons. Obtiene un callback cuando esté listo para ser usado
        mBeaconManager.bind(this);


    }

    private void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            mAppUtils.showToastMessage(getString(R.string.stop_looking_for_beacons),this);
        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al parar de buscar beacons " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();

        // Desenlazar servicio de beacons
        mBeaconManager.unbind(this);



    }

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
    @SuppressLint("MissingPermission")
    private void prepareDetection() {

        if (!mBeaconUtils.isLocationEnabled(TAG,this)) {

            locationRequest();

        } else { // Localización activada, comprobemos el bluetooth

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {

                mAppUtils.showToastMessage(getString(R.string.not_support_bluetooth_msg),this);

            } else if (mBluetoothAdapter.isEnabled()) {

                startDetectingBeacons();

            } else {

                // Pedir al usuario que active el bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
        finish();
    }

}
