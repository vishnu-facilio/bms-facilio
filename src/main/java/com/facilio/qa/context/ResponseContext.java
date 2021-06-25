package com.facilio.qa.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import lombok.AllArgsConstructor;
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

    @JsonIgnore
    private ResponseStatus resStatus;

    @JsonIgnore
    private QAndAType qAndAType;

    private QAndATemplateContext template; // For internal use

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

    public QAndATemplateContext getTemplate() {
        return this instanceof DummyResponseContext ? template : getParent() == null ? template : getParent();
    }

    public abstract T getParent();
    public abstract void setParent(T template);

    // Scoring fields
    private Double fullScore, totalScore;
    private Float scorePercent;

    @AllArgsConstructor
    public enum ResponseStatus implements FacilioIntEnum {
        DISABLED("Disabled"),
        NOT_ANSWERED("Not Answered"),
        PARTIALLY_ANSWERED("Partially Answered"),
        COMPLETED("Completed")
        ;
    	
    	String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}

        public static ResponseStatus valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static class QAndATypeIdResolver extends FacilioEnumClassTypeIdResolverBase<ResponseContext> {

        @Override
        protected Class<? extends ResponseContext> getDefaultClassOnException() {
            return DummyResponseContext.class;
        }

        @Override
        protected Class<? extends ResponseContext> getSubClass(String index) {
            QAndAType typeEnum = QAndAType.valueOf(Integer.parseInt(index));
            return typeEnum == null ? DummyResponseContext.class : typeEnum.getResponseClass();
        }
    }
}
