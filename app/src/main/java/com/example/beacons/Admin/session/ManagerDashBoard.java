package com.example.beacons.Admin.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.beacons.Admin.Options.AddDeal;
import com.example.beacons.Admin.Options.CompanyBeacons;
import com.example.beacons.Admin.Options.CompanyDeals;
import com.example.beacons.Admin.Options.addBeacon;
import com.example.beacons.User.MainActivity;
import com.example.beacons.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ManagerDashBoard extends AppCompatActivity implements View.OnClickListener {


    /**
     * setting up variables we need for the dashboard, such as
     * FirebaseAuth to see if the user is correctly logged in
     * Firestore to get the user information
     */
    private static final String TAG = "ManagerDashBoard";
    TextView CompanyName, numbOfBeacons, numbOfDeals;
    FirebaseAuth fAuth;
    // connection to firestore
    FirebaseFirestore ffstore;
    String userid;
    ImageView profileImage;
    int counter = 0;
    private ProgressBar mProgressBar;
    // thi is for the profile image
    private StorageReference mstorageRef;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dash_board);
        CompanyName = findViewById(R.id.CompanyName);
        numbOfBeacons = findViewById(R.id.beaconNum);
        numbOfDeals = findViewById(R.id.DealNum);

        profileImage = findViewById(R.id.profile);
        fAuth = FirebaseAuth.getInstance();
        ffstore = FirebaseFirestore.getInstance();
        mstorageRef = FirebaseStorage.getInstance().getReference();

        userid = fAuth.getCurrentUser().getUid();
        final DocumentReference docref = ffstore.collection("Companies").document(userid);
        docref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                CompanyName.setText(documentSnapshot.getString("Company_name"));
            }
        });


// Setting the number of Deals and Beacons the company has
        getCountDocuments("Beacons", numbOfBeacons);
        getCountDocuments("Deals", numbOfDeals);


        StorageReference profileReference = mstorageRef.child("companiesProfilePic/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


//        Bitmap bitmap= BitmapFactory.

        // setting all the textViews a click Listeners
        getLogout().setOnClickListener(this);
        getAddddeal().setOnClickListener(this);
        getAddbeacon().setOnClickListener(this);
        getMyDeals().setOnClickListener(this);
        getMyBeacons().setOnClickListener(this);
        getMyBeaconsNumber().setOnClickListener(this);
        getMyDealsNum().setOnClickListener(this);
        // to change the profile image of the company
        getEditImage().setOnClickListener(this);
    }


    // an image to log out. This is to get it by using its id and then set it a click listener
    public ImageView getLogout() {

        return (ImageView) findViewById(R.id.logoutimg);
    }

    // the ones below are getters for the text views in order to set them click listeners
    public TextView getAddddeal() {

        return (TextView) findViewById(R.id.newDeal);
    }

    public TextView getMyDeals() {

        return (TextView) findViewById(R.id.seeDeals);
    }
    public TextView getMyDealsNum() {

        return (TextView) findViewById(R.id.DealNum);
    }

    public TextView getMyBeaconsNumber() {

        return (TextView) findViewById(R.id.beaconNum);
    }


    public TextView getMyBeacons() {

        return (TextView) findViewById(R.id.seeBeacons);
    }

    public TextView getAddbeacon() {

        return (TextView) findViewById(R.id.registerBeacon);
    }

    public ImageView getEditImage() {

        return (ImageView) findViewById(R.id.change);
    }


    // depends on what textView is click an action will be executed such as log out which is executed by clicking on a image view
    // or opening other activitoes to view beacons or to deals as well as to add new ones
    @Override
    public void onClick(View v) {
        if (v.equals(findViewById(R.id.logoutimg))) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();

        } else if (v.equals(findViewById(R.id.newDeal))) {
            startActivity(new Intent(getApplicationContext(), AddDeal.class));
            finish();

        } else if (v.equals(findViewById(R.id.registerBeacon))) {
            startActivity(new Intent(getApplicationContext(), addBeacon.class));
            finish();

        } else if (v.equals(findViewById(R.id.seeDeals))) {

            startActivity(new Intent(getApplicationContext(), CompanyDeals.class));
            finish();

        }else if (v.equals(findViewById(R.id.DealNum))) {

            startActivity(new Intent(getApplicationContext(), CompanyDeals.class));
            finish();

        }

        else if (v.equals(findViewById(R.id.seeBeacons))) {


            startActivity(new Intent(getApplicationContext(), CompanyBeacons.class));
            finish();

        }else if (v.equals(findViewById(R.id.beaconNum ))) {

            startActivity(new Intent(getApplicationContext(), CompanyBeacons.class));
            finish();

        }

        else if (v.equals(findViewById(R.id.change))) {

            Intent openAlbuns = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openAlbuns.setType("image/*");
            openAlbuns.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(openAlbuns, 1000);


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri image = data.getData();

            uploadProfile(image);

        }
    }

    /**
     * This is the method to upload images to the storage
     */
    public void uploadProfile(Uri image) {

        final StorageReference fileRef = mstorageRef.child("companiesProfilePic/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progress();
                        Picasso.get().load(uri).into(profileImage);
                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    // this is for the progress bar when an image is uploaded
    public void progress() {
        mProgressBar = findViewById(R.id.progress_bar);
        final Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                mProgressBar.setProgress(counter);
                if (counter == 100) {
                    t.cancel();
                }
            }
        };

        t.schedule(tt, 0, 10);
    }

    // this is to get the number of deals and beacons the company has
    public void getCountDocuments(String collection, final TextView textView) {

        ffstore.collection(collection).whereEqualTo("compId", userid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                    }
                    textView.setText(String.valueOf(count));
                    // Log.d("TAG", count + "");
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
