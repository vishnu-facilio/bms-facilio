package com.facilio.moduleBuilder.util;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseFormatUtil {

    public static Map<String, Object> formatModuleBasedOnResponseFields(FacilioModule module, List<String> responseFieldNames, boolean iterateOverInstance) throws Exception {
        Map<String, Object> moduleResponse = new HashMap<>();

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(module);
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
        }

        for (String fieldName : responseFieldNames) {
            if (propertyDescriptorMap.containsKey(fieldName)) {
                Object value = propertyDescriptorMap.get(fieldName).getReadMethod().invoke(module);

                if (value instanceof FacilioField && iterateOverInstance) {
                    value = formatFieldsBasedOnResponseFields((FacilioField) value, responseFieldNames, false);
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

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(field);
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
        }

        for (String fieldName : responseFieldNames) {
            if (propertyDescriptorMap.containsKey(fieldName)) {
                Object value = propertyDescriptorMap.get(fieldName).getReadMethod().invoke(field);

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

}
