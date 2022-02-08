package com.facilio.v3.exception;


import javax.servlet.http.HttpServletResponse;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(1, HttpServletResponse.SC_NOT_FOUND),
    UNHANDLED_EXCEPTION(2, HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(3, HttpServletResponse.SC_BAD_REQUEST),
    USER_ALREADY_EXISTS(4, HttpServletResponse.SC_BAD_REQUEST),
    DEPENDENCY_EXISTS(5, HttpServletResponse.SC_BAD_REQUEST),
    DELIVERY_PARSING_SUCCESS(101, HttpServletResponse.SC_OK),
    DELIVERY_PARSING_EMPLOYEE_NOT_FOUND(102, HttpServletResponse.SC_OK),
    DELIVERY_PARSING_FAILED(103, HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
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
