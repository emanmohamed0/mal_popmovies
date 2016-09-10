package com.example.emyeraky.mal_popmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Emy Eraky on 8/30/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    //database_declare
    private static final String DATABASE_NAME="Movie";
    private static final int DATABASE_VERSION=1;

    //tables
    public static final String TABLE_NAME="MovieData";

    //columns
    public static final String ID="_id";
    public static final String MID="id";
    public static final String POSTER_PATH = "poster_path" ;
    public static final String TIME = "time" ;
    public static final String ORIGINAL_TITLE ="original_title" ;
    public static final String POPULARITY = "popularity" ;
    public static final String DATE = "date";

    //create TABLE
    String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+
            "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            MID+" TEXT,"+ORIGINAL_TITLE+" TEXT,"+TIME+" TEXT,"+DATE+" TEXT,"+POSTER_PATH+" TEXT,"+POPULARITY+" TEXT);";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
