package ru.pm52.myapplication.ViewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.pm52.myapplication.Model.AuthRepository;

public class Factory implements ViewModelProvider.Factory {

    @Nullable
    private Context context;

    public Factory(@Nullable Context context){
        this.context = context;
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
            return (T) new TaskViewModel(context);
        }
        return null;
    }
}
