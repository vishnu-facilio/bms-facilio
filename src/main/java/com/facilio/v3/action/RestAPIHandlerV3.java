package com.facilio.v3.action;

import com.amazonaws.http.HttpMethodName;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestAPIHandlerV3 extends RESTAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(RestAPIHandlerV3.class.getName());

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

        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        switch (httpMethod) {

            case POST:
                if (getData() != null) {
                    return create();
                } else {
                    return throwValidationException("Data Object cannot be empty");
                }
            case GET:
                if (getId() > 0 && (StringUtils.isNotEmpty(getType()) && getType().equals("glimpse"))) {
                    return glimpse();
                }
                else if (getId() > 0) {
                    return summary();
                }
                else if(getViewName()!=null && (getType()!=null && getType().equals("count"))) {
                    return count();
                }
                else{
                    return list();
                }

            case PATCH:
                if(getId()>0){
                    return patch();
                }
                else {
                    return throwValidationException("Record ID cannot be empty during PATCH");
                }
            case DELETE:
                if (getId() > 0) {
                        ArrayList data=new ArrayList();
                        data.add(getId());
                        setData(getModuleName(),data);
                    return delete();
                } else {
                    return throwValidationException("Record ID cannot be empty during DELETE");
                }
            default:
                return throwValidationException("Unsuported HTTP Method : " + httpMethod.name());
        }
    }
    @Getter @Setter
    private String type;
}


