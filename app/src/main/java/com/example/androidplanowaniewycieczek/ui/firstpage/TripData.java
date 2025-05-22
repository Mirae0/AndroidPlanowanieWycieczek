package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.graphics.Bitmap;

public class TripData {
    public String from;
    public String to;
    public long tripTimeMillis;
    public Bitmap imageBitmap;

    public TripData(String from, String to, long tripTimeMillis, Bitmap imageBitmap) {
        this.from = from;
        this.to = to;
        this.tripTimeMillis = tripTimeMillis;
        this.imageBitmap = imageBitmap;
    }

    public long getTripTimeMillis() {
        return tripTimeMillis;
    }

    public String getTo() {
        return to;
    }
}

