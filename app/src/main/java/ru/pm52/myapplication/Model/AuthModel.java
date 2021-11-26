package ru.pm52.myapplication.Model;

import androidx.annotation.Nullable;

public class AuthModel {

    @Nullable
    private String Login;

    @Nullable
    private String Password;

    public AuthModel(@Nullable String login, @Nullable String password) {
        Login = login;
        Password = password;
    }

    public AuthModel(){
        this(null, null);
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
