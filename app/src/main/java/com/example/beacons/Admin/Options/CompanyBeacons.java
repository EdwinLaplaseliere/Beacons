package com.example.beacons.Admin.Options;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.beacons.Admin.Adapters.BeaconAdapter;
import com.example.beacons.Admin.session.ManagerDashBoard;
import com.example.beacons.Admin.Models.Beacon;
import com.example.beacons.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CompanyBeacons extends AppCompatActivity {
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference Beaconref= db.collection("Beacons");

    private BeaconAdapter adapter;
    String userId;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_beacons);


        fAuth = FirebaseAuth.getInstance();
        setUpBeaconRecyclerView();
    }

    /**
     * this is where the beacons get shown
     * they get shown using recycler view
     * it will show it with a company id and a beacon id
     */
    public void setUpBeaconRecyclerView(){

        userId=fAuth.getCurrentUser().getUid();
        Query query=Beaconref.whereEqualTo("compId",userId);

        // an object of the class beacon gets created
        // this gets shown as a beacon_item
        FirestoreRecyclerOptions<Beacon> options= new FirestoreRecyclerOptions.Builder<Beacon>()
                .setQuery(query, Beacon.class)
                .build();
        adapter=new BeaconAdapter(options);
        RecyclerView recyclerView= findViewById(R.id.recycler_Beacon_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // if the beacon gets swiped to the left it will get deleted
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteBeacon(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * on start the adapter starts listening for a swipe
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * on back press the user gets redirected to the dashboard
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
        finish();
    }
}
