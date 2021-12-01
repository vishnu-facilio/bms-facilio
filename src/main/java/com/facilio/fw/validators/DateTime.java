package com.facilio.fw.validators;

import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.type.NumberType;

public class DateTime extends NumberType {
    public DateTime(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
    }
}
