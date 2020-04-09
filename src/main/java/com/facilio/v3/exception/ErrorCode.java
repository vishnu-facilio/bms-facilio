package com.facilio.v3.exception;


import javax.servlet.http.HttpServletResponse;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(1, HttpServletResponse.SC_NOT_FOUND),
    UNHANDLED_EXCEPTION(2, HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(3, HttpServletResponse.SC_BAD_REQUEST)
    ;

    private int code;
    private int httpStatus;

    ErrorCode(int code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int errorCode) {
        this.code = errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
