package com.facilio.qa.context;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DummyQAndATemplateContext extends QAndATemplateContext {

    public DummyQAndATemplateContext (Long id) {
        super(id);
    }

    @Override
    protected ResponseContext newResponseObject() {
        throw new UnsupportedOperationException("DummyQAndATemplateContext is used only for POJO handling. Shouldn't be used in business logic");
    }

    @Override
    protected void addDefaultPropsForResponse(ResponseContext response) {
        throw new UnsupportedOperationException("DummyQAndATemplateContext is used only for POJO handling. Shouldn't be used in business logic");
    }
}
