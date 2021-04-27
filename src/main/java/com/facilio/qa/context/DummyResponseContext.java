package com.facilio.qa.context;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class DummyResponseContext extends ResponseContext {
    @Override
    @JsonIgnore
    public QAndATemplateContext getParent() {
        throw new UnsupportedOperationException("DummyResponseContext is used only for POJO handling. Shouldn't be used in business logic");
    }

    @Override
    @JsonIgnore
    public void setParent(QAndATemplateContext template) {
        throw new UnsupportedOperationException("DummyResponseContext is used only for POJO handling. Shouldn't be used in business logic");
    }
}
