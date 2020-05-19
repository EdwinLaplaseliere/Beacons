package com.example.beacons.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.beacons.Fragments.settingfragment;
import com.example.beacons.R;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getSupportActionBar().setTitle("Settings");

        if(findViewById(R.id.fragment_container)!=null){

            if(savedInstanceState!=null)

                return;

            getFragmentManager().beginTransaction().add(R.id.fragment_container, new settingfragment()).commit();



        }
    }
}
