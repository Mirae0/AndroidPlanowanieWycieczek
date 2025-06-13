package com.example.androidplanowaniewycieczek.ui.firstpage;

import java.util.ArrayList;
import java.util.List;
public class TripStorageHolder {
    private static final List<Trip> trips = new ArrayList<>();

    public static List<Trip> getTrips() {
        return trips;
    }

    public static void addTrip(Trip trip) {
        // Jeśli to pierwsza przykładowa wycieczka, usuń ją
        if (!trips.isEmpty() && trips.get(0).getLocationTo().equalsIgnoreCase("Przykład")) {
            trips.clear(); // lub trips.remove(0);
        }
        trips.add(trip);
    }
}
