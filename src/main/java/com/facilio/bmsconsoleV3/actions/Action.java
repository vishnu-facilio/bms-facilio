package com.facilio.bmsconsoleV3.actions;

import com.amazonaws.http.HttpMethodName;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter @Setter
public class Action extends RESTAPIHandler {

    private static final Logger LOGGER = Logger.getLogger(Action.class.getName());

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

        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        switch (httpMethod) {
            case PATCH:
                switch (getActionType()) {
                    case FacilioConstants.Action.TRANSITION:
                        if (getStateTransitionId() == null || getStateTransitionId() <= 0) {
                            return throwValidationException("Invalid StateTransitionId");
                        }
                        return patch();

                    case FacilioConstants.Action.APPROVAL:
                        if (getApprovalTransitionId() == null || getApprovalTransitionId() <= 0) {
                            return throwValidationException("Invalid ApprovalTransitionId");
                        }
                        return patch();

                    case FacilioConstants.Action.CUSTOM_BUTTON:
                        if (getCustomButtonId() == null || getCustomButtonId() <= 0) {
                            return throwValidationException("Invalid CustomButtonId");
                        }
                        return patch();

                    default:
                        return throwValidationException("UnSupported ActionType");

                }
            default:
                return throwValidationException("Unsupported HTTP Method : " + httpMethod.name());

        }
    }
    private String actionType;
}
