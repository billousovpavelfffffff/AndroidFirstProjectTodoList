package com.prog.belousov.todolist;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import junit.framework.Test;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    //Константа, код для идентификации для startActivityForResult()
    private final static int REQUEST_CODE = 1;
    //Константа, которая содержит имя таблицы в базе данных.
    private final static String DATABASE_TABLE = "TASKS";
    //Сам список заданий.
    ListView listView;
    //Адаптер для ListVie, который принимает айди разметки, на этой разметке айди TextView, и массив заданий.
    ArrayAdapter<Task> listAdapter;
    //Наш массив заданий пользователя.
    ArrayList<Task> taskList;
    //База данных с заданиями пользователя.
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        //Сцепляем ListView из разметки к ListView в коде.
        listView = findViewById(R.id.listView);
        //Получаем базу данных, от DataBaseHelper в которую иожем записывать элементы.
        database = new DataBaseHelper(this).getWritableDatabase();
        //Иницилизация массива.
        taskList = new ArrayList<>();
        initList();

    }

    //Метод иницилизации ListView.
    void initList() {
        Cursor cursor = database.query(DATABASE_TABLE, new String[]{"USERTEXT"},
                null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String userText = cursor.getString(0);
            taskList.add(new Task(userText));
        }
        cursor.close();
        //Инициализируем адаптер, в качестве параметров: Контекст, файл разметки с TextView, и айди конкретного TextView, куда он будет вставлять текст.
        listAdapter = new ArrayAdapter<>(this,
                R.layout.item_task, R.id.taskTextView, taskList);
        //Связываем адаптер с ListView.
        listView.setAdapter(listAdapter);
    }

    //Метод для установки кнопок меню (Те что сверху)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Метод для описания того, что должно происходить при нажатиях на пункты меню.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //При нажатии на белый плюс сверху.
            case R.id.action_create_task:
                Intent intent = new Intent(this, CreateNewTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            //При нажатии на Settings в выпадающем списке.
            case R.id.settingsItem:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Метод, который вызывается, после окончания выполнения CreateNewTaskActivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Проверка на то, всё ли пошло по плану в CreateNewTaskActivity.
        if (resultCode == RESULT_OK) {
            //Получаем объект нового задания Task из Intent data.
            Task task = (Task) data.getSerializableExtra("usertask");
            //Добавляем новое задание в массив.
            taskList.add(task);
            //Оповещаем адаптер, что в массив данных поменялся.
            listAdapter.notifyDataSetChanged();
            //Переделываем данные в нужный формат для базы данных (ContentValue: Имя столбца -> Значение столбца).
            ContentValues contentValues = new ContentValues();
            contentValues.put("USERTEXT", task.getTaskText());
            //Добавляем новые задания в базу данных.
            database.insert(DATABASE_TABLE, null, contentValues);
        }
    }
}
