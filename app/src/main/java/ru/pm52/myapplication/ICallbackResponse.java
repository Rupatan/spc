package ru.pm52.myapplication;

public interface ICallbackResponse {

    public void CallbackResponse(String content, int responseCode) throws InterruptedException, Exception;

}