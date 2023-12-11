package com.facilio.fw.validators;

import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;

public class FiltersDataType extends ValidatorBase{
    private final RequestConfig requestConfig;

    public FiltersDataType(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
        this.requestConfig = requestConfig;
    }

    @Override
    public NodeError validateBeforeAuth(RequestContext requestContext, Object o) { return null; }

    @Override
    public NodeError validateAfterAuth(RequestContext requestContext, Object o) {
        return null;
    }
}
