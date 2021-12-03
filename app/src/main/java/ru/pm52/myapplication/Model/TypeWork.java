package ru.pm52.myapplication.Model;

import com.google.gson.annotations.SerializedName;

public class TypeWork {
    @SerializedName("Ссылка")
    public final String Ref;

    @SerializedName("Наименование")
    public final String Name;

    public TypeWork(String ref, String name) {
        Ref = ref;
        Name = name;
    }
}
