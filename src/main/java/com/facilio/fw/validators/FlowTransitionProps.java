package com.facilio.fw.validators;

import com.facilio.fw.validators.validator.ValidatorUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Set;

//TODO Validate sub classes props of FlowTransitionContext
public class FlowTransitionProps extends ValidatorBase {
    private final RequestConfig requestConfig;

    public FlowTransitionProps(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
        this.requestConfig = requestConfig;
    }

    @Override
    public NodeError validateBeforeAuth(RequestContext requestContext, Object o) {
        return null;
    }

    @SneakyThrows
    @Override
    public  NodeError validateAfterAuth(RequestContext requestContext, Object node) {
        Map val = (Map) node;
        if (MapUtils.isEmpty(val)) {
            return new NodeError("Request body should not be empty.");
        }

        Set<String> keys = val.keySet();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFlowTransitionFields());
        for(String key:keys){
            FacilioField field = fieldMap.get(key);
            if(field!=null){
                ValidatorBase validator = ValidatorUtil.v3fieldsDataType(field, requestConfig, key);

                if (validator != null) {
                    NodeError nodeError = validator.validateBeforeAuth(requestContext, val.get(key));
                    if (nodeError != null) {
                        return nodeError;
                    }

                    nodeError = validator.validateAfterAuth(requestContext, val.get(key));
                    if (nodeError != null) {
                        return nodeError;
                    }
                }
            }

        }
        return null;
    }
}
