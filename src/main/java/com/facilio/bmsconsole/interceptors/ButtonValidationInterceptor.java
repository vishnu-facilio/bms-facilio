package com.facilio.bmsconsole.interceptors;

import com.facilio.bmsconsole.systemButtons.ButtonEvaluationHandler;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.dispatcher.Parameter;

@Log4j
public class ButtonValidationInterceptor extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        try{

            Parameter parameter = ActionContext.getContext().getParameters().get("buttonPermission");
            if (parameter != null &&  parameter.getValue() != null && parameter.getValue().equals("true")) {
                Parameter moduleParam = ActionContext.getContext().getParameters().get("moduleName");
                Parameter recordIdParam = ActionContext.getContext().getParameters().get("recordId");
                Parameter actions = ActionContext.getContext().getParameters().get("buttonAction");

                String moduleName = moduleParam != null ? moduleParam.getValue() : null;
                long recordId = recordIdParam != null ? Long.parseLong(recordIdParam.getValue()) : -1;

                if (actions != null) {
                    String actionValue = actions.getValue();
                    if (actionValue != null) {
                        ButtonEvaluationHandler.ButtonActionType buttonTypeEnum = ButtonEvaluationHandler.ButtonActionType.valueOf(actionValue);
                        String identifier = null;
                        if (buttonTypeEnum.equals(ButtonEvaluationHandler.ButtonActionType.systemButton)) {
                            Parameter identifierParam = ActionContext.getContext().getParameters().get("identifier");
                            identifier = identifierParam != null ? identifierParam.getValue() : null;
                        }
                        ButtonEvaluationHandler handler = buttonTypeEnum.getHandler();
                        boolean evaluate = handler.evaluateButtons(moduleName, recordId, identifier);
                        if (!evaluate) {
                            return ErrorUtil.sendError(ErrorUtil.Error.BUTTON_VALIDATION);
                        }
                    }

                }
            }

        }catch (Exception e){
            LOGGER.error("Error at Button Validation Interceptor",e);
        }

        return actionInvocation.invoke();
    }
}
