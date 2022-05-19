package com.facilio.fw.validators;

import com.facilio.fw.validators.validator.ValidatorUtil;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LookUpDataType extends ValidatorBase {

    private final RequestConfig requestConfig;

    public LookUpDataType(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
        this.requestConfig = requestConfig;
    }

    @Override
    public NodeError validateBeforeAuth(RequestContext requestContext, Object o) {

        return null;
    }

    @Override
    public NodeError validateAfterAuth(RequestContext requestContext, Object o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> idMap = (Map<String, Object>) o;
        Set<String> key = idMap.keySet();

        String stringKey;
        for (String stringSet : key) {
            stringKey = stringSet;
            if (Objects.equals(stringKey, "id")||Objects.equals(stringKey, "lng")||Objects.equals(stringKey, "lat")) {
                ValidatorBase validator = ValidatorUtil.v3fieldsDataType(null, requestConfig, stringKey);

                if (validator != null) {
                    NodeError nodeError = validator.validateBeforeAuth(requestContext, idMap.get(stringKey));
                    if (nodeError != null) {
                        return nodeError;
                    }

                    nodeError = validator.validateAfterAuth(requestContext, idMap.get(stringKey));
                    if (nodeError != null) {
                        return nodeError;
                    }
                }
            }
        }
        return null;
    }
}
