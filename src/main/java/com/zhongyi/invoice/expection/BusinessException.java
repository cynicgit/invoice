package com.zhongyi.invoice.expection;

/**
 * Created by Juphoon on 2017/6/6.
 */
public class BusinessException extends RuntimeException {

    private int code = 0;

    private String message;


    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public BusinessException(String message) {
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
