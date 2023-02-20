package com.facilio.v3.action;

import com.facilio.v3.RESTAPIHandler;
import com.amazonaws.http.HttpMethodName;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PickListActionV3 extends RESTAPIHandler {

    private static final Logger LOGGER = Logger.getLogger(PickListActionV3.class.getName());
    private static final long serialVersionUID = 1L;
    protected V3Action.api currentApi(){
        return api.v3;
    }
    private String throwValidationException(String message){
        this.setMessage(message);
        this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
        LOGGER.log(Level.SEVERE,message);
        return ERROR;
    }
    @Override
    public String execute() throws Exception{
        if(getModuleName() == null){
            return throwValidationException("Module Name cannot be null");
        }
        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());
        switch (httpMethod){
            case GET:
                if(getViewName() != null){
                    return pickList();
                }
            default:
                return throwValidationException("Unsupported Method");
        }
    }
}
