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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class V3fields extends ValidatorBase {
    private RequestConfig requestConfig;

    public V3fields(RequestConfig requestConfig, Property config) {
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
        Map<String, List<Object>> paramMap = requestContext.getParamMap();
        List<Object> moduleNames = paramMap.get("moduleName");
        String moduleName = null;
        if (CollectionUtils.isNotEmpty(moduleNames)) {
            moduleName = (String) moduleNames.get(0);
        }

        Map val = (Map) node;
        if (MapUtils.isEmpty(val)) {
            return new NodeError("Request body should not be empty.");
        }

        if (StringUtils.isEmpty(moduleName)) {
            moduleName = (String) val.get("moduleName");
        }
        if (StringUtils.isEmpty(moduleName)) {
            moduleName = (String) requestContext.getGlobalValueMap().get("moduleName");
        }

        Map data = (Map) val.get("data");
        Set set = data.keySet();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
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
                case DECIMAL:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "number"));
                    break;
                case BOOLEAN:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "boolean"));
                    break;
                case DATE:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "date"));
                    break;
                case DATE_TIME:
                    validator = TypeFactory.getValidatorForNode(requestConfig, getAsProperty(key, "date_time"));
                    break;
            }

            if (validator != null) {
                NodeError nodeError = validator.validateBeforeAuth(requestContext, data.get(key));
                if (nodeError != null) {
                    return nodeError;
                }

                nodeError = validator.validateAfterAuth(requestContext, data.get(key));
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
