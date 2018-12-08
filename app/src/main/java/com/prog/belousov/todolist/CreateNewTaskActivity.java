package com.prog.belousov.todolist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CreateNewTaskActivity extends AppCompatActivity {

    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        /** Это делает кнопку назад, слева от лейбла активности*/
        getSupportActionBar().setHomeButtonEnabled(true);
        //Связываем EditText.
        editText = findViewById(R.id.taskText);
    }

    public void addNewTask(View view) {
        //Получаем пользовательский текст из нашего EditText.
        String userText = editText.getText().toString();
        //Проверка на пустой ввод.
        if (!userText.equals("")) {
            //Создаем новое задание с текстом пользователя.
            Task task = new Task(userText, false);
            //Создаём новый интент.
            Intent intent = new Intent();
            //Кладём в интент наш объект Task.
            intent.putExtra("usertask", task);
            //Устанавливаем результат выполнения нашей активности.
            setResult(RESULT_OK, intent);
            //Интересный метод, просто завершает данную активность, и приложение возвращается на главную активность!
            finish();
        } else
            //Если ввод пустой, просим пользователя ввести его задание.
            Toast.makeText(this, "Please write your task.", Toast.LENGTH_SHORT).show();
    }

    //Метод, который обрабатывает нажатие на кнопу назад в левом верхнем углу.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }
}
