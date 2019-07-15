package com.densoftdevelopers.installation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteAssetHelper openHelper;
    public  SQLiteDatabase db;
    private  static  DatabaseAccess instance;
    public Cursor c = null;
    private  String TABLENAME;


    //pivate constructor
    private DatabaseAccess(Context context)
    {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //to return  a single instance of the database
    public static DatabaseAccess getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseAccess(context);
        }
        return  instance;
    }


    //to open the database
    public void open()
    {
        this.db = openHelper.getWritableDatabase();
    }

    //closing the database
    public void close()
    {
        if (db != null)
        {
            this.db.close();
        }
    }

    //getting all the sector values based on technology
    public List<String>getAllSectors(String table_name)
    {

        List<String> list = new ArrayList<>();

        //select all query
        c = db.rawQuery("select sector from '"+table_name+"' where selected = 'no'",null);

        if (c.moveToFirst())
        {
            do {
                list.add(c.getString(0));
            }while (c.moveToNext());
        }

        return  list;
    }


    //updating row value
    public String updateSector_table(String table_name, String sector_no)
    {
       ContentValues contentValues = new ContentValues();
       contentValues.put("selected", "yes");

        long update =db.update(table_name,contentValues,"sector ="+sector_no,null);

        String message = null;

        if (update !=-1)
        {
            message = "updated";
        }
        else
        {
            message = "not updated";
        }
        return message;

    }

    //write progress table
    public String WriteProgress(String sector_num, String heading_val, String roll_val, String pitch_val)
    {

        ContentValues contentValues  = new ContentValues();
        contentValues.put("sector",sector_num);
        contentValues.put("heading",heading_val);
        contentValues.put("roll",roll_val);
        contentValues.put("pitch",pitch_val);

        //inserting Row
       long insert =  db.insert("progress",null,contentValues);

       String message = null;

        if (insert != -1)
        {
            message = "inserted";
        }
        else
        {
            message = "not inserted";
        }
        return message;
    }

    //calculate the number number of rows

    public int CountRowsSectordb(String sectortable)
    {

        int countsectorTable;
        int count;


        c = db.rawQuery("SELECT * from '"+sectortable+"' where selected = 'yes'",null);
        countsectorTable = c.getCount();

        if (countsectorTable>0)
        {
            count = countsectorTable;
        }
        else
        {
           count = 0;
        }


        return  count;

    }

    public int CountRowsProgressDb()
    {

        int countProgress = 0;
        c = db.rawQuery("SELECT * from progress",null);

        if (countProgress >0)
        {
            countProgress = c.getCount();
        }
        else
        {
            countProgress = 0;
        }


        return  countProgress;
    }

    public  String emptyProgressTable()
    {
       int delete =  db.delete("progress",null,null);

        String deletestats;

       if (delete > 0)
        {
            deletestats = "deleted";
        }
        else
       {
           deletestats = "not deleted";
       }

       return deletestats;
    }
}
