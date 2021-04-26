package com.facilio.qa.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ResponseContext extends V3Context {
    private Integer totalAnswered;
    private ResponseStatus resStatus;

    public Integer getResponseStatus() {
        if (resStatus != null) {
            resStatus.getIndex();
        }
        return null;
    }
    public void setResponseStatus(Integer responseStatus) {
        this.resStatus = ResponseStatus.valueOf(responseStatus);
    }

    public abstract QAndATemplateContext getParent();

    public enum ResponseStatus implements FacilioEnum<ResponseStatus> {
        DISABLED,
        NOT_ANSWERED,
        PARTIALLY_ANSWERED,
        COMPLETED
        ;

        public static ResponseStatus valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
