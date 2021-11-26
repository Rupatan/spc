package ru.pm52.myapplication.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TaskModel {

    @SerializedName("Ссылка")
    public String Id;

    @SerializedName("Наименование")
    public String Name;

    @SerializedName("Номер")
    public String Number;

    @SerializedName("Дата")
    public Date DateTime;

    @SerializedName("Комментарий")
    public String Comment;

    @SerializedName("Факт")
    public Double LeadTime;

    @SerializedName("Исполнитель")
    public Double Executor;

}
