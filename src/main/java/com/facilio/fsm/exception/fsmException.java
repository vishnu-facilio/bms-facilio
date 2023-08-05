package com.facilio.fsm.exception;

import com.facilio.fw.FacilioException;

import java.util.ArrayList;
import java.util.List;

public class FSMException extends FacilioException {
    private FSMErrorCode fsmErrorCode;
    private String message;

    private String[] messageParams;

    private List<FSMException> additionalExceptions;

    public FSMErrorCode getFsmErrorCode() {
        return fsmErrorCode;
    }

    public void setMessageParams(String[] messageParams) {
        this.messageParams = messageParams;
    }
    public String[] getMessageParams() {
        return messageParams;
    }

    public FSMException(FSMErrorCode fsmErrorCode) {
        super(fsmErrorCode.getMessage());
        this.fsmErrorCode = fsmErrorCode;
        this.message = fsmErrorCode.getMessage();
    }

    public FSMException(FSMErrorCode fsmErrorCode, String... messageParams) {
        super(fsmErrorCode.getMessage());
        this.fsmErrorCode = fsmErrorCode;
        this.message = fsmErrorCode.getMessage();
        this.messageParams = messageParams;
    }

    public FSMException addAdditionalException(FSMException fsmException) {
        if (fsmException != null) {
            if (additionalExceptions == null) {
                this.additionalExceptions = new ArrayList<>();
            }
            additionalExceptions.add(fsmException);
        }
        return this;
    }

    public List<FSMException> getAdditionalExceptions() {
        return additionalExceptions;
    }
}
