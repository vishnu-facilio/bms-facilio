package com.facilio.v3.exception;

import com.facilio.fw.FacilioException;

public class RESTException extends FacilioException {
    private ErrorCode errorCode;
    private String message;

    public RESTException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = "";
    }

    public RESTException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
