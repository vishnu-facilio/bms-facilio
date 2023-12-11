package com.facilio.qa.context;

import com.facilio.bmsconsole.context.ResourceContext;
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
    private QAndAType qAndAType;
    private Integer totalPages, totalQuestions, totalResponses;
    private Boolean isPublished;

    private List<PageContext> pages;

    public QAndATemplateContext (Long id) {
        super(id);
    }

    public Integer getType() {
        return qAndAType == null ? null : qAndAType.getIndex();
    }
    public void setType(Integer type) {
        qAndAType = type == null ? null : QAndAType.valueOf(type);
    }

    protected abstract R newResponseObject();
    protected abstract List<R> newResponseObjects(List<ResourceContext> resources) throws Exception;
    protected abstract void addDefaultPropsForResponse(R response); //Having as two methods so that props can be overridden

    public R constructResponse() {
        R response = newResponseObject();
		response.setName(name);
        response.setQAndAType(this.qAndAType);
        response.setParent(this);
        response.setResStatus(ResponseContext.ResponseStatus.NOT_ANSWERED);
        response.setTotalAnswered(0);
        addDefaultPropsForResponse(response);
        return response;
    }
    
    public List<R> constructResponses(List<ResourceContext> resources) throws Exception {
    	List<R> responses = newResponseObjects(resources);
    	if(responses != null) {
    		for(R response : responses) {
        		response.setName(name);
        		response.setQAndAType(this.qAndAType);
                response.setParent(this);
                response.setResStatus(ResponseContext.ResponseStatus.NOT_ANSWERED);
                response.setTotalAnswered(0);
                addDefaultPropsForResponse(response);
        	}
    	}
        return responses;
    }

    public static class QAndATypeIdResolver extends FacilioEnumClassTypeIdResolverBase<QAndATemplateContext> {

        @Override
        protected Class<? extends QAndATemplateContext> getDefaultClassOnException() {
            return DummyQAndATemplateContext.class;
        }

        @Override
        protected Class<? extends QAndATemplateContext> getSubClass(String index) {
            QAndAType typeEnum = QAndAType.valueOf(Integer.parseInt(index));
            return typeEnum == null ? DummyQAndATemplateContext.class : typeEnum.getTemplateClass();
        }
    }
}
