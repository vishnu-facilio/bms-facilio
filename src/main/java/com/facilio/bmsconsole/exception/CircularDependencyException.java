package com.facilio.bmsconsole.exception;

import com.facilio.fw.FacilioException;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class CircularDependencyException extends FacilioException {

    private ErrorCode errorCode;
    private String message;
    private JSONObject data;

    public CircularDependencyException(String message){
        this.message = message;
    }
    public CircularDependencyException(String message,ErrorCode errorCode,JSONObject data){
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
}
