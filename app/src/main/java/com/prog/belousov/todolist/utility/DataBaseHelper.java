package com.prog.belousov.todolist.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.prog.belousov.todolist.Task;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Константа, которая содержит имя таблицы в базе данных.
    private final static String TABLE_NAME = "TASKS";
    // Имя базы данных
    private static final String DB_NAME = "tasksdb";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERTEXT = "USERTEXT";
    private static final String COLUMN_ISDONE = "ISDONE";
    private static final String COLUMN_EXTRA = "EXTRA";
    private static final String COLUMN_TIME_OF_ALARM = "TIMEOFALARM";

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
                + "USERTEXT TEXT, "
                + "ISDONE INTEGER, "
                + "EXTRA TEXT, "
                + "TIMEOFALARM TEXT);");
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
        contentValues.put(COLUMN_ISDONE, task.isDone()? 1: 0);
        if(task.getTaskText()!= null)  contentValues.put(COLUMN_EXTRA, task.getExtraText());
        if(task.getTimeOfAlarm() != null) contentValues.put(COLUMN_TIME_OF_ALARM, task.getTimeOfAlarm());
        //Добавляем новые задания в базу данных.
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }


    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> taskList = new ArrayList<>();
        //Получаем ссылку на базу данных.
        SQLiteDatabase database = this.getWritableDatabase();
        //Курсор, для доступа к запросу из базы данных. Можно назвать итератором.
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_USERTEXT, COLUMN_ISDONE, COLUMN_EXTRA, COLUMN_TIME_OF_ALARM},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            //Пока есть строки, он оттуда берет инфу.
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                String userText = cursor.getString(1);
                task.setTaskText(userText);
                boolean done = cursor.getInt(2) == 1;
                task.setDone(done);
                String extra = cursor.getString(3);
                if(extra!=null) task.setExtraText(extra);
                String time = cursor.getString(4);
                if (time !=null) task.setTimeOfAlarm(time);
                taskList.add(task);
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
        String extra = newTask.getExtraText();
        if(extra != null) contentValues.put(COLUMN_EXTRA, extra);
        String time = newTask.getTimeOfAlarm();
        if (time!=null) contentValues.put(COLUMN_TIME_OF_ALARM, time);
        database.update(TABLE_NAME, contentValues, COLUMN_USERTEXT + " = ?", new String[] {oldTask.getTaskText()});
     }
     //Отмечает задание как сделанное в БД.
    public void updateTaskToDone(Task task){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ISDONE, 1);
        database.update(TABLE_NAME, contentValues, COLUMN_USERTEXT + " = ?", new String[] {task.getTaskText()});
    }
    //Уберает отметку задания как отмеченное с БД.
    public void updateTaskToUndone(Task task){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ISDONE, 0);
        database.update(TABLE_NAME, contentValues, COLUMN_USERTEXT + " = ?", new String[] {task.getTaskText()});
    }

    public void deleteAllDoneTasks(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, COLUMN_ISDONE + " = ?", new String[]{"1"});
    }

    public int  getTaskId(Task task){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_ID},
                COLUMN_USERTEXT + " = ?", new String[]{task.getTaskText()}, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public Task getTaskById(int id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_USERTEXT, COLUMN_ISDONE, COLUMN_EXTRA},
                COLUMN_ID + " = ?",new String[]{Integer.toString(id)} , null, null, null);
        Task task = new Task();
        if(cursor.moveToFirst()) {
            String userText = cursor.getString(0);
            task.setTaskText(userText);
            boolean done = cursor.getInt(1) == 1;
            task.setDone(done);
            String extra = cursor.getString(2);
            if (extra != null) task.setExtraText(extra);
            return task;
        } else return null;

    }
}