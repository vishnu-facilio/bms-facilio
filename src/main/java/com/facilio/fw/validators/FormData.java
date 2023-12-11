package com.facilio.fw.validators;


import com.facilio.fw.validators.validator.ValidatorUtil;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class FormData extends ValidatorBase {
    private final RequestConfig requestConfig;

    public FormData(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
        this.requestConfig = requestConfig;
    }

    @Override
    public NodeError validateBeforeAuth(RequestContext requestContext, Object o) {
        return null;
    }

    @SneakyThrows
    @Override
    public NodeError validateAfterAuth(RequestContext requestContext, Object node) {

        Map<String, Object> paramMap = requestContext.getParamMap();
        Map<String, Object> nodeVal = (Map<String, Object>) node;

        Map<String, Object> requestContextVal = requestContext.getRequestBodyMap();

        if (MapUtils.isEmpty(nodeVal) && MapUtils.isEmpty(requestContextVal)) {
            return new NodeError("Request body should not be empty.");
        }

        long formId = (long) requestContextVal.get("formId");
        if(formId==0){
            return new NodeError("formId is empty");
        }
        long moduleId = ValidatorUtil.getModuleId(formId);

        String moduleName = ValidatorUtil.getModule(moduleId);

        Map<String, Object> formData = (Map<String, Object>) requestContextVal.get("formData");
        Set<String> setFormData = formData.keySet();

        NodeError nodeError = ValidatorUtil.checkFields(requestConfig, requestContext, moduleName, setFormData,formData);

        if(nodeError!=null){
            return nodeError;
        }

        nodeError=SubFormData.checkSubFormFields(requestContext,requestConfig,nodeVal);

        return nodeError;
    }
}
