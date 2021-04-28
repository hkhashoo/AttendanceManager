package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "StudentDB";
    public static final String TABLE_NAME = "Subjects";
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + "(id integer primary key autoincrement, name text, classes_attended integer, classes_total integer )"
        );

        /*db.execSQL(
                "create table " + TABLE_NAME + "(id integer primary key autoincrement, globalAttended integer, globalTotal integer )"
        );*/
    }

    public void removeRow(String subject){
        String query = "DELETE FROM "+TABLE_NAME+" WHERE name = \""+subject+"\"";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(String name, int classes_attended, int classes_total){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("classes_attended", classes_attended);
        contentValues.put("classes_total", classes_total);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return data;
    }

    public boolean subjectIsInDB(String subjectName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE name = \""+subjectName+"\"", null);
        if(data.getCount()<=0){
            data.close();
            return false;
        }
        data.close();
        return true;
    }

    public boolean tableIsNotEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if(data.getCount()<=0){
            data.close();
            return false;
        }

        return true;
    }

    public void changeAttendedTotal(String subjectname, String classes_attended, String classes_total){
        String query = "UPDATE "+TABLE_NAME+" SET classes_attended = "+classes_attended +
                ", classes_total = "+classes_total+ " WHERE name = \""+ subjectname+"\"";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);


    }

    public void changeName(String oldName, String newName){
        String query = "UPDATE "+ TABLE_NAME+" SET name = \""+newName+ "\" WHERE name = \""+oldName+"\"";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

}
