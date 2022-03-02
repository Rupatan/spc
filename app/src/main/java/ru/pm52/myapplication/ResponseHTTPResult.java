package ru.pm52.myapplication;

public class ResponseHTTPResult {
    public final int Status;
    public final String Info;
    public final String Body;
    public final String Event;


    public ResponseHTTPResult(int status, String info, String body, String event) {
        Status = status;
        Info = info;
        Body = body;
        Event = event;
    }
}
