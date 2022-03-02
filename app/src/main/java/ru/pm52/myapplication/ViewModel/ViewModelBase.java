package ru.pm52.myapplication.ViewModel;

import android.os.Environment;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.pm52.myapplication.ICallbackResponse;
import ru.pm52.myapplication.INotify;
import ru.pm52.myapplication.Model.TaskModel;

public abstract class ViewModelBase extends ViewModel implements INotify {

    protected List<INotify> notifyList = new ArrayList<>();
    public static final String AUTHORITY = "ru.pm52.myapplication.MainActivity.provider";
    public static final File FILE_PATH_DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

    public ViewModelBase() {
        super();
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {

    }

    public void addListener(INotify objectNotify) {
        notifyList.add(objectNotify);
    }

    public void removeListener(INotify objectNotify) {
        if (notifyList.contains(objectNotify))
            notifyList.remove(objectNotify);
    }

    public void notifyObject() {
//        for (INotify iNotify : notifyList)
//            iNotify.NotifyResponse();
    }

}

