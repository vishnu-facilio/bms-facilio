package com.facilio.fw.validators;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class V3fields extends ValidatorBase {
    private final RequestConfig requestConfig;

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
    public  NodeError validateAfterAuth(RequestContext requestContext, Object node) {
        Map<String, Object> paramMap = requestContext.getParamMap();
        Object moduleNameObj = paramMap.get("moduleName");
        String moduleName = null;
        if (moduleNameObj != null) {
            moduleName = (String) moduleNameObj;
        }

        Map val = (Map) node;
        if (MapUtils.isEmpty(val)) {
            return new NodeError("Request body should not be empty.");
        }

        if (StringUtils.isEmpty(moduleName)) {
            String moduleNameVal;
            try {
                moduleNameVal = (String)val.get("moduleName");
            } catch (ClassCastException var5) {
                return new NodeError(String.format("String expected for %s","moduleName" ));
            }
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
            if (facilioField == null && !Objects.equals(key, "id")) {
                continue;
            }

            ValidatorBase validator = ValidatorUtil.v3fieldsDataType(facilioField,requestConfig,key);

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
}
