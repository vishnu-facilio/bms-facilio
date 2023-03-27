package com.facilio.exception;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.i18n.util.ErrorsUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Getter @Setter
public class ErrorResponseUtil {
    private ErrorCode errorCode;
    private String message;
    private String correctiveAction;
    private JSONObject data;
    private Object arguments;

    private Boolean isVisible;
    private Boolean isErrorGeneralized;

    public   ErrorResponseUtil(RESTException exception) throws Exception {
        ResourceBundle bundle = ErrorsUtil.getBundle();
        StringSubstitutor sub = null;
        String correctiveAction = null;
        String exceptionMessage = null;
        if(exception.getArguments() != null){
            sub = new StringSubstitutor((Map<String, String>) exception.getArguments());
        }
        Boolean license = exception.getMessage().length() > 0 ? false :  AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ERROR_FRAMEWORK_GENERALIZATION);
//        license = true;
        if(license){
            try {
                correctiveAction = bundle.getString(exception.getKey() + ".correctiveAction");
            }catch (Exception e){
                correctiveAction = "";
            }
            try {
                exceptionMessage = bundle.getString(exception.getKey() + ".msg");
            }catch (Exception e){
                exceptionMessage = exception.getKey();
            }
        }
        this.setErrorCode(exception.getErrorCode());
        this.setMessage(license ?  exception.getArguments() != null ? sub.replace(exceptionMessage) : exceptionMessage : exception.getMessage());
        this.setCorrectiveAction(license ? exception.getArguments() != null ? sub.replace(correctiveAction) : correctiveAction : StringUtils.EMPTY);
        this.setData(exception.getData());
        this.setArguments(exception.getArguments());
        this.setIsErrorGeneralized(license);
        this.setIsVisible(exception.getIsVisible());


    }
}