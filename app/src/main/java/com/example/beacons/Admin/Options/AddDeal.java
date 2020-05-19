package com.example.beacons.Admin.Options;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacons.Admin.session.ManagerDashBoard;
import com.example.beacons.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddDeal extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddDeal";

    // private variables for the Deal to be set
    private static final String COMPANY_ID = "compId";
    private static final String DEAL = "Deal";
    private static final String PRICE_OR_DISCOUNT = "Price_Discount";
    private static final String DEAL_LINK = "Deal_Link";
    private static final String TIMES = "Time";

    // this spinner holds the times for a deal
    Spinner times;
    // this FirebaseAuth gets the current user id
    FirebaseAuth fAuth;
    String userId;
    // the Edit text for the Deal details
    EditText deal, price_or_discount, deal_link;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String deal_times;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);
        getsentBack().setOnClickListener(this);

        times = findViewById(R.id.times);

        deal = findViewById(R.id.dealDescription);
        price_or_discount = findViewById(R.id.dealPrice);
        deal_link = findViewById(R.id.dealLink);
        fAuth = FirebaseAuth.getInstance();

        // this adapter is for the spinner which holds the times for the deals
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        times.setAdapter(adapter);
        times.setOnItemSelectedListener(this);

    }

    /**
     * This method gets called when the user presses the button to save it.
     * @param v
     */
    public void saveDeal(View v) {
        final String deal_name = deal.getText().toString().trim();
        final String deal_price_or_disc = price_or_discount.getText().toString().trim();
        final String deal_website = deal_link.getText().toString().trim();
        final Map<String, Object> Deal = new HashMap<>();


        userId = fAuth.getCurrentUser().getUid();
// Deal is the map with the values of a deal
        Deal.put(COMPANY_ID, userId);
        Deal.put(DEAL, deal_name);
        Deal.put(PRICE_OR_DISCOUNT, deal_price_or_disc);
        Deal.put(DEAL_LINK, deal_website);
        Deal.put(TIMES, deal_times);



        //Deals is the collection in which deals will be stored
        DocumentReference docRef = db.collection("Deals").document();
        // when the deals are saved, the users gets redirected to the dashboard
                 docRef.set(Deal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDeal.this, R.string.deal_saved, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDeal.this, R.string.could_not_save, Toast.LENGTH_SHORT).show();

                    }
                });

    }


    /**
     * the user ca press a goback text view to go back to the managers dashboard
     * @return
     */
    public TextView getsentBack() {

        return (TextView) findViewById(R.id.backToDash);
    }

    /**
     * Getting the back to the dashboard
     * @param v
     */
    @Override
    public void onClick(View v) {

        if (v.equals(findViewById(R.id.backToDash))) {
            startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
        finish();
    }

    /**
     * This method calls the time adapter
     * The time adapter contains the times a deal can be set for
     * after getting the adapter, it takes the position the user selects
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deal_times = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
