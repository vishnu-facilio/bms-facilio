package com.facilio.fw.validators.validator;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.ValidatorBase;
import com.facilio.security.requestvalidator.config.Property;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import com.facilio.security.requestvalidator.type.TypeFactory;

import java.util.*;

public class ValidatorUtil {

    public static NodeError checkFields(RequestConfig requestConfig, RequestContext requestContext, String moduleName, Set<String> map,Map<String, Object> formData) throws Exception {
        for (String key : map) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> formFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> formFieldMap = FieldFactory.getAsMap(formFields);
            FacilioField facilioField = formFieldMap.get(key);
            if (facilioField == null && !Objects.equals(key, "sub_form_action")) {
                continue;
            }
            ValidatorBase validator = ValidatorUtil.v3fieldsDataType(facilioField, requestConfig, key);

            if(validator != null && Objects.equals(key, "siteId")){
                Object objectSiteId= formData.get(key);
                Map<String, Object> idMap = (Map<String, Object>) objectSiteId;
                NodeError nodeError = validator.validateBeforeAuth(requestContext, idMap.get("id"));
                if (nodeError != null) {
                    return nodeError;
                }
            }
            else if (validator != null) {
                NodeError nodeError = validator.validateBeforeAuth(requestContext, formData.get(key));
                if (nodeError != null) {
                    return nodeError;
                }

                nodeError = validator.validateAfterAuth(requestContext, formData.get(key));
                if (nodeError != null) {
                    return nodeError;
                }
            }
        }
        return null;
    }
    public static ValidatorBase v3fieldsDataType(FacilioField facilioField, RequestConfig requestConfig, String key) {
        FieldType dataTypeEnum;
        if (Objects.equals(key, "sub_form_action") && facilioField == null) {
            dataTypeEnum = FieldType.STRING;
        }else if (Objects.equals(key, "id")||Objects.equals(key, "lng")||Objects.equals(key, "lat") && facilioField == null) {
            dataTypeEnum = FieldType.NUMBER;
        }
        else{
            assert facilioField != null;
            dataTypeEnum = facilioField.getDataTypeEnum();}
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
                case LOOKUP:
                    validator = TypeFactory.getValidatorForNode(requestConfig,getAsProperty(key,"lookup"));
                    break;
            }
        return validator;
    }
    private static Property getAsProperty(String name, String type) {
        Property prop = new Property();
        prop.setName(name);
        prop.setType(type);
        prop.setAttrs(new HashMap<>());
        return prop;
    }
    public static String getModule(long moduleId) throws Exception {
        GenericSelectRecordBuilder selectBuilderModule = new GenericSelectRecordBuilder()
                .select(FieldFactory.getModuleFields())
                .table(ModuleFactory.getModuleModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
        List<Map<String, Object>> moduleNames = selectBuilderModule.get();

        Object ModuleName;
        for (Map<String, Object> name : moduleNames) {
            ModuleName = name.get("name");
            return (String) ModuleName;
        }
        return null;
    }

    public static long getModuleId(long formId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFormFields())
                .table(ModuleFactory.getFormModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(formId), NumberOperators.EQUALS));
        List<Map<String, Object>> fieldProps = selectBuilder.get();

        long moduleIds = 0;
        for (Map<String, Object> fields : fieldProps) {
            moduleIds = (long) fields.get("moduleId");
            return moduleIds;
        }
        return  moduleIds;
    }
    public static long getModuleId(String subName) throws Exception {
            GenericSelectRecordBuilder selectBuilderForSubForm = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getFormFields())
                    .table(ModuleFactory.getFormModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("NAME", "name", subName, StringOperators.IS));
            List<Map<String, Object>> fieldPropsForSubForm = selectBuilderForSubForm.get();

            long moduleIdsForSubForm = 0;
            for (Map<String, Object> fields : fieldPropsForSubForm) {
                moduleIdsForSubForm = (long) fields.get("moduleId");
                return moduleIdsForSubForm;
            }
            return moduleIdsForSubForm;
        }
}
