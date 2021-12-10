package ru.pm52.myapplication;

import android.app.Application;
import android.content.Context;

import ru.pm52.myapplication.Model.UserModel;

public class App extends Application {

    private static App instance;
    private static UserModel User;

    public App() {
        instance = this;
    }

    public static void setUser(UserModel user){
        User = user;
    }

    public static UserModel getUser() {
        return User;
    }

    public static Context getContext() {
        return instance;
    }

}
