package com.prog.belousov.todolist;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prog.belousov.todolist.utility.DataBaseHelper;
import com.prog.belousov.todolist.utility.NotificationUtils;

import static com.prog.belousov.todolist.CreateNewTaskActivity.getUserHours;
import static com.prog.belousov.todolist.CreateNewTaskActivity.getUserMinutes;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText editTask;
    EditText editExtra;
    Switch editNeedAlarm;
    EditText editTime;

    Task oldTask;
    Task newTask;

    int hourOfNotification;
    int minuteOfNotification;

    TimePickerDialog.OnTimeSetListener onTimeSetListener;

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

        //Ставим на него слушателя нажатий.
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNeedAlarm.isChecked()){
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
                            editTime.setText(strHours + ":"+strMinutes);
                        }
                    };
                    //Создаём диалоговое окно выбора времени, передаём контекст; что нужно сделать,
                    //после того, как пользователь выберет время; текущее время на усройстве и формат(24 или 12).
                    TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateTaskActivity.this,
                            onTimeSetListener,
                            hourOfNotification,
                            minuteOfNotification,
                            true);
                    //Выводим диалоговое окно методом show().
                    timePickerDialog.show();
                }
            }
        });
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
        else editTime.setHint(getUserHours(true)+ ":"+ getUserMinutes(true));
        //Перемещает курсор в конец для удобства.

        editTask.setSelection(editTask.getText().length());
        //Ставим слушателя изменения позиции рычажка в Switch-e.
        editNeedAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTime.setText(getUserHours(true)+":" + getUserMinutes(true));
                } else
                    editTime.setText("");
                editTime.setHint(getUserHours(true)+":" + getUserMinutes(true));
            }
        });


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
            //Создаём новый интент.
            Intent intent = new Intent();
            //Кладём старый обьект Task.
            intent.putExtra("useroldtask", oldTask);
            //Кладём в интент наш новый объект Task.
            intent.putExtra("usernewtask", newTask);
            //Устанавливаем результат выполнения нашей активности.
            setResult(RESULT_OK, intent);
            if(oldTask.getTimeOfAlarm() !=null){
                //И если в данном случае пользователь выключил Switch.
                if(!editNeedAlarm.isChecked()){
                    //Отменяем уведомление на обновленном задании.
                    NotificationUtils.cancelAlarm(this, oldTask.getId());
                    //Очищаем поле с временем, ставя в hint время с устройства.
                    editTime.setHint(getUserHours(true)+ ":"+ getUserMinutes(true));
                    //Если же Switch остался включенным, проверяем на сходсиво время, указанное в timeUpdateEditText.
                } else {
                    //Если время другое...
                    if(!oldTask.getTimeOfAlarm().equals(editTime.getText().toString())){
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
                        dataBaseHelper.updateTask(oldTask, newTask);
                        int id = dataBaseHelper.getTaskId(newTask);
                        //Ставим уведомление на новое время.
                        NotificationUtils.updateNotificationShedule(this, id, hourOfNotification, minuteOfNotification);
                        //Ставим в поле задания время.
                        newTask.setTimeOfAlarm(editTime.getText().toString());
                    }
                }
                //В случае, если на изменяемом задании не была установлено уведомление..
            } else {
                //Если рычажок во включенном положении..
                if(editNeedAlarm.isChecked()){
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
                    dataBaseHelper.updateTask(oldTask, newTask);
                    int id = dataBaseHelper.getTaskId(newTask);
                    //Ставим уведомление черезитерфейс.
                    NotificationUtils.setNotificationScheduler(this, id, hourOfNotification, minuteOfNotification);
                    //Ставим в поле задания время.
                    newTask.setTimeOfAlarm(editTime.getText().toString());
                }
            }
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            dataBaseHelper.updateTask(oldTask, newTask);
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
