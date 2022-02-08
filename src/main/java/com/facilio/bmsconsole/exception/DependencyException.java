package com.facilio.bmsconsole.exception;

import com.facilio.fw.FacilioException;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class DependencyException extends FacilioException {
    private ErrorCode errorCode;
    private String message;
    private JSONObject data;

    public DependencyException(ErrorCode errorCode, String message, JSONObject data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
}
