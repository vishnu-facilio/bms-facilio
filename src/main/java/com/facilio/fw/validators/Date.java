package com.facilio.fw.validators;

import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.type.NumberType;

public class Date extends NumberType {
    public Date(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
    }
}
