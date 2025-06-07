package com.example.androidplanowaniewycieczek.ui.firstpage;

import java.util.ArrayList;
import java.util.List;
public class TripStorageHolder {
    public static List<TripData> trips = new ArrayList<>();

    public static void addTrip(TripData trip) {
        trips.add(trip);
    }

    public static List<TripData> getTrips() {
        return trips;
    }
}
