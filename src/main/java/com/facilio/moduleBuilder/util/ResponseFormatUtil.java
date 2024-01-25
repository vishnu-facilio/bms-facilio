package com.facilio.moduleBuilder.util;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseFormatUtil {

    public static final Map<String, PropertyDescriptor> FACILIOMODULE_PROPERTY_DESCRIPTOR = getPropertyDescriptorForBean(new FacilioModule());
    public static final Map<String, PropertyDescriptor> FACILIOFIELD_PROPERTY_DESCRIPTOR = getPropertyDescriptorForBean(new FacilioField());

    private static Map<String, PropertyDescriptor>  getPropertyDescriptorForBean(Object object) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
        }

        return MapUtils.unmodifiableMap(propertyDescriptorMap);
    }

    public static Map<String, Object> formatModuleBasedOnResponseFields(FacilioModule module, List<String> responseFieldNames, boolean iterateOverInstance) throws Exception {
        Map<String, Object> moduleResponse = new HashMap<>();

        for (String fieldName : responseFieldNames) {
            if (FACILIOMODULE_PROPERTY_DESCRIPTOR.containsKey(fieldName)) {
                Object value = FACILIOMODULE_PROPERTY_DESCRIPTOR.get(fieldName).getReadMethod().invoke(module);

                if (value instanceof FacilioModule && iterateOverInstance) {
                    value = formatModuleBasedOnResponseFields((FacilioModule) value, responseFieldNames, false);
                }

                moduleResponse.put(fieldName, value);
            }
        }

//        for(String fieldName : responseFieldNames) {
//            Object value = PropertyUtils.getProperty(module, fieldName);
////            if(value instanceof List) {
////                List<Object> listModuleResponseList = new ArrayList<>();
////
////                for(Object val : (List)value) {
////                    if(val instanceof FacilioModule) {
////                        listModuleResponseList.add(formatModuleBasedOnResponseFields((FacilioModule) val, responseFieldNames));
////                    }
////                }
////                value =  listModuleResponseList;
////            } else
//            if(value instanceof FacilioModule && iterateOverInstance) {
//                value = formatModuleBasedOnResponseFields((FacilioModule) value, responseFieldNames, false);
//            }
//
//            moduleResponse.put(fieldName, value);
//        }

        return moduleResponse;
    }

    public static Map<String, Object> formatFieldsBasedOnResponseFields(FacilioField field, List<String> responseFieldNames, boolean iterateOverInstance) throws Exception {
        Map<String, Object> fieldResponse = new HashMap<>();

        for (String fieldName : responseFieldNames) {
            if (FACILIOFIELD_PROPERTY_DESCRIPTOR.containsKey(fieldName)) {
                Object value = FACILIOFIELD_PROPERTY_DESCRIPTOR.get(fieldName).getReadMethod().invoke(field);

                if (value instanceof FacilioField && iterateOverInstance) {
                    value = formatFieldsBasedOnResponseFields((FacilioField) value, responseFieldNames, false);
                }

                fieldResponse.put(fieldName, value);
            }
        }

//        for(String fieldName : responseFieldNames) {
//            Object value = PropertyUtils.getProperty(field, fieldName);
//            if(value instanceof FacilioField && iterateOverInstance) {
//                value = formatFieldsBasedOnResponseFields((FacilioField) value, responseFieldNames, false);
//            }
//
//            fieldResponse.put(fieldName, value);
//        }

        return fieldResponse;
    }

    public static List<?> formatWorkflowRulesBasedOnResponseFields(List<?> ruleList, List<String> responseFieldNames, boolean iterateOverInstance, Object ruleInstance) throws Exception {
        List workflowResponseList=new ArrayList();
        if(CollectionUtils.isNotEmpty(ruleList)) {
            for (Object rule : ruleList) {
                workflowResponseList.add(formatSingleWorkflowRuleBasedOnResponseFields(rule,responseFieldNames,iterateOverInstance,ruleInstance));
            }
        }
        return workflowResponseList;
    }

    public static Map<String,Object> formatSingleWorkflowRuleBasedOnResponseFields(Object rule,List<String> responseFieldNames, boolean iterateOverInstance, Object ruleInstance) throws Exception {
        Map<String,PropertyDescriptor> facilioWorkflowRulePropertyDescriptor=getPropertyDescriptorForBean(ruleInstance);
        Map<String, Object> workflowMap = new HashMap<>();
        for (String fieldName : responseFieldNames) {
            if (facilioWorkflowRulePropertyDescriptor.containsKey(fieldName)) {
                Object value = facilioWorkflowRulePropertyDescriptor.get(fieldName).getReadMethod().invoke(rule);
                if (value instanceof WorkflowRuleContext && iterateOverInstance) {
                    value = formatSingleWorkflowRuleBasedOnResponseFields(value, responseFieldNames, false,value.getClass().newInstance());
                }
                workflowMap.put(fieldName, value);
            }
        }
        return workflowMap;
    }
}
