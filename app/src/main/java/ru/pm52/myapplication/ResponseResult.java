package ru.pm52.myapplication;

public class ResponseResult {
    public final String Body;
    public final int Code;

    public ResponseResult(String body, int code) {
        Body = body;
        Code = code;
    }
}