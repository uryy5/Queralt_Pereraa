package com.example.ivan.reversi_final.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Oriol QA on 12/06/2017.
 */

public class GamesSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreate = "CREATE TABLE Games " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " alias TEXT, " +
            " end_date TEXT, " +
            " num_cells INTEGER, " +
            " flag_time TEXT, " +
            " white INTEGER, " +
            " black INTEGER, " +
            " result TEXT, " +
            " time_total REAL, " +
            " total_time INTEGER) ";

    public GamesSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Games");
        db.execSQL(sqlCreate);
    }
}


