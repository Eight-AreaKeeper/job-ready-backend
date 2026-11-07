package com.jobready.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Business exception. GlobalExceptionHandler turns it into Result.error(msg)
 * with the given HTTP status.
 */
public class BaseException extends RuntimeException {

    private final HttpStatus status;

    public BaseException(HttpStatus status, String msg) {
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static BaseException badRequest(String msg) {
        return new BaseException(HttpStatus.BAD_REQUEST, msg);
    }

    public static BaseException unauthorized(String msg) {
        return new BaseException(HttpStatus.UNAUTHORIZED, msg);
    }

    public static BaseException forbidden(String msg) {
        return new BaseException(HttpStatus.FORBIDDEN, msg);
    }

    public static BaseException notFound(String what) {
        return new BaseException(HttpStatus.NOT_FOUND, what + " not found");
    }

    public static BaseException conflict(String msg) {
        return new BaseException(HttpStatus.CONFLICT, msg);
    }

    public static BaseException aiFailed(String msg) {
        return new BaseException(HttpStatus.BAD_GATEWAY, msg);
    }
}
