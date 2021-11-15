package com.facilio.fw.validators;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import com.facilio.security.requestvalidator.type.TypeFactory;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomFields extends ValidatorBase {
    private String module;
    private RequestConfig requestConfig;
    public CustomFields(RequestConfig requestConfig, Property config) {
        super(requestConfig, config);
        this.requestConfig = requestConfig;
        this.module = config.getAttrs().get("module");
    }

    @Override
    public NodeError validateBeforeAuth(RequestContext requestContext, Object o) {
        return null;
    }

    @SneakyThrows
    @Override
    public NodeError validateAfterAuth(RequestContext requestContext, Object node) {
        Map val = (Map) node;
        Set set = val.keySet();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(this.module);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        for (Object k: set) {
            String key = (String) k;
            FacilioField facilioField = fieldMap.get(key);
            if (facilioField == null) {
                continue;
            }

            FieldType dataTypeEnum = facilioField.getDataTypeEnum();
            ValidatorBase validator = null;
            switch (dataTypeEnum) {
                case STRING:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "string"));
                    break;
                case NUMBER:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "number"));
                    break;
                case BOOLEAN:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "boolean"));
                    break;
            }

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
        return null;
    }

    private Property getAsProperty(String name, String type) {
        Property prop = new Property();
        prop.setName(name);
        prop.setType(type);
        prop.setAttrs(new HashMap<>());
        return prop;
    }
}
