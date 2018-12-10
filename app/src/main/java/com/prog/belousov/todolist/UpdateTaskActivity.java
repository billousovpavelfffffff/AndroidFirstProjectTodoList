package com.prog.belousov.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText editTask;
    EditText editExtra;
    Task oldTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        //Это делает кнопку назад, слева от лейбла активности.
        getSupportActionBar().setHomeButtonEnabled(true);
        editTask = findViewById(R.id.editTask);
        editExtra = findViewById(R.id.editExtra);
        //Получаем старое задание, которое нужно отредактировать.
        oldTask = (Task) getIntent().getSerializableExtra("usertask");
        //Ставим в EditText текст старого задания.
        editTask.setText(oldTask.getTaskText());
        String extra = oldTask.getExtraText();
        if(extra !=null) editExtra.setText(extra);
        //Перемещает курсор в конец для удобства.
        editTask.setSelection(editTask.getText().length());

    }

    //Метод, который вызывается при нажатии кнопки Submit.
    public void submitChanges(View view){
        //Получаем пользовательский текст из нашего EditText.
        String userText = editTask.getText().toString().trim();
        String userExtra = editExtra.getText().toString().trim();
        //Проверка на пустой ввод.
        if (!userText.equals("")) {
            //Создаем обновлённое задание с текстом пользователя.
            Task newTask = new Task(userText, oldTask.isDone());
            if(!userExtra.equals("")) newTask.setExtraText(userExtra);
            //Создаём новый интент.
            Intent intent = new Intent();
            //Кладём старый обьект Task.
            intent.putExtra("useroldtask", oldTask);
            //Кладём в интент наш новый объект Task.
            intent.putExtra("usernewtask", newTask);
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
