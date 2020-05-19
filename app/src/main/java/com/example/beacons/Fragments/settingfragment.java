package com.example.beacons.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.beacons.R;

import androidx.annotation.Nullable;

import static com.example.beacons.User.MainActivity.mPreferences;

public class settingfragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListerner;

    // Check box preferences to save the variables
   public static CheckBoxPreference food, sports, clothing, Beauty, Tech, Fun, Cinema;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // we initialize the checkBoxPreferences with their keys
        food = (android.preference.CheckBoxPreference) findPreference("food_all_related");
        sports = (android.preference.CheckBoxPreference) findPreference("sports_all_related");
        clothing = (android.preference.CheckBoxPreference) findPreference("clothes_all_related");
        Beauty = (android.preference.CheckBoxPreference) findPreference("beauty_all_related");
        Fun = (android.preference.CheckBoxPreference) findPreference("fun_all_related");
        Cinema = (android.preference.CheckBoxPreference) findPreference("cinema_all_related");
        Tech = (android.preference.CheckBoxPreference) findPreference("tech_all_related");

        preferenceChangeListerner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // everytime one of the box preferences is checked a value gets added to the array
                // and a summary is set to the preference
                if (key.equals("food_all_related")) {


                    if (food.isChecked()) {
                        food.setSummaryOn("Food All related");
                        mPreferences.add("Food");

                    } else {
                        mPreferences.remove("Food");

                    }
                }
                // this methods listens for preferences changes so, a preference gets picked by a key
                if (key.equals("sports_all_related")) {


                    if (sports.isChecked()) {
                        sports.setSummaryOn("Sports All related");
                        mPreferences.add("Sports");
                    } else {
                        mPreferences.remove("Sports");
                    }
                }
                if (key.equals("clothes_all_related")) {


                    if (clothing.isChecked()) {
                        clothing.setSummaryOn("Clothes All related");
                        mPreferences.add("Clothing");
                    } else {
                        mPreferences.remove("Clothing");
                    }
                }
                if (key.equals("beauty_all_related")) {


                    if (Beauty.isChecked()) {
                        Beauty.setSummaryOn("Beauty All related");
                        mPreferences.add("Beauty");

                    } else {
                        mPreferences.remove("Beauty");
                    }
                }
                if (key.equals("tech_all_related")) {


                    if (Tech.isChecked()) {
                        Tech.setSummaryOn("Tech All related");
                        mPreferences.add("Tech");

                    } else {
                        mPreferences.remove("Tech");
                    }
                }
                if (key.equals("fun_all_related")) {


                    if (Fun.isChecked()) {
                        Fun.setSummaryOn("Fun All related");
                        mPreferences.add("Fun");

                    } else {
                        mPreferences.remove("Fun");

                    }
                }

                if (key.equals("cinema_all_related")) {


                    if (Cinema.isChecked()) {
                        Cinema.setSummaryOn("Cinema All related");
                        mPreferences.add("Cinema");

                    } else {
                        mPreferences.remove("Cinema");
                    }
                }


            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        setPreferencesSummaries();

        // registering the onSharePreferences change listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListerner);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregistering the onSharePreferences change listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListerner);
    }


    /**
     * This method will be executed when the activity opens
     * It will add preferences to the array and set summaries where needed
     */
    public void setPreferencesSummaries() {
        if (mPreferences.contains("Food") || food.isChecked()) {
            food.setSummary("Food all related");

            addorno("Food");
        }
        if (mPreferences.contains("Sports") || sports.isChecked()) {
            sports.setSummary("Sports all related");

            addorno("Sports");
        }
        if (mPreferences.contains("Fun") || Fun.isChecked()) {
            Fun.setSummary("Fun all related");

            addorno("Fun");
        }
        if (mPreferences.contains("Cinema") || Cinema.isChecked()) {
            Cinema.setSummary("Cinema all related");
            addorno("Cinema");
        }
        if (mPreferences.contains("Clothing") || clothing.isChecked()) {
            clothing.setSummary("Clothing all related");

            addorno("Clothing");
        }
        if (mPreferences.contains("Beauty") || Beauty.isChecked()) {
            clothing.setSummary("Clothing all related");
            addorno("Beauty");
        }
        if (mPreferences.contains("Tech") || Tech.isChecked()) {
            Tech.setSummary("Tech all related");
            addorno("Tech");
        }

    }

    /**
     * this method will check if a value is in the array, if it isn't it will be added
     * mPreferences should only have 1 of each value
     * @param value
     */
    public void addorno(String value) {

        if (!mPreferences.contains(value)) {
            mPreferences.add(value);
        }
    }
}
