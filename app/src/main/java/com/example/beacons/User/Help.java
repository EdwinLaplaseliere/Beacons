package com.example.beacons.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beacons.R;

public class Help extends AppCompatActivity implements View.OnClickListener{

    // these are text Views to open help options according to the type of user
    TextView textView2, user1, administrator123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // initialising text Views
        textView2 = (TextView) findViewById(R.id.textView2);
        user1 = (TextView) findViewById(R.id.user1);
        administrator123 = (TextView) findViewById(R.id.administrator123);

        findViewById(R.id.b01).setOnClickListener(this);
        findViewById(R.id.b02).setOnClickListener(this);
        findViewById(R.id.b3).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     * We use a switch statement to see what button  has been pressed
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        // we do it with every button to make sure the user ony see what he needs to
        switch(v.getId()){

            case R.id.b01:
                if (textView2.getVisibility() == View.GONE)
                {
                    textView2.setVisibility(View.VISIBLE);
                    administrator123.setVisibility(View.GONE);
                    user1.setVisibility(View.GONE);
                }else

                {
                    textView2.setVisibility(View.GONE);
                } break;



            case R.id.b02:
                if (user1.getVisibility() == View.GONE)
                {
                    user1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.GONE);
                    administrator123.setVisibility(View.GONE);
                }else

                {
                    user1.setVisibility(View.GONE);
                } break;



            case R.id.b3:
                if (administrator123.getVisibility() == View.GONE)
                {
                    administrator123.setVisibility (View.VISIBLE);
                    user1.setVisibility (View.GONE);
                    textView2.setVisibility(View.GONE);
                }else

                {
                    administrator123.setVisibility(View.GONE);
                } break;

        }
    }

    public void testing1(View view)
    {
        Toast.makeText(this,"Welcome Administrator", Toast.LENGTH_LONG).show();
    }

    public void user(View view)
    {
        Toast.makeText(this,"Welcome User", Toast.LENGTH_LONG).show();
    }
}
