package com.example.beacons.Admin.Models;

/**
 * Beacon is a model of the real beacon. In thi case we are only working with a Beacon Id and a company id
 */
public class Beacon {

    private String BeaconId;
    private String compId;

    // an empty constructor needed for the recycling view
    public Beacon() {
        // needed
    }

    public Beacon(String BeaconId, String compId) {
        this.BeaconId = BeaconId;
        this.compId = compId;
    }

    public String getBeaconId() {
        return BeaconId;
    }

    public String getCompId() {
        return compId;
    }
}
