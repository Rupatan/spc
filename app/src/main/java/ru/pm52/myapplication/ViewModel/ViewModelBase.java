package ru.pm52.myapplication.ViewModel;

import androidx.lifecycle.ViewModel;

import ru.pm52.myapplication.ICallbackResponse;
import ru.pm52.myapplication.INotify;
import ru.pm52.myapplication.Model.TaskModel;

public class ViewModelBase extends ViewModel implements INotify {

    @Override
    public void NotifyResponse(String eventString, Object... params) {

    }
}

