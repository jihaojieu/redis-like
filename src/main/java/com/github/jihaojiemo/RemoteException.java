package com.github.jihaojiemo;

/**
 * Description: 专门用来捕获用户输入的Error类型的错误，而不是程序自己的错误
 * Author: admin
 * Create: 2019-08-09 15:59
 */
public class RemoteException extends Exception {

    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
