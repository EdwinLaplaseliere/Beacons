package com.example.beacons.Admin.Options;

import android.content.Intent;
import android.os.Bundle;

import com.example.beacons.Admin.Adapters.DealAdapter;
import com.example.beacons.Admin.session.ManagerDashBoard;
import com.example.beacons.Admin.Models.Deal;
import com.example.beacons.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyDeals extends AppCompatActivity {

    String userId;
    FirebaseAuth fAuth;
    // getting variables ready for later use. The FirebaseFirestore
    // and the collection reference will give us access to the deals collections
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Dealref = db.collection("Deals");
    private DealAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_deals);
        // initializing FirebaseAuth for later usage
        fAuth = FirebaseAuth.getInstance();
        // setting up the recycler view
        setUpRecyclerView();
    }

    /**
     * this is where the deals get shown
     * they get shown using recycler view
     * it will show it with few other details. Such as price, times
     */
    private void setUpRecyclerView() {
        userId = fAuth.getCurrentUser().getUid();
        Query query = Dealref.whereEqualTo("compId", userId);
        FirestoreRecyclerOptions<Deal> options = new FirestoreRecyclerOptions.Builder<Deal>()
                .setQuery(query, Deal.class)
                .build();
        adapter = new DealAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // deals get deleted by swiping left
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteDeal(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    // the adapter starts listening for a swipe in order to delete a deal
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // onBack press will redirect the user to the dashboard
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
        finish();
    }

}
