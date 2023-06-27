package com.facilio.odataservice.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.odataservice.service.ModuleViewsEdmProvider;
import com.facilio.v3.context.Constants;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.provider.*;
import java.util.*;

public class ODATAUtil {
   public static long orgId = AccountUtil.getCurrentOrg().getOrgId();
    private static String moduleName;
    public static String getModuleName() {
        return moduleName;
    }
    public static void setModuleName(String moduleName) {
        ODATAUtil.moduleName = moduleName;
    }
    private static FacilioModule module;
    private static List<FacilioField> fields;
    private static Map<String, FacilioField> fieldsAsMap;
    public static Map<String, String> getODATAType() {
        Map<String, String> dataTypeMap = new HashMap<>();
        dataTypeMap.put("bigint", "Int64");
        dataTypeMap.put("tinyint", "Int16");
        dataTypeMap.put("int", "Int32");
        dataTypeMap.put("long", "Int32");
        dataTypeMap.put("Number", "Int32");
        dataTypeMap.put("String", "String");
        dataTypeMap.put("double", "Double");
        dataTypeMap.put("float", "Float");
        dataTypeMap.put("Boolean", "Boolean");
        dataTypeMap.put("Decimal", "Double");
        dataTypeMap.put("System Enum", "Int32");
        dataTypeMap.put("File", "Int64");
        dataTypeMap.put("Identifier", "Int32");
        dataTypeMap.put("Misc", "String");
        dataTypeMap.put("DateTime", "DateTimeOffset");
        dataTypeMap.put("Date", "Date");
        dataTypeMap.put("Enum", "Int32");
        dataTypeMap.put("Counter", "Int32");
        dataTypeMap.put("Multi Lookup", "String");
        dataTypeMap.put("Multi Enum", "String");
        dataTypeMap.put("Score", "String");
        dataTypeMap.put("Line Item", "String");
        dataTypeMap.put("Large Text", "String");
        dataTypeMap.put("Big String", "String");
        dataTypeMap.put("String System Enum", "String");
        dataTypeMap.put("Url", "String");
        dataTypeMap.put("Currency", "String");
        dataTypeMap.put("Lookup", "String");
        return dataTypeMap;
    }
    public static List<SupplementRecord> getSupplementRecords() throws Exception {
        fields = Constants.getModBean().getAllFields(moduleName);
        List<SupplementRecord> lookup = new ArrayList<>();
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                lookup.add((SupplementRecord) field);
            }
        }
        return lookup;
    }
    public static Map getModuleFields(String moduleName) throws Exception {
        fields = Constants.getModBean().getAllFields(moduleName);
        module = Constants.getModBean().getModule(moduleName);
        fieldsAsMap = FieldFactory.getAsMap(fields);
        fieldsAsMap.put("id", FieldFactory.getIdField(module));
        return fieldsAsMap;
    }


    public static List<CsdlProperty> getEntityTypeProperties(Map<String, FacilioField> fieldMap, List<CsdlProperty> propertyList, boolean isReadings,int readingType) throws Exception {
        Map<String,String> map= getODATAType();
        for (Map.Entry<String, FacilioField> entry : fieldMap.entrySet()) {
            String s = map.get(entry.getValue().getDataTypeEnum().getTypeAsString());
            CsdlProperty csdlProperty = new CsdlProperty();
            csdlProperty.setName(entry.getValue().getName());
            csdlProperty.setType(EdmPrimitiveTypeKind.getByName(s).getFullQualifiedName());
            propertyList.add(csdlProperty);
        }
        if(isReadings) {
            propertyList.addAll(getReadingProperties(readingType));
        }
        return propertyList;
    }
    public static String getDisplayName(String fieldName){
        String dispName;
        dispName = fieldsAsMap.get(fieldName).getDisplayName();
        return dispName;
    }
    public static List<CsdlProperty> getReadingProperties(int readingType){
        List<CsdlProperty> propertyList = new ArrayList<>();
        CsdlProperty csdlProperty = new CsdlProperty();     // ttime is mandatory field for readings
        csdlProperty.setName("ttime");
        csdlProperty.setNullable(true);
        csdlProperty.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
        propertyList.add(csdlProperty);

        csdlProperty = new CsdlProperty();     // parentId is mandatory field for readings
        csdlProperty.setName("parentId");
        csdlProperty.setNullable(true);
        csdlProperty.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
        propertyList.add(csdlProperty);

        csdlProperty = new CsdlProperty();
        if(readingType == 1) {
            csdlProperty.setName("Asset");
        }else if(readingType == 2) {
            csdlProperty.setName("Space");
        }else {
            csdlProperty.setName("WeatherStation");
        }
        csdlProperty.setNullable(true);
        csdlProperty.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
        propertyList.add(csdlProperty);
        return propertyList;
    }
    public static EntityCollection  getMapAsEntityCollection(List<Map<String, Object>> propsMap) throws Exception {
        List<Entity> newRecords = new ArrayList<>();
        List<String> LookupFields = getLookupFields();
        for (Map<String, Object> prop : propsMap) {
            Entity entity = new Entity();
            for (Map.Entry<String, Object> entry : prop.entrySet()) {
                    if (ODATAUtil.isLookup(entry.getKey(),LookupFields)) {
                        if (entry.getValue() instanceof Map && ((LinkedHashMap) entry.getValue()).containsKey("displayName")) {
                            if(((LinkedHashMap) entry.getValue()).get("displayName")!=null) {
                                String name = ((LinkedHashMap) entry.getValue()).get("displayName").toString();
                                entity.addProperty(new Property(null, entry.getKey(), ValueType.PRIMITIVE, name));
                            }
                        } else if (entry.getValue() instanceof Map && ((LinkedHashMap) entry.getValue()).containsKey("status")) {
                            if(((LinkedHashMap) entry.getValue()).get("status")!=null) {
                                String name = ((LinkedHashMap) entry.getValue()).get("status").toString();
                                entity.addProperty(new Property(null, entry.getKey(), ValueType.PRIMITIVE, name));
                            }
                        }else if(entry.getValue() instanceof ArrayList){
                            List<Map<String,Object>> list = (List) entry.getValue();
                            List<String> valueList = new ArrayList<>();
                            if(list.get(0) instanceof Map) {
                                for (Map<String, Object> obj : list) {
                                    if (obj.get("displayName") != null) {
                                        valueList.add((String) obj.get("displayName"));
                                    } else if (obj.get("name") != null) {
                                        valueList.add((String) obj.get("name"));
                                    }
                                }
                            }
                        entity.addProperty(new Property(null, entry.getKey(), ValueType.PRIMITIVE, valueList));
                    }
                } else {
                        entity.addProperty(new Property(null,entry.getKey(), ValueType.PRIMITIVE, entry.getValue()));
                }
            }
            newRecords.add(entity);
        }
        return getListAsEntityCollection(newRecords);
    }
    public static Map<String, FacilioField> getFieldsAsMap() {
        return fieldsAsMap;
    }

    public static void setFieldsAsMap(Map<String, FacilioField> fieldsAsMap) {
        ODATAUtil.fieldsAsMap = fieldsAsMap;
    }

    public static List<String> getLookupFields() throws Exception {
        fields = Constants.getModBean().getAllFields(moduleName);
        List<String> lookupFields = new ArrayList<>();
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                lookupFields.add(field.getName());
            }
        }
        return lookupFields;
    }
    public static Boolean isLookup(String field, List<String> LookupFieldList) {
        Boolean result = LookupFieldList.contains(field);
        return result;
    }
    public static EntityCollection getListAsEntityCollection(List<Entity> RecordList){
        EntityCollection retEntitySet = new EntityCollection();
        for (Entity productEntity : RecordList) {
            retEntitySet.getEntities().add(productEntity);
        }
        return retEntitySet;

    }
    public static List<FullQualifiedName> getFQNLIST(List<String> EntityTypeList)
    {
        List<FullQualifiedName> fqnList = new ArrayList<>();
        for(String entityType : EntityTypeList)
        {
            fqnList.add(getFQN(entityType));
        }
        return fqnList;
    }
    public static FullQualifiedName getFQN(String entityType){
        FullQualifiedName fqn = new FullQualifiedName(ModuleViewsEdmProvider.NAMESPACE, entityType);
        return  fqn;
    }
    public static String splitFQN(FullQualifiedName fqn) {
        String name = fqn.toString().substring(fqn.toString().indexOf('.')+1);
        return name;
    }
}