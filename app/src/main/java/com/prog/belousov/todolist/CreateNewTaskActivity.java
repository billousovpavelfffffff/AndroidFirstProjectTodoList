package com.prog.belousov.todolist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prog.belousov.todolist.utility.DataBaseHelper;
import com.prog.belousov.todolist.utility.NotificationUtils;

import java.util.Calendar;


public class CreateNewTaskActivity extends AppCompatActivity {


    EditText timeEdText;
    EditText editText;
    EditText extraInfEditText;
    Switch needAlarm;


    TimePickerDialog.OnTimeSetListener onTimeSetListener;
    int hourOfNotification;
    int minuteOfNotification;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        /** Это делает кнопку назад, слева от лейбла активности*/
        getSupportActionBar().setHomeButtonEnabled(true);
        //Связываем EditText - ы и Switch.
        editText = findViewById(R.id.taskText);
        extraInfEditText = findViewById(R.id.extraEditText);
        needAlarm = findViewById(R.id.switch1);
        timeEdText = findViewById(R.id.timeEditText);
        //Настраиваем EditText времени.
        timeEdText.setHint(getUserHours(true)+":" + getUserMinutes(true));
        timeEdText.setFocusable(false);
        timeEdText.setClickable(false);
        timeEdText.setLongClickable(false);
        //Ставим слушателя изменения позиции рычажка в Switch-e.
        needAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   timeEdText.setText(getUserHours(true)+":" + getUserMinutes(true));
               } else
                   timeEdText.setText("");
                   timeEdText.setHint(getUserHours(true)+":" + getUserMinutes(true));
            }
        });

        //Ставим на него слушателя нажатий.
        timeEdText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(needAlarm.isChecked()){
                    //Получаем текущий час от календаря.
                    hourOfNotification = Integer.valueOf(getUserHours(false));
                    //Получаем текущие минуты от календаря.
                    minuteOfNotification = Integer.valueOf(getUserMinutes(false));
                    //Инициализируем слушателя (Что сделать, когда пользователь выберет время).
                    onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            //Забираем у TimePicker-а время, выбранное пользоватем, и сохраняем в глобальные переменные.
                            hourOfNotification = hourOfDay;
                            minuteOfNotification = minute;
                            //Ставим обновлённые данные в EditText.
                            String strHours = String.format("%02d", hourOfNotification);
                            String strMinutes = String.format("%02d", minuteOfNotification);
                            timeEdText.setText(strHours + ":"+strMinutes);
                        }
                    };
                    //Создаём диалоговое окно выбора времени, передаём контекст; что нужно сделать,
                    //после того, как пользователь выберет время; текущее время на усройстве и формат(24 или 12).
                    TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNewTaskActivity.this,
                            onTimeSetListener,
                            hourOfNotification,
                            minuteOfNotification,
                            true);
                    //Выводим диалоговое окно методом show().
                    timePickerDialog.show();
                }
            }
        });

    }

    public void addNewTask(View view) {
        //Получаем пользовательский текст из нашего EditText.
        String userText = editText.getText().toString().trim();
        String extraInf = extraInfEditText.getText().toString().trim();
        String userTime = timeEdText.getText().toString();
        //Проверка на пустой ввод.
        if (!userText.equals("") && !extraInf.equals("")) {
            //Создаем новое задание с текстом пользователя.
            Task task = new Task(userText, false);
            if (!extraInf.equals("")) task.setExtraText(extraInf);
            if(!userTime.equals("")) task.setTimeOfAlarm(userTime);
            //Создаём новый интент.
            Intent intent = new Intent();
            //Кладём в интент наш объект Task.
            intent.putExtra("usertask", task);
            //Через интерфейс помошника добавляем в БД новую запись.
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.addTask(task);
            //Устанавливаем результат выполнения нашей активности.
            setResult(RESULT_OK, intent);
            //Ставим уведомление.
            if(needAlarm.isChecked()) {
                int id = dataBaseHelper.getTaskId(task);
                NotificationUtils.setNotificationScheduler(this, id, hourOfNotification, minuteOfNotification);
            }
            //Интересный метод, просто завершает данную активность, и приложение возвращается на главную активность!
            finish();
        } else
            //Если ввод пустой, просим пользователя ввести его задание.
            Toast.makeText(this, "Please write your task.", Toast.LENGTH_SHORT).show();
    }

    //Метод, который обрабатывает нажатие на кнопу назад в левом верхнем углу.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else return super.onOptionsItemSelected(item);
    }
    //Метод, который возвращает часы, которые в данный момент на устройстве.
    public static String getUserHours(boolean forString){
        //Создаём обьект Calendar, чтобы взять текущее время на устройстве.
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        String strHours = String.valueOf(hours);
        if(forString){
            if(hours>=0 && hours<10){
                strHours = "0"+strHours;
            }
        }
        return strHours;
    }
    //Метод, который возвращает минуты, которые в данный момент на устройстве.
    public static String getUserMinutes(boolean forString){
        //Создаём обьект Calendar, чтобы взять текущее время на устройстве.
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        String strMinute = String.valueOf(minute);
        if(forString) {
           if(minute>=0 && minute<10){
                strMinute = "0"+strMinute;
           }
        }
        return strMinute;
         }
}

