package com.facilio.qa.context;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.struts2.json.annotations.JSON;

public class DummyResponseContext extends ResponseContext {
    @Override
    @JsonIgnore
    @JSON(serialize = false)
    public QAndATemplateContext getParent() {
        throw new UnsupportedOperationException("DummyResponseContext is used only for POJO handling. Shouldn't be used in business logic");
    }

    @Override
    @JsonIgnore
    @JSON(serialize = false)
    public void setParent(QAndATemplateContext template) {
        throw new UnsupportedOperationException("DummyResponseContext is used only for POJO handling. Shouldn't be used in business logic");
    }
}
