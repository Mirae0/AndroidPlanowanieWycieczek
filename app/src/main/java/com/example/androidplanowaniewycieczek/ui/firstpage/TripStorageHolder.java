package com.example.androidplanowaniewycieczek.ui.firstpage;

import java.util.ArrayList;
import java.util.List;
public class TripStorageHolder {
    public static List<Trip> trips = new ArrayList<>();

    public static void addTrip(Trip trip) {
        trips.add(trip);
    }

    public static List<Trip> getTrips() {
        return trips;
    }
}
