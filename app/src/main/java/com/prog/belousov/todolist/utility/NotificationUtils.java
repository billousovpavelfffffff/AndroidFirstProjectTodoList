package com.prog.belousov.todolist.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Switch;

import com.prog.belousov.todolist.R;
import com.prog.belousov.todolist.TasksActivity;

public class NotificationUtils {

    //Константа, с Id для PendingIntent, используется для идентифицирования.
    private static final int PENDING_INTENT_ID = 39595;
    private static final String TODOLIST_NOTIFICATAION_CHANNEL_ID = "todolist_notification_channel";


    //Метод, ответственный за создание уведомления, и вывода его.
    public static void notifyUserAboutTask(Context context) {
        //Инициализируем NotificationManager, он управляет уведомлениями.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Теперь специально для Андроид версии 8 и выше...
        //Создаем канал уведомлений.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TODOLIST_NOTIFICATAION_CHANNEL_ID, "notification_channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        //Строим уведомление.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, TODOLIST_NOTIFICATAION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_stat_check)
                        .setContentTitle("There is my notification!")
                        .setContentText("And here your task that you should do.")
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);
        //Если версия Андроида ниже, чем 8, ставим приоритет тут (Если версия выше, приоритет уже выставлен в канале).
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        //Айди конкретного уведомления.
        int notificationId = 1;
        //Наконец отправляем уведомление с помощью менеджера.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    //Вспомогательный метод, которые возвращает Intent для перехода на главную активность, который будет использован при нажатии на уведомление.
    private static PendingIntent contentIntent(Context context) {
        //Создаём этот Intent.
        Intent startActivityIntent = new Intent(context, TasksActivity.class);
        //Оборачиваем наш Intent в PendingIntent(Чтобы сама система могла его запускать, обычный Intent она запустить не сможет).
        return PendingIntent.getActivity(context, PENDING_INTENT_ID, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
