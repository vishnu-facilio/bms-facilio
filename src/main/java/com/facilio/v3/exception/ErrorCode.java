package com.facilio.v3.exception;


public enum ErrorCode {
    RESOURCE_NOT_FOUND(1, 404),
    UNHANDLED_EXCEPTION(2, 500)
    ;

    private int errorCode;
    private int httpCode;

    ErrorCode(int errorCode, int httpCode) {
        this.errorCode = errorCode;
        this.httpCode = httpCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
