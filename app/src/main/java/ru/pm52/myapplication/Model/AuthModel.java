package ru.pm52.myapplication.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AuthModel {

    @Nullable
    private String Login;

    @Nullable
    private String Password;

    @NonNull
    private String Server = "";

    @NonNull
    private String Base = "";

    public AuthModel(@Nullable String login, @Nullable String password) {
        Login = login;
        Password = password;
    }

    public AuthModel(){
        this(null, null);
    }

    public void setBase(String base) {
        Base = base;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getBase() {
        return Base;
    }

    public String getServer() {
        return Server;
    }

    public void setLogin(@Nullable String login) {
        Login = login;
    }

    public void setPassword(@Nullable String password) {
        Password = password;
    }

    @Nullable
    public String getLogin() {
        return Login;
    }

    @Nullable
    public String getPassword() {
        return Password;
    }
}
