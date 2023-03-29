package com.facilio.v3.exception;

import com.facilio.fw.FacilioException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@Getter
@Setter
@AllArgsConstructor
public class RESTException extends FacilioException {
    private ErrorCode errorCode;
    private String message;
    private JSONObject data;

    public RESTException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = StringUtils.EMPTY;
    }

    public RESTException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
