package com.prog.belousov.todolist;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    //Константа, код для идентификации для startActivityForResult() для активити создания.
    private final static int REQUEST_CODE_CREATE = 1;
    //Константа, код для идентификации для startActivityForResult() для активити обновления.
    private final static int REQUEST_CODE_UPDATE = 2;
    //Сам список заданий.
    ListView listView;
    //Адаптер для ListVie, который принимает айди разметки, на этой разметке айди TextView, и массив заданий.
    ArrayAdapter<Task> listAdapter;
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
        listAdapter = new ArrayAdapter<>(this,
                R.layout.item_task, R.id.taskTextView, taskList);
        //Создаём слушателя нажатий на определнный элемент из списка.
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TasksActivity.this, UpdateTaskActivity.class);
                intent.putExtra("usertask", taskList.get(position));
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        };
        //Ставим слушателя на ListView.
        listView.setOnItemClickListener(itemClickListener);
        //Связываем адаптер с ListView.
        listView.setAdapter(listAdapter);
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
                listAdapter.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                //Через интерфейс помошника добавляем в БД новую запись.
                dataBaseHelper.addTask(task);
                //Добавляем новое задание в массив.
                taskList.add(task);
                //Оповещаем адаптер, что в массив данных поменялся.
                listAdapter.notifyDataSetChanged();

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
                listAdapter.notifyDataSetChanged();
            }
        }
    }
}
