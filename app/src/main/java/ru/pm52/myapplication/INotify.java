package ru.pm52.myapplication;

public interface INotify {

    public void NotifyResponse(String eventString, Object...params) throws Exception;

}
