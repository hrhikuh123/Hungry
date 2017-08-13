package com.example.moranav.hungry.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * this class handles all the sql classes and the main sql DB
 */

public class ModelSql extends SQLiteOpenHelper {

    ModelSql(Context context) {
        super(context, "database.db", null,4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RestaurantSql.onUpgrade(db,oldVersion,newVersion);
        FeedBackSql.onUpgrade(db,oldVersion,newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        RestaurantSql.onCreate(db);
        FeedBackSql.onCreate(db);
    }
}
