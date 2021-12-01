package ru.pm52.myapplication;

import androidx.annotation.Nullable;

import ru.pm52.myapplication.Model.TaskModel;

public interface Navigator {

    public void showDetails(TaskModel task, @Nullable INotify callback);

    public void goBack();

    public void toast(int messageRes);

}
