package com.example.androidplanowaniewycieczek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB";
    private static final int DB_VERSION = 1;
    private static final String ID_COL = "id";

    //------------TABLE USERS----------------------
    private static final String TB_USERS = "usersTB";
    private static final String NAME_COL = "name";
    private static final String SURNAME_COL = "surname";
    private static final String NICKNAME_COL = "nickname";
    private static final String EMAIL_COL = "email";
    private static final String PASSWORD_COL = "password";


    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TB_USERS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT, " + SURNAME_COL + " TEXT, " + NICKNAME_COL + " TEXT, "+ EMAIL_COL+" TEXT, " + PASSWORD_COL + " TEXT)";
        db.execSQL(query);

        registerUser(new String[]{"admin","admin","Admin","admin@admin.com","password"});

    }
    //-----------------Użytkownicy funkcje------------------------
    public void registerUser(String[] user){
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TB_USERS);
        onCreate(db);
    }
}