package ru.pm52.myapplication.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ru.pm52.myapplication.Model.TaskModel;

public class TaskViewModel extends ViewModelBase {

    private MutableLiveData<TaskModel> task = new MutableLiveData<>();
    public LiveData<TaskModel> Task = task;



}
