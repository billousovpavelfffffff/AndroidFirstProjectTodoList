package com.prog.belousov.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    //Это класс, который умеет из содержимого layout-файла создать View-элемент.
    private LayoutInflater inflater;
    //Разметка, которую этот адаптер будет заполнять данными.
    private int layout;
    //Список заданий, который нужно отобразить.
    private List<Task> tasks;


    public TaskAdapter(@NonNull Context context, int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);

        layout = resource;
        tasks = objects;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Создаем объект View для каждого отдельного элемента в списке
        View view = inflater.inflate(layout, parent, false);
        //Связываем элементы из разметки с кодом.
        TextView usertextTextView = view.findViewById(R.id.taskTextView);
        CheckBox isDoneCheckBox = view.findViewById(R.id.checkBoxIsDone);
        TextView userExtraTextView = view.findViewById(R.id.extraTextView);
        //Берем конкретное задание из массива.
        Task task = tasks.get(position);
        //Отображаем пользовательский текст на TextView.
        usertextTextView.setText(task.getTaskText());
        String extra = task.getExtraText();
        //Если есть дополнительный текст - выводим его, если нет - прячем TextView для доп. текста.
        if(extra != null) userExtraTextView.setText(extra);
        else userExtraTextView.setVisibility(View.GONE);
        //Проверяем, сделано ли задание.
        if(!task.isDone()){
            //Задание не выполнено - не делаем с текстом никаких изменений, и CheckBox не отмечаем.
            return view;
        } else {
            //Задание выполнено - ...
            //Делаем текст в TextView перечёркнутым.
            usertextTextView.setPaintFlags(usertextTextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );
            userExtraTextView.setPaintFlags(userExtraTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //Отмечаем CheckBox.
            isDoneCheckBox.setChecked(!isDoneCheckBox.isChecked());
            return view;
        }

    }
}
