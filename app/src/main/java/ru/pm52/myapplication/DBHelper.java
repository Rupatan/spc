package ru.pm52.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    protected final static String DB_NAME = "pm52";
    protected final static int DB_VERSION = 1;

    public DBHelper() {
        super(App.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringSql = new StringBuilder();
        stringSql.append("CREATE TABLE SETTINGS(");
        stringSql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        stringSql.append("Name TEXT NOT NULL DEFAULT \"\",");
        stringSql.append("Value TEXT NOT NULL DEFAULT \"\"");
        stringSql.append(")");

        sqLiteDatabase.execSQL(stringSql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase getReadableDB(){
        return getReadableDatabase();
    }

    public SQLiteDatabase getWritableDB(){
        return getWritableDatabase();
    }
}
