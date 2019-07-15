package com.densoftdevelopers.installation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tech.db" ;
    private static final String TABLE_NAME = "technology_cat_table" ;
    private static final String Col_1 = "sector_1" ;
    private static final String Col_2 = "sector_2" ;
    private static final String Col_3 = "sector_3" ;
    private static final String Col_4 = "sector_4" ;
    private static final String Col_5 = "sector_5" ;
    private static final String Col_6 = "sector_6" ;
    private static final String Col_7 = "sector_7" ;

    public DatabaseHandler(@Nullable Context context) {
        super(context,DATABASE_NAME , null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_NAME+"(tech_name TEXT,sector_1 TEXT,sector_2 TEXT,sector_3 TEXT,sector_4 TEXT,sector_5 TEXT,sector_6 TEXT,sector_7 TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }
}
