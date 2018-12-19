package com.prog.belousov.todolist.utility;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prog.belousov.todolist.Task;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 1);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        NotificationUtils.notifyUserAboutTask(context, dataBaseHelper.getTaskById(id));
    }
}
