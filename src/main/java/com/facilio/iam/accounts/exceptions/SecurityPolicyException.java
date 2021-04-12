package com.facilio.iam.accounts.exceptions;

import com.facilio.fw.FacilioException;

import java.util.List;

public class SecurityPolicyException extends FacilioException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public enum ErrorCode {
        MIN_LENGTH_VIOLATION,
        MIN_NUM_DIGIT_VIOLATION,
        MIN_UPPER_CASE_VIOLATION,
        MIN_SPL_CHARS_VIOLATION,
        PREV_PWD_VIOLATION,
        WEB_SESSION_EXPIRY
    }

    private ErrorCode errorCode;

    public SecurityPolicyException(SecurityPolicyException.ErrorCode errorCode, String message){
        super(message);
        this.errorCode=errorCode;
    }

    public SecurityPolicyException.ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return super.getMessage();
    }

    public String toString() {
        return getMessage();
    }
}
