package com.example.beacons.Admin.session;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacons.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // these are the variables we are using in the view
    TextView gotosignup;
    EditText Email;
    EditText Password;
    Button Login;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
// givig value to the view variables by getting an id
        gotosignup = findViewById(R.id.textView2);
        Email = findViewById(R.id.editText);
        Password = findViewById(R.id.editText2);
        Login = findViewById(R.id.button2);
        fAuth = FirebaseAuth.getInstance();

        // getting the current user, if there id=s any current user it will be redirected to the managaers dashboard
        FirebaseUser firebaseUser=fAuth.getCurrentUser();

         if(firebaseUser!=null){
             startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
        }


        /**
         * when the login button is pressed all the validations are gonna be executed for a correct login
         */
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();

                // checking that the user entered a valid email and password
                if (TextUtils.isEmpty(email)) {
                    Email.setError("email required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Password.setError("password required");
                    return;
                }
                if (!email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
                    Email.setError("Email must be written correctly eg.User@gmail.com");
                    return;
                }
                if (password.length() <= 8) {
                    Password.setError("password must be least 8 characters long");
                    return;
                }

                // this is a call to the firebase function to sign in with an email and a password
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ManagerDashBoard.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Error occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });



            }
        });
// if the user hasnt got an account yet, they can press the create an account
// textview which will redirect them to the signup page
        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), signup.class));
                finish();
            }
        });




    }
}
