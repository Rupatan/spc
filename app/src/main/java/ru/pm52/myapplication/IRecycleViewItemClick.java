package ru.pm52.myapplication;


import android.view.View;

import ru.pm52.myapplication.Model.TaskModel;

public interface IRecycleViewItemClick{

    public void onItemClick(TaskModel model, View view);
}