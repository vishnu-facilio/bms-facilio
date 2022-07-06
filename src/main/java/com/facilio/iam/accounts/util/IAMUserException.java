package com.facilio.iam.accounts.util;

import com.facilio.fw.FacilioException;

public class IAMUserException extends FacilioException {
    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

    public IAMUserException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode=errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public String toString() {
        return getMessage();
    }

    public static enum ErrorCode {
        USERNAME_HAS_WHITESPACE
        ;
    }
}
