package ru.pm52.myapplication.Model;

import android.text.Editable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class TaskModel implements Serializable {

    @SerializedName("Ссылка")
    public String Id;

    @SerializedName("Наименование")
    public String Name;

    @SerializedName("Номер")
    public String Number;

    @SerializedName("Дата")
    public Date DateTime;

    @NonNull
    @SerializedName("Комментарий")
    public String Comment = "";

    @NonNull
    @SerializedName("Факт")
    public Double LeadTime = 0.0;

    @SerializedName("Исполнитель")
    public UserModel Executor;

    @SerializedName("Контрагент")
    public String Contragent;

    @SerializedName("КонтактноеЛицо")
    public String Contact;
}
