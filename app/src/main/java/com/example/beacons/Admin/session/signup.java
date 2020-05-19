package com.example.beacons.Admin.session;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacons.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private static final String TAG = "signup";

    private static final String COMPANY_NAME = "Company_name";
    private static final String COMPANY_EMAIL = "Email";
    private static final String COMPANY_CATEGORY = "Category";

    // these are inputs made by the user
    EditText companyName, email, password, passwordRepeat;

    // the spinner for the categories example food, sports, fun  and so on
    Spinner categories;
    // an instance to the firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // this instance is for the authentication
    FirebaseAuth fAuth;
    // the user id is the id of user currently logged in
    String userId;
    String compaCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // getting the values from the text fields
        companyName = findViewById(R.id.editText7);
        categories = findViewById(R.id.editText8);
        email = findViewById(R.id.editText6);
        password = findViewById(R.id.editText3);
        passwordRepeat = findViewById(R.id.editText4);
        fAuth = FirebaseAuth.getInstance();

        // this adapter is for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        categories.setOnItemSelectedListener(this);
        getTextView().setOnClickListener(this);
    }


    /**
     * this method will be called when the user presses the button to save the company
     * @param v
     */
    public void saveCompany(View v) {

        final String companyname = companyName.getText().toString().trim();

        final String emailAd = email.getText().toString().trim();
        final String passwordAd = password.getText().toString().trim();
        final String passwordR = passwordRepeat.getText().toString().trim();
        final Map<String, Object> company = new HashMap<>();

        if (TextUtils.isEmpty(companyname)) {
            companyName.setError("name required");
            return;
        }

        if (companyname.matches("[0-9]+")) {
            companyName.setError("Name must contain only letters");
            return;
        }
        if (companyname.matches("[^<>%$]")) {
            companyName.setError("No special characters allowed");
            return;
        }
        if (TextUtils.isEmpty(emailAd)) {
            email.setError("users email required");
            return;
        }
        if (TextUtils.isEmpty(passwordAd)) {
            password.setError("users password required");
            return;
        }
        if (!emailAd.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            email.setError("users Email must be written correctly eg.User@gmail.com");
            return;
        }
        if (passwordAd.length() <= 8) {
            password.setError("Users password must be least 8 characters long");
            return;
        }
        if (!passwordAd.matches(passwordR)) {
            passwordRepeat.setError("Password must match");
        }


        fAuth.createUserWithEmailAndPassword(emailAd, passwordAd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   // getting current user id
                    userId = fAuth.getCurrentUser().getUid();
                    DocumentReference docRef = db.collection("Companies").document(userId);

                    // saving the values into the map
                    company.put(COMPANY_NAME, companyname);
                    company.put(COMPANY_CATEGORY, compaCategory);
                    company.put(COMPANY_EMAIL, emailAd);
                    docRef.set(company)

                            // if the user is registered this will happen otherwise it will no be successful
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(signup.this, "Your business has been registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(signup.this, "An erro has ocured, please try it again later", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                } else {
                    Toast.makeText(signup.this, "Error!" + task.getException().getMessage() + emailAd, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


    /**
     * getting the textView to set a click listener
     * to send the user to the login page
     * @return
     */
    public TextView getTextView() {

        return (TextView) findViewById(R.id.sendToLogin);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.equals(findViewById(R.id.sendToLogin))) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    /**
     * this method is for the categories
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        compaCategory = parent.getItemAtPosition(position).toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
