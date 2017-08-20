package com.kawabanga.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bugsec on 20/08/2017.
 */

public class ModelSql extends SQLiteOpenHelper {

    ModelSql(Context context) {
        super(context, "database.db", null,4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PostSql.onUpgrade(db,oldVersion,newVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        PostSql.onCreate(db);

    }
}