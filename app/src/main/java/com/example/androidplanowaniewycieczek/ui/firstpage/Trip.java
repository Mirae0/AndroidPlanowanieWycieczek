package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class Trip {

    private long startTime;
    private long endTime;
    public double totalDistanceMeters;
    public double totalDistanceKM;
    private long durationMillis;
    private String name;
    private String locationFrom;
    private String locationTo;
    public String tripDate;
    public Bitmap imageBitmap;


    public Trip(){
        super();
    }

    public Trip(String locationFrom, String locationTo, long durationMillis, Bitmap imageBitmap){
        this.locationFrom=locationFrom;
        this.locationTo=locationTo;
        this.durationMillis=durationMillis;
        this.imageBitmap=imageBitmap;
    }

    public Trip(String name, String locationFrom, String locationTo, double totalDistanceKM, String tripDate, long durationMillis){
        this.name = name;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.totalDistanceKM = totalDistanceKM;
        this.tripDate = tripDate;
        this.durationMillis = durationMillis;
    }

    public String getTripDate(){
        return tripDate;
    }

    public void startTrip() {
        startTime = System.currentTimeMillis();
        totalDistanceMeters = 0;
        durationMillis = 0;
    }

    public void endTrip() {
        endTime = System.currentTimeMillis();
        durationMillis = endTime - startTime;
    }

    public void calculateDistance(List<GeoPoint> points) {
        totalDistanceMeters = 0;
        for (int i = 1; i < points.size(); i++) {
            totalDistanceMeters += points.get(i - 1).distanceToAsDouble(points.get(i));
        }
    }

    public double getTotalDistanceKm() {
        return totalDistanceMeters / 1000.0;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getLocationTo() {return locationTo; }

    public String getLocationFrom() {return locationFrom;}

    public String getName() {return name;}

    public String getFormattedDuration() {
        long seconds = getDurationMillis() / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public double getAverageSpeedKmH() {
        double hours = getDurationMillis() / 3600000.0;
        return hours > 0 ? getTotalDistanceKm() / hours : 0;
    }

    public void setDurationMillis(long millis) {
        this.durationMillis = millis;
    }


    public void setName(String name) { this.name = name; }


    public void setLocationFrom(String string) {
        this.locationFrom=string;
    }

    public void setLocationTo(String string) {
        this.locationTo=string;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public void setTotalDistanceKM(double totalDistanceKM) {
        this.totalDistanceKM = totalDistanceKM;
    }
}
