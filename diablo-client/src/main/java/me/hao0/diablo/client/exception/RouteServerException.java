package me.hao0.diablo.client.exception;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class RouteServerException extends RuntimeException {

    public RouteServerException() {
    }

    public RouteServerException(String message) {
        super(message);
    }

    public RouteServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteServerException(Throwable cause) {
        super(cause);
    }

    public RouteServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
