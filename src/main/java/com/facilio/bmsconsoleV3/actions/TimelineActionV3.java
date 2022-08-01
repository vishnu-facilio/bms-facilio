package com.facilio.bmsconsoleV3.actions;

import com.amazonaws.http.HttpMethodName;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TimelineActionV3 extends TimelineAction{

    @Getter @Setter
    private String methodType;

    private static final Logger LOGGER = Logger.getLogger(TimelineActionV3.class.getName());

    private static final long serialVersionUID = 1L;


    protected V3Action.api currentApi() {
        return api.v3;
    }

    private String throwValidationException(String message) {

        this.setMessage(message);
        this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
        LOGGER.log(Level.SEVERE, message);
        return ERROR;
    }

    @Override
    public String execute() throws Exception {

        if (getModuleName() == null) {
            return throwValidationException("Module Name cannot be null");
        }
        if(getViewName()==null){
            return throwValidationException("View Name cannot be null");
        }

        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        switch (httpMethod) {

            case GET:
                if(getMethodType().equals("list")){
                    return list(false);
                }
                else if(getMethodType().equals("unscheduled")){
                    return list(true);
                }
                else if(getMethodType().equals("data")){
                    return timelineData();
                }
            case PATCH:
                if(getId()>0){
                    return timelinePatch();
                }
                else {
                    return throwValidationException("Record ID cannot be empty during PATCH");
                }
            default:
                return throwValidationException("Unsuported HTTP Method : " + httpMethod.name());
        }
    }
}


