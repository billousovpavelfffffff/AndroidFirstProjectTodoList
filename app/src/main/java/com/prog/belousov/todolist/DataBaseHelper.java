package com.prog.belousov.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tasksdb"; // Имя базы данных
    private static final int DB_VERSION = 1; // Версия базы данных

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    //Метод выполняется только в том случае, если база данных еще не создана.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TASKS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "USERTEXT TEXT);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
