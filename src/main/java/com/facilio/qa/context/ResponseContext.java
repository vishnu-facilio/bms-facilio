package com.facilio.qa.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ResponseContext <T extends QAndATemplateContext> extends V3Context {
    private Integer totalAnswered;
    private ResponseStatus resStatus;
    private QAndAType qAndAType;

    public Integer getType() {
        return qAndAType == null ? null : qAndAType.getIndex();
    }
    public void setType(Integer type) {
        qAndAType = type == null ? null : QAndAType.valueOf(type);
    }

    public Integer getResponseStatus() {
        return resStatus == null ? null : resStatus.getIndex();
    }
    public void setResponseStatus(Integer responseStatus) {
        this.resStatus = responseStatus == null ? null : ResponseStatus.valueOf(responseStatus);
    }

    public abstract T getParent();
    public abstract void setParent(T template);

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
