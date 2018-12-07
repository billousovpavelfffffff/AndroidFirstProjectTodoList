package com.prog.belousov.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Константа, которая содержит имя таблицы в базе данных.
    private final static String TABLE_NAME = "TASKS";
    // Имя базы данных
    private static final String COLUMN_USERTEXT = "USERTEXT";
    private static final String DB_NAME = "tasksdb";
    // Версия базы данных
    private static final int DB_VERSION = 1;

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //Метод, который добавляет в базу данных задание.
    public void addTask(Task task) {
        //Получаем базу данных.
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERTEXT, task.getTaskText());
        //Добавляем новые задания в базу данных.
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }


    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> taskList = new ArrayList<>();
        //Получаем ссылку на базу данных.
        SQLiteDatabase database = this.getWritableDatabase();
        //Курсор, для доступа к запросу из базы данных. Можно назвать итератором.
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_USERTEXT},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            //Пока есть строки, он оттуда берет инфу.
            do {
                String userText = cursor.getString(0);
                taskList.add(new Task(userText));
            } while (cursor.moveToNext());
        }
        //Освобождаем ресурсы.
        cursor.close();
        database.close();
        return taskList;
    }
    //Метод, удаляющий все задания из базы данных.
    public void  deleteAllTasks(){
        //Получение ссылки на базу данных.
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, null, null);
        database.close();
    }
    //Метод, удаляющий из БД определенное задание.
    public void deleteTask(Task task){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, COLUMN_USERTEXT + " = ?", new String[] {task.getTaskText()});
    }
    //Метод, меняющий в базе данных oldTask на newTask.
     public void updateTask(Task oldTask, Task newTask){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERTEXT, newTask.getTaskText());
        database.update(TABLE_NAME, contentValues, COLUMN_USERTEXT + " = ?", new String[] {oldTask.getTaskText()});
     }


}