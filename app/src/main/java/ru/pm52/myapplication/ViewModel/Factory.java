package ru.pm52.myapplication.ViewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Model.TaskRepository;

public class Factory implements ViewModelProvider.Factory {

    @Nullable
    private Context context;

    @Nullable
    private TaskModel taskModel;

    public Factory(@Nullable Context context){
        this(context, null);
    }

    public Factory(@Nullable Context context, @Nullable TaskModel taskModel){
        this.context = context;
        this.taskModel = taskModel;
    }
    public Factory(){
        this(null);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (AuthViewModel.class.equals(modelClass)) {
            return (T) new AuthViewModel(AuthRepository.getInstance());
        }else if (TaskViewModel.class.equals(modelClass)){
            return (T) new TaskViewModel(new TaskRepository(taskModel));
        }
        return null;
    }
}
