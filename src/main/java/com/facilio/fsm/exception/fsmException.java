package com.facilio.fsm.exception;

import com.facilio.fw.FacilioException;

import javax.servlet.http.HttpServletResponse;

public class fsmException extends FacilioException {
    private ExceptionType exceptionType;
    private String message;

    private String[] messageParams;

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setMessageParams(String[] messageParams) {
        this.messageParams = messageParams;
    }
    public String[] getMessageParams() {
        return messageParams;
    }

    public fsmException() {
    }

    public fsmException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.message = exceptionType.getMessage();
    }

    public fsmException(ExceptionType exceptionType, String... messageParams) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.messageParams = messageParams;
    }

    public enum ExceptionType {
        ORG_MANDATORY_FIELDS_MISSING(HttpServletResponse.SC_BAD_REQUEST, "Mandatory fields for the organization are missing (Name, Domain)");

        private final int httpStatus;
        private final String message;

        ExceptionType(int httpStatus, String message) {
            this.httpStatus = httpStatus;
            this.message = message;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }
    }
}
