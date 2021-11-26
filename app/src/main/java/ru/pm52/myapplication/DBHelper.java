package ru.pm52.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    protected final static String DB_NAME = "pm52";
    protected final static int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder stringSql = new StringBuilder();
        stringSql.append("CREATE TABLE USERS(");
        stringSql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        stringSql.append("USER TEXT NOT NULL DEFAULT \"\",");
        stringSql.append("PASSWORD TEXT NOT NULL DEFAULT \"\"");
        stringSql.append(")");

        sqLiteDatabase.execSQL(stringSql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
