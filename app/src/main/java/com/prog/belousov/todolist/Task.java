package com.prog.belousov.todolist;


import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {

    private String taskText;

    public Task(String taskText) {
        this.taskText = taskText;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        if (!taskText.equals(""))
            this.taskText = taskText;
    }

    @Override
    public String toString() {
        return ""+taskText;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  Task){
            if (((Task) obj).taskText.equals(taskText)) {
                return true;
            } else return false;
        } else return false;
    }
}
