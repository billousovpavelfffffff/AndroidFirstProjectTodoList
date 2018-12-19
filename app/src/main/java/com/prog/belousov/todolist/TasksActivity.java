package com.prog.belousov.todolist;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.prog.belousov.todolist.utility.DataBaseHelper;
import com.prog.belousov.todolist.utility.TaskAdapter;

import java.util.ArrayList;
import java.util.Iterator;

public class TasksActivity extends AppCompatActivity {

    //Константа, код для идентификации для startActivityForResult() для активити создания.
    private final static int REQUEST_CODE_CREATE = 1;
    //Константа, код для идентификации для startActivityForResult() для активити обновления.
    private final static int REQUEST_CODE_UPDATE = 2;
    //Сам список заданий.
    ListView listView;
    //Адаптер для ListVie, который принимает айди разметки, на этой разметке айди TextView, и массив заданий.
    TaskAdapter taskAdapter;
    //Наш массив заданий пользователя.
    ArrayList<Task> taskList;
    //Помошник для работы с БД.
    DataBaseHelper dataBaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        //Сцепляем ListView из разметки к ListView в коде.
        listView = findViewById(R.id.listView);
        //Регистрируем ListView, говоря что он поддерживает контекстное меню.
        registerForContextMenu(listView);
        //Создаём экземпляр помошника, через которого работаем с базой данных.
        dataBaseHelper = new DataBaseHelper(this);
        //Иницилизация массива.
        taskList = new ArrayList<>();
        initList();
    }

    //Метод иницилизации ListView.
    void initList() {
        //Инициализируем наш массив, заполняя его из базы данных через интерфейс.
        taskList = dataBaseHelper.getAllTasks();
        //Инициализируем адаптер, в качестве параметров: Контекст, файл разметки с TextView, и айди конкретного TextView, куда он будет вставлять текст.
        taskAdapter = new TaskAdapter(this, R.layout.item_task, taskList);
        //Создаём слушателя нажатий на определнный элемент из списка.
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Получаем задание, на который нажали.
                Task task = taskList.get(position);
                if (!task.isDone()) {
                    //Если он не отмечен сделанным, отмечаем его.
                    task.setDone(true);
                    taskAdapter.notifyDataSetChanged();
                    //Записывем изменение в БД.
                    dataBaseHelper.updateTaskToDone(task);
                } else {
                    //Если же отмечен сделанным, то убераем отметку.
                    task.setDone(false);
                    taskAdapter.notifyDataSetChanged();
                    //Записывам изменение в БД.
                    dataBaseHelper.updateTaskToUndone(task);
                }
            }
        };
        //Ставим слушателя на ListView.
        listView.setOnItemClickListener(itemClickListener);
        //Связываем адаптер с ListView.
        listView.setAdapter(taskAdapter);
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
                startActivityForResult(intent, REQUEST_CODE_CREATE);
                return true;
            //При нажатии на Settings в выпадающем списке.
            case R.id.settingsItem:
                return true;
            //При нажатии на Delete all в выпадающем списке.
            case R.id.deleteAllItem:
                //Через интерфейс удаляем все записи из базы данных.
                dataBaseHelper.deleteAllTasks();
                //Удаляем всё записи из адаптера.
                taskAdapter.clear();
                return true;
            case R.id.deleteDoneTaskas:
                //Через интерфейс удаляем все отмеченные задания из БД.
                dataBaseHelper.deleteAllDoneTasks();
                //Проверка на нулевой размер массива.
                if (taskList.size() != 0) {
                    //Получаем интератор.
                    Iterator iterator = taskList.iterator();
                    do{
                        //Если задание помеченно как сделанное - итератор это удаляет.
                         Task task = (Task) iterator.next();
                         if(task.isDone()) iterator.remove();

                    } while (iterator.hasNext());
                    //Оповещаем адаптер про изменение.
                    taskAdapter.notifyDataSetChanged();
                }
                else {
                    showToastMessage("Nothing to delete!");
                }
                return true;
            case R.id.fakeTasks:
                for(int i = 0; i<30; i++) {
                    //Получаем объект нового задания Task из Intent data.
                    Task task = new Task("Какое-то задание обычного размера " + i, false);
                    //Через интерфейс помошника добавляем в БД новую запись.
                    dataBaseHelper.addTask(task);
                    //Добавляем новое задание в массив.
                    taskList.add(task);
                    //Оповещаем адаптер, что в массив данных поменялся.
                    taskAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Создание контекстного меню (Список, появляющийся при долгом нажатии на элемент списка ListView).
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //Этот класс преобразует XML файл в меню.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    //Описывает поведение при выборе определенного варианта в появляющемся списке контекстного меню.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Содержит информацию о элементе, на котором было вызвано контекстное меню.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deleteItem:
                //info.position - позиция жлемента, на котором было выхвано контекстное меню.
                dataBaseHelper.deleteTask(taskList.get(info.position));
                //Удаляем из массива это задание.
                taskList.remove(info.position);
                //Оповещаем адаптер про изменение в массиве.
                taskAdapter.notifyDataSetChanged();
                return true;
            case R.id.editItem:
                Intent intent = new Intent(TasksActivity.this, UpdateTaskActivity.class);
                intent.putExtra("usertask", taskList.get(info.position));
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            default:
                return super.onContextItemSelected(item);
        }


    }

    //Метод, который вызывается, после окончания выполнения CreateNewTaskActivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Проверка на то, всё ли пошло по плану в активности, которую мы запускали для резулльтата.
        if (resultCode == RESULT_OK) {
            //Проверяем, от кого получили результат. Результат от CreateNewTaskActivity.
            if (requestCode == REQUEST_CODE_CREATE) {
                //Получаем объект нового задания Task из Intent data.
                Task task = (Task) data.getSerializableExtra("usertask");
                //Добавляем новое задание в массив.
                taskList.add(task);
                //Оповещаем адаптер, что в массив данных поменялся.
                taskAdapter.notifyDataSetChanged();

            }
            //Результат от UpdateTaskActivity.
            else if (requestCode == REQUEST_CODE_UPDATE) {
                //Берём изменяемый старый обьект Task.
                Task oldTask = (Task) data.getSerializableExtra("useroldtask");
                //Измененный обьект Task, который нужно занести в базу данных вместо старого.
                Task newTask = (Task) data.getSerializableExtra("usernewtask");
                //Через интерфейс меняем задание в базе данных.
                dataBaseHelper.updateTask(oldTask, newTask);
                //Находим нужный нам индекс.
                int index = taskList.indexOf(oldTask);
                //Удаляем обьект с этого индекса.
                taskList.remove(index);
                //Добавляем новое задание на тот индекс.
                taskList.add(index, newTask);
                //Оповещаем адаптер, что в массив данных поменялся.
                taskAdapter.notifyDataSetChanged();
            }
        }
    }

    //Метод, упрощающий вывод тоста.
    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
