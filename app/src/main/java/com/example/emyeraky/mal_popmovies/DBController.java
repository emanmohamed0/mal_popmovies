package com.example.emyeraky.mal_popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by Emy Eraky on 8/30/2016.
 */
public class DBController {
    DBHelper dbHelper;
    SQLiteDatabase database;

    public DBController(Context c){
        dbHelper=new DBHelper(c);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }
    public void close_db(){
        dbHelper.close();
    }

    public long insert_db(String id ,String original_title,String poster_path,String popularity,String date,String time) throws SQLException {
        open();

        ContentValues values=new ContentValues();
        values.put(DBHelper.MID,id);
        values.put(DBHelper.ORIGINAL_TITLE,original_title);
        values.put(DBHelper.TIME,time);
        values.put(DBHelper.DATE,date);
        values.put(DBHelper.POSTER_PATH,poster_path);
        values.put(DBHelper.POPULARITY,popularity);

        long num = database.insert(DBHelper.TABLE_NAME,null,values);

        close_db();
        return num;
    }

    public Cursor get_dataselect() throws SQLException {

        open();
        String [] column ={DBHelper.MID,DBHelper.ORIGINAL_TITLE,DBHelper.TIME,DBHelper.DATE,DBHelper.POSTER_PATH,DBHelper.POPULARITY};
        Cursor c= database.query(DBHelper.TABLE_NAME,column,null,null,null,null,null,null);

        if(c !=null){
            c.moveToFirst();
        }
        close_db();
        return  c;
    }
}
