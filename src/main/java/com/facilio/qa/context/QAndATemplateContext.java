package com.facilio.qa.context;

import com.facilio.util.FacilioEnumClassTypeIdResolverBase;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonTypeInfo(
        defaultImpl = DummyQAndATemplateContext.class,
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonTypeIdResolver(QAndATemplateContext.QAndATypeIdResolver.class)
public abstract class QAndATemplateContext <R extends ResponseContext> extends V3Context {

    /**
     *
     * Copy the following to all classes extending this class
     * @JsonTypeInfo(
     *         use = JsonTypeInfo.Id.NONE
     * )
     *
     */

    private String name;
    private String description;
    private QAndAType typeEnum;
    private Long responseModuleId;

    private List<PageContext> pages;

    public QAndATemplateContext (Long id) {
        super(id);
    }

    public Integer getType() {
        return typeEnum == null ? null : typeEnum.getIndex();
    }
    public void setType(Integer type) {
        typeEnum = type == null ? null : QAndAType.valueOf(type);
    }

    protected abstract R constructNewResponse();
    protected abstract void addDefaultProps(R response); //Having as two methods so that props can be overridden

    public R constructResponse() {
        R response = constructNewResponse();
        response.setParent(this);
        response.setResStatus(ResponseContext.ResponseStatus.NOT_ANSWERED);
        addDefaultProps(response);
        return response;
    }

    public static class QAndATypeIdResolver extends FacilioEnumClassTypeIdResolverBase<QAndATemplateContext> {
        @Override
        protected Class<? extends QAndATemplateContext> getSubClass(int index) {
            QAndAType typeEnum = QAndAType.valueOf(index);
            return typeEnum == null ? DummyQAndATemplateContext.class : typeEnum.getSubClass();
        }
    }
}
