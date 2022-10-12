package com.facilio.fw.validators;

import com.facilio.fw.validators.validator.ValidatorUtil;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubFormData {
    public  static NodeError checkSubFormFields(RequestContext requestContext, RequestConfig requestConfig,Map<String, Object> nodeVal)throws  Exception {
        Map<String, Object> relation = (Map<String, Object>) nodeVal.get("relations");

        Set<String> setRelation = relation.keySet();

        for (String sub : setRelation) {

            long moduleIdForSubForm = ValidatorUtil.getModuleId(sub);

            String moduleNameForSubForm = ValidatorUtil.getModule(moduleIdForSubForm);

            Map<String, Object> subForm = (Map<String, Object>) relation.get(sub);

            List<Object> subFormData = (List<Object>) subForm.get("data");

            NodeError nodeError;
            for (Object arrayKeySet : subFormData) {
                Map<String, Object> map = (Map<String, Object>) arrayKeySet;
                Set<String> SetSubForm = map.keySet();
                nodeError = ValidatorUtil.checkFields(requestConfig, requestContext, moduleNameForSubForm, SetSubForm, map);
                if(nodeError!=null){
                    return nodeError;
                }
            }
        }
        return null;
    }
}
