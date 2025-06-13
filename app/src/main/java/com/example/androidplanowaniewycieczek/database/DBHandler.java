package com.example.androidplanowaniewycieczek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidplanowaniewycieczek.ui.firstpage.Trip;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB";
    private static final int DB_VERSION = 1;
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
                SAVED_TRIP_NAME_COL + " TEXT, " + SAVED_FROM_COL + " TEXT, " + SAVED_TO_COL + " TEXT, "+ SAVED_DISTANCE_COL+" REAL, " + SAVED_TIME_COL + " TEXT, "+ SAVED_DATE_COL + " TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TB_FUTURE_TRIPS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FUTURE_TRIP_NAME_COL + " TEXT, " + FUTURE_FROM_COL + " TEXT, " + FUTURE_TO_COL + " TEXT, "+ FUTURE_DISTANCE_COL+" REAL, " + FUTURE_DATE_COL + " TEXT)";
        db.execSQL(query);

        registerUser(new String[]{"admin","admin","Admin","admin@admin.com","password"});
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

    //-----------------SAVED_TRIPS FUNC------------------------

    public void saveTrip(Trip trip){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SAVED_TRIP_NAME_COL,trip.getName());
        values.put(SAVED_FROM_COL,trip.getLocationFrom());
        values.put(SAVED_TO_COL,trip.getLocationTo());
        values.put(SAVED_DISTANCE_COL,trip.getTotalDistanceKm());
        values.put(SAVED_TIME_COL,trip.getFormattedDuration());
       //values.put(SAVED_DATE_COL,trip.());


        db.insert(TB_USERS,null,values);
        db.close();
    }

    public ArrayList<Trip> getRanking(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Trip> wynik = new ArrayList<>();
        String query="SELECT * FROM "+TB_SAVED_TRIPS+" ORDER BY "+SAVED_DISTANCE_COL+" DESC";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                wynik.add(new Trip(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getDouble(4),cursor.getString(5),cursor.getLong(6)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wynik;
    }

    //-----------------FUTURE_TRIPS FUNC------------------------
    public ArrayList<Trip> getFutureTripsAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Trip> wynik = new ArrayList<>();
        String query="SELECT * FROM "+TB_FUTURE_TRIPS;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                wynik.add(new Trip(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getDouble(4),cursor.getString(5),cursor.getLong(6)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wynik;
    }

    public ArrayList<String> getFutureTripsNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> wynik = new ArrayList<>();
        String query="SELECT "+FUTURE_TRIP_NAME_COL+" FROM "+TB_FUTURE_TRIPS;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                wynik.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wynik;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TB_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+TB_SAVED_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS "+TB_FUTURE_TRIPS);
        onCreate(db);
    }

}