package com.zjgs.backend.common.exception;

public class MyException extends RuntimeException {
    // alt + insert => 重写方法的前五个异常方法
    protected MyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MyException(Throwable cause) {
        super(cause);
    }

    public MyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyException() {
        super();
    }

    public MyException(String message) {
        super(message);
    }
}
