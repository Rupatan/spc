package ru.pm52.myapplication.Model;

import com.google.gson.annotations.SerializedName;

public class ServicePointModel {

    @SerializedName("ТочкаОбслуживания")
    public String ID;

    @SerializedName("ТочкаОбслуживанияПредставление")
    public String Name;

    @SerializedName("ТочкаОбслуживанияАдрес")
    public String Address;

}
