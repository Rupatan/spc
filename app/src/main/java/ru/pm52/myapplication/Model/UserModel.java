package ru.pm52.myapplication.Model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("Ссылка")
    public String Id;

    @SerializedName("Наименование")
    public String Name;

    @SerializedName("ЭтоПрограммист")
    public boolean IsProgrammist;

}
