package com.prog.belousov.todolist;


import java.io.Serializable;


public class Task implements Serializable {

    //Основной текст, заголовок.
    private String taskText;
    //Флаг, отмечающий выполненное задание.
    private boolean isDone;



    //Дополниетльный текст заданя.
    private String extraText;

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Task(){}

    public Task(String taskText, boolean isDone) {
        this.taskText = taskText;
        this.isDone = isDone;
    }

    public Task(String taskText, boolean isDone, String extraText){
        this.taskText = taskText;
        this.isDone = isDone;
        this.extraText = extraText;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        if (!taskText.equals(""))
            this.taskText = taskText;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    @Override
    public String toString() {
        return ""+taskText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskText == null) ? 0 : taskText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        if (taskText == null) {
            if (other.taskText != null)
                return false;
        } else if (!taskText.equals(other.taskText))
            return false;
        return true;
    }
}
