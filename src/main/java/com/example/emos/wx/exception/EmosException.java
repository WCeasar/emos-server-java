package com.example.emos.wx.exception;

import lombok.Data;

@Data
public class EmosException extends  RuntimeException {
    public int code = 500;
    public String message;


    public  EmosException(String message) {
        super(message);
        this.message = message;
    }

    public  EmosException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
    public  EmosException( String message, int code) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public  EmosException(String message, int code, Throwable e) {
        super(message, e);
        this.message = message;
        this.code = code;
    }
}
