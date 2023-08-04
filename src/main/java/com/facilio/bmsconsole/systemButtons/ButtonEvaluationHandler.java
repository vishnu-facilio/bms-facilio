package com.facilio.bmsconsole.systemButtons;


import com.facilio.bmsconsole.context.WidgetGroupConfigContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.modules.SystemButtonValidationHandler;
import lombok.Getter;

public interface ButtonEvaluationHandler {

    boolean evaluateButtons(String moduleName,long recordId,String identifier) throws Exception;

    @Getter
    public enum  ButtonActionType implements FacilioStringEnum {
        systemButton("systemButton", new SystemButtonValidationHandler());


        String displayName;
        ButtonEvaluationHandler handler;
        ButtonActionType(String name,ButtonEvaluationHandler handler){
            this.displayName = name;
            this.handler = handler;
        }


    }
}
