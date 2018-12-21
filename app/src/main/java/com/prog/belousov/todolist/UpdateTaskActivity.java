package com.prog.belousov.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText editTask;
    EditText editExtra;
    Switch editNeedAlarm;
    EditText editTime;

    Task oldTask;
    Task newTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        //Это делает кнопку назад, слева от лейбла активности.
        getSupportActionBar().setHomeButtonEnabled(true);
        //Связываем все наши EditText-ы.
        editTask = findViewById(R.id.editTask);
        editExtra = findViewById(R.id.editExtra);
        editTime = findViewById(R.id.timeUpdateEditText);
        editNeedAlarm = findViewById(R.id.switch2);
        //Получаем старое задание, которое нужно отредактировать.
        oldTask = (Task) getIntent().getSerializableExtra("usertask");
        //Ставим в EditText текст старого задания.
        editTask.setText(oldTask.getTaskText());
        String extra = oldTask.getExtraText();
        if(extra !=null){
            editExtra.setText(extra);
            //Перемещает курсор в конец для удобства.
            editExtra.setSelection(editExtra.getText().length());
        }
        String time = oldTask.getTimeOfAlarm();
        if(time !=null) {
            editTime.setText(time);
            editNeedAlarm.setChecked(true);
        }
        else editTime.setText(CreateNewTaskActivity.getUserHours(true)+ ":"+ CreateNewTaskActivity.getUserMinutes(true));
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
            newTask = new Task(userText, oldTask.isDone());
            if(!userExtra.equals("")) newTask.setExtraText(userExtra);
            //Берём из timeUpdateEditText время, и проверяем.
            //Если у задания до этого было установлено уведомление.
            if(!oldTask.getTimeOfAlarm().equals("")){
                //И если в данном случае пользователь выключил Switch.
                if(!editNeedAlarm.isChecked()){
                    //Отменяем уведомление на обновленном задании.
                    //TODO: Сделать возможность отмены уведомления на коккретном задании.
                   cancelNotification();
                //Если же Switch остался включенным, проверяем на сходсиво время, указанное в timeUpdateEditText.
                } else {
                    //Если время другое...
                    if(!oldTask.getTimeOfAlarm().equals(editTime.getText().toString())){
                        //TODO: Сделать обновление времени уведомления.
                    }
                }
            }
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

    private void cancelNotification() {
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
