package com.prog.belousov.todolist.utility;

import android.app.AlarmManager;
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
import com.prog.belousov.todolist.Task;
import com.prog.belousov.todolist.TasksActivity;

import java.util.Calendar;

public class NotificationUtils {

    //Константа, с Id для PendingIntent, используется для идентифицирования.
    private static final int PENDING_INTENT_ID = 39595;
    private static final String TODOLIST_NOTIFICATAION_CHANNEL_ID = "todolist_notification_channel";
    static AlarmManager alarmManager;


    public static void setNotificationScheduler(Context context,  int taskId, int hours, int minutes){
        //Инициализируем AlarmManager, именно он отправляет Intent в нужное время.
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent, который нужно отправить в заданное время.
        Intent intent = new Intent(context, NotificationReceiver.class);
        //Кладем дополнительно ID задания, о котором нужно напомнить.
        intent.putExtra("id", taskId);
        //Оборачиваем Intent в PendingIntent, чтобы его могла кинуть сама система.
        //Идентифицируем PendingIntent тем же айди, что и задание, для которого он создаётся.
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Устанавливаем время, в которое AlarmManager должен прислать Intent.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        //Устанавливаем AlarmManager.
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }

    public static void cancelAlarm(Context context, int taskID){
        //Инициализируем AlarmManager, именно он отправляет Intent в нужное время.
        alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(context, taskID, new Intent(context, NotificationReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));

    }

    public static void updateNotificationShedule(Context context, int taskId, int hours, int minutes){
        //Инициализируем AlarmManager, именно он отправляет Intent в нужное время.
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent, который нужно отправить в заданное время.
        Intent intent = new Intent(context, NotificationReceiver.class);
        //Кладем дополнительно ID задания, о котором нужно напомнить.
        intent.putExtra("id", taskId);
        //Оборачиваем Intent в PendingIntent, чтобы его могла кинуть сама система.
        //Идентифицируем PendingIntent тем же айди, что и задание, для которого он создаётся.
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Устанавливаем время, в которое AlarmManager должен прислать Intent.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        //Устанавливаем AlarmManager.
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }



    //Метод, ответственный за создание уведомления, и вывода его.
    public static void notifyUserAboutTask(Context context, Task task) {
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
                        .setContentTitle(task.getTaskText())
                        .setContentText(task.getExtraText())
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
