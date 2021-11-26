package ru.pm52.myapplication.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.pm52.myapplication.Model.AuthRepository;

public class Factory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (AuthViewModel.class.equals(modelClass)) {
            return (T) new AuthViewModel(AuthRepository.getInstance());
        }
        return null;
    }
}
