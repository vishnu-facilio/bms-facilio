package com.facilio.odataservice.util;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.odataservice.data.ODataFilterContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;

import java.util.*;
import java.util.stream.Collectors;

public class ODATAUtil {
    private static final Logger LOGGER = LogManager.getLogger(ODATAUtil.class.getName());

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
        dataTypeMap.put("System Enum", "String");
        dataTypeMap.put("File", "Int64");
        dataTypeMap.put("Identifier", "Int32");
        dataTypeMap.put("Misc", "String");
        dataTypeMap.put("DateTime", "DateTimeOffset");
        dataTypeMap.put("Date", "Date");
        dataTypeMap.put("Enum", "String");
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
    public static Map<String,FacilioField> getModuleFields(String moduleName) throws Exception {
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        fieldsAsMap.put("id", FieldFactory.getIdField(module));
        return fieldsAsMap;
    }


    public static List<CsdlProperty> getEntityTypeProperties(Map<String, FacilioField> fieldMap, List<CsdlProperty> propertyList, boolean isReadings, int readingType, String moduleName) throws Exception {
        Map<String,String> map= getODATAType();
        Map<String, String> fieldNameVsDisplayNameMap = getFieldNameVsDisplayNameMap(moduleName);
        for (Map.Entry<String, FacilioField> entry : fieldMap.entrySet()) {
            String facilioDataTypeEnum = map.get(entry.getValue().getDataTypeEnum().getTypeAsString());
            CsdlProperty csdlProperty = new CsdlProperty();
            if(!isReadings) {
                if(entry.getValue().getName().equalsIgnoreCase("siteId")){
                    csdlProperty.setName("Site");
                }else {
                    if(entry.getValue().getName().equals("id")){
                        csdlProperty.setName("Id");
                    }else {
                        String displayName = fieldNameVsDisplayNameMap.get(entry.getValue().getName());
                        csdlProperty.setName(removeNameSpaces(displayName));
                    }
                }
            }else {
                String displayName = fieldNameVsDisplayNameMap.get(entry.getValue().getName());
                csdlProperty.setName(removeNameSpaces(displayName));
//                csdlProperty.setName(entry.getValue().getName());
            }
            csdlProperty.setType(EdmPrimitiveTypeKind.getByName(facilioDataTypeEnum).getFullQualifiedName());
            propertyList.add(csdlProperty);
        }
        if(isReadings) {
            propertyList.addAll(getReadingProperties(readingType));
        }
        return propertyList;
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

    private static Property getLookupEntity(Map.Entry<String,Object> entry,String displayName){
        Property property = new Property();
        property.setName(displayName);
        Object value = entry.getValue();
        if(entry.getValue() instanceof Map){
            Map<String,Object> entryValueMap = (Map<String, Object>) value;
            if(entryValueMap.containsKey("displayName")){
                property.setValue(ValueType.PRIMITIVE,entryValueMap.get("displayName").toString());
            }else if(entryValueMap.containsKey("status")){
                property.setValue(ValueType.PRIMITIVE,entryValueMap.get("status").toString());
            }else if(entryValueMap.containsKey("name")){
                property.setValue(ValueType.PRIMITIVE,entryValueMap.get("name").toString());
            }
        }else if(entry.getValue() instanceof ArrayList){
            List<Map<String,Object>> list = (List<Map<String, Object>>) entry.getValue();
            List<String> valueList = new ArrayList<>();
            for (Map<String, Object> obj : list) {
                String name = obj.getOrDefault("displayName",obj.get("name")).toString();
                if(name != null){
                    valueList.add(name);
                }
            }
            property.setValue(ValueType.PRIMITIVE,valueList);
        }
        return property;
    }
    public static EntityCollection  getMapAsEntityCollection(List<Map<String, Object>> propsMap, FacilioModule module,boolean isReadings) throws Exception {
        long startTime = System.currentTimeMillis();
        List<Entity> newRecords = new ArrayList<>();
        List<String> LookupFields = new ArrayList<>();
        Map<String,FacilioField> fieldsAsMap = new HashMap<>();
        
        if(module != null){
            LookupFields = getLookupFields(module);
            fieldsAsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(module.getName()));
            fieldsAsMap.put("id",FieldFactory.getIdField(module));
        }
        Map<Long,String> siteMap = new HashMap<>();
        Map<String, String> fieldNameVsDisplayNameMap = new HashMap<>();
        if(!isReadings) {
            fieldNameVsDisplayNameMap = getFieldNameVsDisplayNameMap(module.getName());
        }
        if(propsMap!= null && !propsMap.isEmpty()) {
            for (Map<String, Object> prop : propsMap) {
                Entity entity = new Entity();
                for (Map.Entry<String, Object> entry : prop.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String displayName = key;
                    if (!isReadings && fieldNameVsDisplayNameMap.containsKey(key)) {
                        displayName = removeNameSpaces(fieldNameVsDisplayNameMap.get(key));
                    } else {
                        if (key.equals("id")) {
                            displayName = "Id";
                        }
                    }
                    if (ODATAUtil.isLookup(key, LookupFields)) {
                        entity.addProperty(getLookupEntity(entry, displayName));
                    }
                    /** Site details for siteId *special handling for specific modules */
                    else if (key.equals("siteId")) {
                        Long siteId = (Long)value;
                        String siteName = "";
                        if(siteMap.containsKey(siteId)){
                            siteName = siteMap.get(siteId);
                        }else{
                            List<Long> siteIds = new ArrayList<>();
                            siteIds.add(siteId);
                            List<SiteContext> sites = SpaceAPI.getSites(siteIds);
                            if (sites != null && !sites.isEmpty()) {
                                siteMap.put(siteId,sites.get(0).getName());
                            }
                        }
                        entity.addProperty(new Property(null, "Site", ValueType.PRIMITIVE, siteName));
                    }
                    else {
                        if (fieldsAsMap.containsKey(key)) {
                            FacilioField field = fieldsAsMap.get(key);
                            String fieldType = field.getDataTypeEnum().getTypeAsString();
                            if(fieldType.equalsIgnoreCase("system enum")){
                               String enumValue = ((SystemEnumField)field).getValue((Integer)entry.getValue());
                                entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, enumValue));
                            }else if(fieldType.equalsIgnoreCase("enum")){
                                String enumValue = ((EnumField)field).getValue((Integer)entry.getValue());
                                entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, enumValue));
                            }else if (fieldType.equals("DateTime")) {
                                long dateTimeValue = (long) value;
                                entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, DateTimeUtil.getDateTime(dateTimeValue)));
                            }
                            else if (fieldType.equals("Date")) {
                                long dateTimeValue = (long) value;
                                entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, DateTimeUtil.getDateTime(dateTimeValue).toLocalDate()));
                            }else {
                                entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, value));
                            }
                        }else {
                            entity.addProperty(new Property(null, displayName, ValueType.PRIMITIVE, value));
                        }
                    }
                }
                newRecords.add(entity);
            }
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("Time taken for Converting propsMap to entity list : "+(endTime-startTime));
        return getListAsEntityCollection(newRecords);
    }

    public static List<String> getLookupFields(FacilioModule module) throws Exception {
        List<FacilioField> fields = Constants.getModBean().getAllFields(module.getName());
        List<String> lookupFields = new ArrayList<>();
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                lookupFields.add(field.getName());
            }
        }
        return lookupFields;
    }
    public static Boolean isLookup(String field, List<String> LookupFieldList) {
        return LookupFieldList.contains(field);
    }
    public static EntityCollection getListAsEntityCollection(List<Entity> RecordList){
        long startTime = System.currentTimeMillis();
        EntityCollection retEntitySet = new EntityCollection();
        for (Entity productEntity : RecordList) {
            retEntitySet.getEntities().add(productEntity);
        }
        LOGGER.info("Time taken for Converting entity list to entity collection : "+(System.currentTimeMillis()-startTime));
        return retEntitySet;

    }
    public static List<FullQualifiedName> getFQNLIST(List<String> EntityTypeList, String NAMESPACE)
    {
        List<FullQualifiedName> fqnList = new ArrayList<>();
        for(String entityType : EntityTypeList)
        {
            fqnList.add(getFQN(entityType, NAMESPACE));
        }
        return fqnList;
    }
    public static FullQualifiedName getFQN(String entityType, String NAMESPACE){
        return new FullQualifiedName(NAMESPACE, entityType);
    }
    public static String splitFQN(FullQualifiedName fqn) {
        return fqn.toString().substring(fqn.toString().indexOf('.')+1);
    }

    public static void getFilterContextList(Expression operand,List<ODataFilterContext> filterContexts,String moduleName){
        String value = "",field="";
        if(operand instanceof BinaryImpl){
            Expression lRightOp = ((BinaryImpl) operand).getRightOperand();
            Expression lLeftOp = ((BinaryImpl) operand).getLeftOperand();
            BinaryOperatorKind lOperator = ((BinaryImpl) operand).getOperator();
            if(lRightOp instanceof LiteralImpl){
                value = lRightOp.toString();
            }
            if(lLeftOp instanceof MemberImpl){
                field = lLeftOp.toString().replace("[","").replace("]","");
            }
            ODataFilterContext dataFilterContext = new ODataFilterContext();
            dataFilterContext.setField(field);
            dataFilterContext.setModuleName(moduleName);
            dataFilterContext.setValueList(value);
            dataFilterContext.setOperatorName(lOperator.toString());
            filterContexts.add(dataFilterContext);
        }
    }

    public static Map<String,String> getFieldNameVsDisplayNameMap(String moduleName) throws Exception{
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fieldsList = Constants.getModBean().getAllFields(moduleName);
        fieldsList.add(FieldFactory.getIdField(module));
        return fieldsList.stream().collect(Collectors.toMap(FacilioField::getName,FacilioField::getDisplayName, (prevValue, curValue) -> prevValue));
    }

    public static Map<String,String> getDisplayNameVsFieldNameMap(String moduleName) throws Exception{
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        List<FacilioField> fieldsList = Constants.getModBean().getAllFields(moduleName);
        fieldsList.add(FieldFactory.getIdField(module));
        return fieldsList.stream().collect(Collectors.toMap(FacilioField::getDisplayName,FacilioField::getName, (prevValue, curValue) -> prevValue));
    }
    /** modify display name without spaces */
    public static String removeNameSpaces(String displayName) {
        if(displayName.equalsIgnoreCase("space / asset")){
            return displayName.replace(" ","");
        }
        return displayName.replace(" ","_");
    }
    public static String addNameSpaces(String displayName) {
        if(displayName.equalsIgnoreCase("space/asset")){
            return displayName.replace("/"," / ");
        }
        return displayName.replace("_"," ");
    }

    public static List<ODataFilterContext> getFilterContext(FilterOption filterOption, String moduleName){
        List<ODataFilterContext> filterContexts = new ArrayList<>();
        Expression expression =  filterOption.getExpression();
        if(expression instanceof BinaryImpl){
            if(((BinaryImpl)expression).getLeftOperand() instanceof BinaryImpl){
                Expression leftOp = ((BinaryImpl)expression).getLeftOperand();
                getFilterContextList(leftOp,filterContexts,moduleName);
            }
            if(((BinaryImpl)expression).getRightOperand() instanceof BinaryImpl){
                Expression rightOp = ((BinaryImpl)expression).getRightOperand();
                getFilterContextList(rightOp,filterContexts,moduleName);
            }
        }
         return filterContexts;
    }
}