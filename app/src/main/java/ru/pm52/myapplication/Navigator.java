package ru.pm52.myapplication;

import ru.pm52.myapplication.Model.TaskModel;

public interface Navigator {

    public void showDetails(TaskModel task);

    public void goBack();

    public void toast(int messageRes);

}
