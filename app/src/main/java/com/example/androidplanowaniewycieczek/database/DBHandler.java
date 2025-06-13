package com.example.androidplanowaniewycieczek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.androidplanowaniewycieczek.ui.firstpage.Trip;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB";
    private static final int DB_VERSION = 4;
    private static final String ID_COL = "id";

    //------------TABLE USERS----------------------
    private static final String TB_USERS = "usersTB";
    private static final String NAME_COL = "nameCol";
    private static final String SURNAME_COL = "surnameCol";
    private static final String NICKNAME_COL = "nicknameCol";
    private static final String EMAIL_COL = "emailCol";
    private static final String PASSWORD_COL = "passwordCol";

    //------------TABLE SAVED_TRIPS----------------------
    private static final String TB_SAVED_TRIPS = "savedTripsTB";
    private static final String SAVED_TRIP_NAME_COL = "tripNameCol";
    private static final String SAVED_FROM_COL = "fromCol";
    private static final String SAVED_TO_COL = "toCol";
    private static final String SAVED_DISTANCE_COL = "distanceCol";
    private static final String SAVED_TIME_COL = "timeCol";
    private static final String SAVED_DATE_COL = "dateCol";
    private static final String SAVED_DURATION_COL = "durationCol";


    //------------TABLE SAVED_TRIPS----------------------
    private static final String TB_FUTURE_TRIPS = "futureSavedTripsTB";
    private static final String FUTURE_TRIP_NAME_COL = "futureTripNameCol";
    private static final String FUTURE_FROM_COL = "futureFromCol";
    private static final String FUTURE_TO_COL = "futureToCol";
    private static final String FUTURE_DISTANCE_COL = "futureDistanceCol";
    private static final String FUTURE_DATE_COL = "futureDateCol";

    Context context;
    public DBHandler(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.context= context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TB_USERS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT, " + SURNAME_COL + " TEXT, " + NICKNAME_COL + " TEXT, "+ EMAIL_COL+" TEXT, " + PASSWORD_COL + " TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TB_SAVED_TRIPS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SAVED_TRIP_NAME_COL + " TEXT, " + SAVED_FROM_COL + " TEXT, " + SAVED_TO_COL + " TEXT, "+ SAVED_DISTANCE_COL+" REAL, " + SAVED_TIME_COL + " TEXT, "+SAVED_DURATION_COL + " INTEGER, " + SAVED_DATE_COL + " TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TB_FUTURE_TRIPS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FUTURE_TRIP_NAME_COL + " TEXT, " + FUTURE_FROM_COL + " TEXT, " + FUTURE_TO_COL + " TEXT, "+ FUTURE_DISTANCE_COL+" REAL, " + FUTURE_DATE_COL + " TEXT)";
        db.execSQL(query);

        ContentValues values = new ContentValues();
        values.put(NAME_COL, "admin");
        values.put(SURNAME_COL, "admin");
        values.put(NICKNAME_COL, "Admin");
        values.put(EMAIL_COL, "admin@admin.com");
        values.put(PASSWORD_COL, "password");
        db.insert(TB_USERS, null, values);

        //db.close();

    }
    //-----------------USERS FUNC------------------------
    public void registerUser(String[] user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, user[0]);
        values.put(SURNAME_COL,user[1]);
        values.put(NICKNAME_COL,user[2]);
        values.put(EMAIL_COL,user[3]);
        values.put(PASSWORD_COL,user[4]);

        db.insert(TB_USERS,null,values);
        db.close();
    }

    public String getUsernameFromDB(String usernameQ){
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM "+TB_USERS+" WHERE username = "+usernameQ;

        Cursor cursor = db.rawQuery(query, null);
        String wynik;

        if(cursor.moveToFirst()) {
            wynik = cursor.getString(3);
            cursor.close();
            db.close();
            return wynik;
        }else{
            cursor.close();
            db.close();
            return "DATABASE ERROR";
        }
    }

    public void removeUserFromDB(String nick){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TB_USERS,NICKNAME_COL+"=?",new String[]{nick});
        db.close();
    }
    //sprawdzenie czy poprawne dane logowania
    public boolean checkUserCredentials(String nickname, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TB_USERS + " WHERE " + NICKNAME_COL + " = ? AND " + PASSWORD_COL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nickname, password});

        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }
    //reset hasla
    public boolean updateUserPassword(String nickname, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TB_USERS, null, NICKNAME_COL + "=? COLLATE NOCASE", new String[]{nickname}, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(PASSWORD_COL, newPassword);
        int rows = db.update(TB_USERS, values, NICKNAME_COL + "=? COLLATE NOCASE", new String[]{nickname});
        db.close();
        return rows > 0;
    }



    //-----------------SAVED_TRIPS FUNC------------------------

    public void saveTrip(Trip trip){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SAVED_TRIP_NAME_COL, trip.getName());
        values.put(SAVED_FROM_COL, trip.getLocationFrom());
        values.put(SAVED_TO_COL, trip.getLocationTo());
        values.put(SAVED_DISTANCE_COL, trip.getTotalDistanceKm());
        values.put(SAVED_DURATION_COL, trip.getDurationMillis());
        values.put(SAVED_DATE_COL, trip.tripDate);

        db.insert(TB_SAVED_TRIPS, null, values);
        db.close();
        Log.d("DB", "Zapisano wycieczkę: " + trip.getName());

    }


    public ArrayList<Trip> getRanking() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Trip> wynik = new ArrayList<>();

        String query = "SELECT * FROM " + TB_SAVED_TRIPS + " ORDER BY " + SAVED_DISTANCE_COL + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SAVED_TRIP_NAME_COL));
                String from = cursor.getString(cursor.getColumnIndexOrThrow(SAVED_FROM_COL));
                String to = cursor.getString(cursor.getColumnIndexOrThrow(SAVED_TO_COL));
                double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(SAVED_DISTANCE_COL));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(SAVED_DATE_COL));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(SAVED_DURATION_COL));

                Trip trip = new Trip(name, from, to, distance, date, duration);
                wynik.add(trip);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d("DB", "Pobrano z bazy: " + wynik.size() + " wycieczek");

        return wynik;

    }

    public void saveFutureTrip(String name, String from, String to, double distance, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FUTURE_TRIP_NAME_COL, name);
        values.put(FUTURE_FROM_COL, from);
        values.put(FUTURE_TO_COL, to);
        values.put(FUTURE_DISTANCE_COL, distance);
        values.put(FUTURE_DATE_COL, date);
        db.insert(TB_FUTURE_TRIPS, null, values);
        db.close();
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TB_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+TB_SAVED_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS "+TB_FUTURE_TRIPS);
        onCreate(db);
    }

}