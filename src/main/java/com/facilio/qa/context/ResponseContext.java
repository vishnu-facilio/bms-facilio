package com.facilio.qa.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        defaultImpl = DummyResponseContext.class,
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonTypeIdResolver(ResponseContext.QAndATypeIdResolver.class)
@NoArgsConstructor
public abstract class ResponseContext <T extends QAndATemplateContext> extends V3Context {

    /**
     *
     * Copy the following to all classes extending this class
     * @JsonTypeInfo(
     *         use = JsonTypeInfo.Id.NONE
     * )
     *
     */

    public ResponseContext (Long id) {
        super(id);
    }

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

    public static class QAndATypeIdResolver extends FacilioEnumClassTypeIdResolverBase<ResponseContext> {
        @Override
        protected Class<? extends ResponseContext> getSubClass(int index) {
            QAndAType typeEnum = QAndAType.valueOf(index);
            return typeEnum == null ? DummyResponseContext.class : typeEnum.getResponseClass();
        }
    }
}
