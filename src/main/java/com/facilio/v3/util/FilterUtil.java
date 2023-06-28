package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import java.util.*;
import java.util.stream.Collectors;

public class FilterUtil {
    private static final Logger LOGGER = LogManager.getLogger(FieldUtil.class.getName());

    public static String removeIfPivotDrillDownPatternExists(JSONObject filters){
        String drillDownPattern=null;

        if(filters.containsKey(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN) && filters.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN) != null) {
            drillDownPattern = filters.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN).toString();
            filters.remove(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN);
        }
        return drillDownPattern;
    }
    public static void getAsFilterList(JSONObject filters,List filterList){
        Iterator<String> filterIterator = filters.keySet().iterator();
        while(filterIterator.hasNext()) {
            String fieldName=filterIterator.next();
            Object filterObj=  filters.get(fieldName);
            if (fieldName.equals(FacilioConstants.ContextNames.ONE_LEVEL_LOOKUP) && !((JSONObject)filterObj).isEmpty()) {
                getAsFilterList((JSONObject) filterObj,filterList);
            }
            else if(filterObj!=null && filterObj instanceof JSONArray && ((JSONArray) filterObj).size()>0) {
                JSONArray fieldJsonArr = (JSONArray) filterObj;
                for(int i=0;i<fieldJsonArr.size();i++) {
                    JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
                    filterList.add(constructFilterObject(fieldName,fieldJsonObj));
                }
            }
            else if(filterObj!=null && filterObj instanceof JSONObject && !((JSONObject) filterObj).isEmpty()) {
                JSONObject fieldJsonObj = (JSONObject) filterObj;
                filterList.add(constructFilterObject(fieldName,fieldJsonObj));
            }
        }
    }
    public static JSONObject constructFilterObject(String fieldName, JSONObject obj)
    {
        JSONObject constructedObj= new JSONObject();
        constructedObj.put("fieldName",fieldName);
        constructedObj.putAll(obj);
        return constructedObj;
    }
    public static Criteria getCriteriaFromFilters(JSONObject filters,String moduleName, Context context) throws Exception {
        Operator op=Operator.getOperator(93);
        Criteria criteria=new Criteria();

        if(filters != null && !filters.isEmpty()) {
            boolean isPm;
            List<String> templateFields=Collections.emptyList();

            isPm = moduleName.equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
            if (isPm) {
                templateFields = PreventiveMaintenanceAPI.getTemplateFields();
            }

            String drillDownPattern=removeIfPivotDrillDownPatternExists(filters);
            List filterList=new ArrayList();
            getAsFilterList(filters,filterList);
            if(StringUtils.isNotEmpty(drillDownPattern)) {
                filterList=(List) filterList.stream().sorted(Comparator.comparingLong(i->(long)((JSONObject)i).get("id"))).collect(Collectors.toList());
            }

            for(Object conditionObj:filterList)
            {
                Object fieldJson = conditionObj;
                String fieldName= (String) ((JSONObject)fieldJson).get("fieldName");
                if(isPm) {
                    moduleName = PreventiveMaintenanceAPI.getPmModule(templateFields, fieldName);
                }

                if(StringUtils.isNotEmpty(drillDownPattern)){
                    fieldName = fieldName.split("----")[0];
                }

                List<Condition> conditionList = new ArrayList<>();
               if(fieldJson!=null) {
                    JSONObject fieldJsonObj = (JSONObject) fieldJson;
                    FilterUtil.setConditions(moduleName, fieldName, fieldJsonObj, conditionList, context, isPm);
                }
                criteria.groupOrConditions(conditionList);
            }

            if(StringUtils.isNotEmpty(drillDownPattern)){
                criteria.setPattern(drillDownPattern);
            }
        }

        return criteria;
    }
    public static Criteria getCriteriaFromQuickFilter(JSONObject filters,String moduleName) throws Exception {
        Criteria criteria=new Criteria();

        if(filters != null && !filters.isEmpty()) {
            boolean isPm;
            List<String> templateFields=Collections.emptyList();

            Iterator<String> filterIterator = filters.keySet().iterator();


            isPm = moduleName.equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
            if (isPm) {
                templateFields = PreventiveMaintenanceAPI.getTemplateFields();
            }


            while(filterIterator.hasNext())
            {
                String fieldName = filterIterator.next();
                Object fieldJson = filters.get(fieldName);

                if(isPm) {
                    moduleName = PreventiveMaintenanceAPI.getPmModule(templateFields, fieldName);
                }
                List<Condition> conditionList = new ArrayList<>();
                if(fieldJson!=null && fieldJson instanceof JSONObject) {
                    JSONObject fieldJsonObj = (JSONObject) fieldJson;
                    FilterUtil.setConditionForQuickFilter(moduleName, fieldName, fieldJsonObj, conditionList);
                }
                criteria.groupOrConditions(conditionList);
            }
        }

        return criteria;
    }

    private static boolean isScopeFieldAndNonScopeModule (String fieldName, String moduleName, ModuleBean modBean) throws Exception {
        switch (fieldName) {
            case "site":
            case "siteId":
                return !FieldUtil.isSiteIdFieldPresent(modBean.getModule(moduleName), true);
        }
        return false;
    }
    public static void setConditionForQuickFilter(String moduleName, String fieldName, JSONObject fieldJson, List<Condition> conditionList) throws Exception {
        ModuleBean moduleBean= Constants.getModBean();

        Operator operator;
        int operatorId=-1;
        FacilioField field=moduleBean.getField(fieldName,moduleName);
        if(field!=null){
            operator=FIELD_TYPE_VS_DEFAULT_OPERATOR_MAP.get(field.getDataTypeEnum());
            if(operator==null){
                throw new IllegalArgumentException(field.getDataTypeEnum() +"-- field type not supported for quick filter");
            }
            operatorId=operator.getOperatorId();
            JSONArray value = (JSONArray) fieldJson.get("value");

            Condition condition = new Condition();
            condition.setField(field);
            condition.setOperatorId(operatorId);

            if(operator.isValueNeeded() && (value!=null && value.size()>0)) {
                setValueForCondition(condition, value, fieldName, field, moduleName, fieldJson);
            }
            condition.validateValue();
            conditionList.add(condition);
        }
    }
    public static void setConditions(String moduleName, String fieldName, JSONObject fieldJson, List<Condition> conditionList) throws Exception {
        setConditions(moduleName,fieldName,fieldJson,conditionList, null, false);
    }

    public static void setConditions(String moduleName, String fieldName, JSONObject fieldJson, List<Condition> conditionList, Context context, boolean isPm) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        int operatorId = -1;
        String operatorName;

        Operator operator = null ;
        if (fieldJson.containsKey("operatorId")) {
            operatorId = (int) (long) fieldJson.get("operatorId");
            operator = Operator.getOperator(operatorId);
        }

        if(operator != null && operator instanceof RelationshipOperator) {
            Condition condition = new Condition();
            condition.setFieldName(fieldName);
            condition.setOperatorId(operatorId);

            JSONArray value = (JSONArray) fieldJson.get("value");

            if(operator.isValueNeeded() && (value!=null && value.size()>0)) {
                StringBuilder values = new StringBuilder();
                boolean isFirst = true;
                Iterator<String> iterator = value.iterator();
                while (iterator.hasNext()) {
                    String obj = iterator.next();
                    if (!isFirst) {
                        values.append(",");
                    } else {
                        isFirst = false;
                    }
                    values.append(obj);
                }
                condition.setValue(values.toString());
                condition.validateValue();
            }
            conditionList.add(condition);
            return;
        }


        boolean isOldField = false;
        //TODO Remove the check once handled in mobile
        if (OLD_FIELDS_MODULE.contains(moduleName) && OLD_FIELDS.contains(fieldName)) {
            fieldName = fieldName.replace("Id", "");
            isOldField = true;
        }

        String relatedFieldName = (String) fieldJson.get("relatedFieldName");
        FacilioField field;
        if(relatedFieldName != null) {
            // here fieldName is modulename of subquery
            field = modBean.getField(relatedFieldName, fieldName);
        }
        else {
            field = modBean.getField(fieldName, moduleName);
        }
        if (field == null) {
            // Temp handling...needs to check
            if (fieldName.equals("site") && OLD_FIELDS_MODULE.contains(moduleName)) {
                field = modBean.getField("siteId", moduleName);
            }
            if (field == null) {
                if (isScopeFieldAndNonScopeModule(fieldName, moduleName, modBean)) {
                    return;
                }
                else {
                    LOGGER.error("Field is not found for: " + fieldName + " : " + moduleName);
                    throw new IllegalArgumentException("Field is not found for: " + fieldName + " : " + moduleName);
                }
            }
        }

        if (fieldJson.containsKey("operatorId")) {
            operatorId = (int) (long) fieldJson.get("operatorId");
            if (isOldField) {
                if (operatorId == NumberOperators.EQUALS.getOperatorId()) {
                    operatorId = PickListOperators.IS.getOperatorId();
                }
                else if (operatorId == NumberOperators.NOT_EQUALS.getOperatorId()) {
                    operatorId = PickListOperators.ISN_T.getOperatorId();
                }
            }
            operator = Operator.getOperator(operatorId);
            operatorName = operator.getOperator();
        } else {
            operatorName = (String) fieldJson.get("operator");
            if (isOldField) {
                if (operatorName.equals(NumberOperators.EQUALS.getOperator())) {
                    operatorName = PickListOperators.IS.getOperator();
                }
                else if (operatorName.equals(NumberOperators.NOT_EQUALS.getOperator())) {
                    operatorName = PickListOperators.ISN_T.getOperator();
                }
            }
            operator = field.getDataTypeEnum().getOperator(operatorName);
            operatorId = operator.getOperatorId();
        }
        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperatorId(operatorId);
        condition.setOperator(operator);
        if (fieldJson.containsKey("criteriaValue") && operator != null && (operator instanceof LookupOperator)) {
            if (field != null) {
                Criteria criteria = new Criteria();
                List<Condition> lookupConditionList = new ArrayList<>();
                condition.setModuleName(moduleName);
                condition.setFieldName(fieldName);
                JSONObject lookup = getParamsForOneLevelLookup((JSONObject) fieldJson.get("criteriaValue"));
                setConditions(((LookupField) field).getLookupModule().getName(), (String) lookup.get("lookupFieldname"), (JSONObject) lookup.get("value"), lookupConditionList);
                criteria.addAndCondition(lookupConditionList.get(0));
                condition.setCriteriaValue(criteria);
                condition.validateValue();
                conditionList.add(condition);
            }
            return;
        } else {
            JSONArray value = (JSONArray) fieldJson.get("value");
            if (operator.isValueNeeded() && (value != null && value.size() > 0)) {

                setValueForCondition(condition, value, fieldName, field, moduleName, fieldJson);

                if (fieldJson.containsKey("orFilters")) {    // To have or condition for different fields..eg: (space=1 OR purposeSpace=1)
                    JSONArray orFilters = (JSONArray) fieldJson.get("orFilters");
                    for (int i = 0; i < orFilters.size(); i++) {
                        JSONObject fieldJsonObj = (JSONObject) orFilters.get(i);
                        if (!fieldJsonObj.containsKey("operator")) {
                            fieldJsonObj.put("operator", operatorName);
                        }
                        if (!fieldJsonObj.containsKey("value")) {
                            fieldJsonObj.put("value", value);
                        }
                        setConditions(moduleName, (String) fieldJsonObj.get("field"), fieldJsonObj, conditionList, context, isPm);
                    }
                }
            }
            condition.validateValue();
            if (isPm && moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE) && fieldName.equals(FacilioConstants.ContextNames.SITE_ID) && context != null) {
                context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_SITE_FILTER_VALUES, condition.getValue());
            } else {
                conditionList.add(condition);
            }
        }
    }
  private static void setValueForCondition(Condition condition,JSONArray value,String fieldName,FacilioField field,String moduleName,JSONObject fieldJson) throws Exception {
      ModuleBean modBean=Constants.getModBean();
      Operator operator=condition.getOperator();

      StringBuilder values = new StringBuilder();
      boolean isFirst = true;
      Iterator<String> iterator = value.iterator();
      while(iterator.hasNext())
      {
          String obj = iterator.next();
          if(!isFirst) {
              values.append(",");
          }
          else {
              isFirst = false;
          }
          if (operator instanceof StringOperators || operator instanceof StringSystemEnumOperators || operator instanceof UrlOperators) {
              obj = obj.replace(",", StringOperators.DELIMITED_COMMA);
              values.append(obj.trim());
          }
          else if (field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL){
              values.append(encodeFilterDataForNumberField(obj.trim(),field));
          }else {
              String splDisplayName = PickListOperators.getDisplayNameForCurrentValue(obj);
              if (StringUtils.isEmpty(splDisplayName)) {
                  values.append(ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), obj.trim()));
              }
              else {
                  values.append(obj.trim());
              }
          }
      }
      String valuesString = values.toString();
      if (condition.getOperator() instanceof FieldOperator) {
          condition.setValueField(modBean.getField(valuesString, moduleName));
      } else if(condition.getOperator() instanceof RelatedModuleOperator) {
          Criteria criteriaVal = new Criteria();
          Operator relatedOperator = NumberOperators.EQUALS;
          if (fieldJson.containsKey("relatedOperatorId")) {
              relatedOperator = Operator.getOperator((int)(long) fieldJson.get("relatedOperatorId"));
          }
          FacilioField relatedField;
          if (fieldJson.containsKey("filterFieldName")) {
              relatedField = modBean.getField((String)fieldJson.get("filterFieldName"), fieldName);
          }
          else {
              relatedField = FieldFactory.getIdField(modBean.getModule(fieldName));
          }
          criteriaVal.addAndCondition(CriteriaAPI.getCondition(relatedField, valuesString, relatedOperator));
          condition.setCriteriaValue(criteriaVal);
      } else if (condition.getOperator() instanceof LookupOperator) {
          LOGGER.info("Old Filter used for OnelevelLookup :  FieldJson - " + fieldJson + " ModuleName - " + moduleName + " FieldName - " + fieldName);
          Criteria criteriaVal = new Criteria();
          Operator lookupOperator = Operator.getOperator((int)(long) fieldJson.get("lookupOperatorId"));
          FacilioField lookupFilterField = modBean.getField((String)fieldJson.get("field"), ((LookupField)field).getLookupModule().getName());
          criteriaVal.addAndCondition(CriteriaAPI.getCondition(lookupFilterField, valuesString, lookupOperator));
          condition.setCriteriaValue(criteriaVal);
      } else {
          condition.setValue(valuesString);
      }
  }
    private static Map<FieldType,Operator> initDefaultOperatorsMap(){
        Map<FieldType,Operator> map=new HashMap<>();
        map.put(FieldType.STRING,StringOperators.CONTAINS);
        map.put(FieldType.NUMBER,StringOperators.CONTAINS);
        map.put(FieldType.BOOLEAN,BooleanOperators.IS);
        map.put(FieldType.STRING_SYSTEM_ENUM,StringSystemEnumOperators.IS);
        map.put(FieldType.SYSTEM_ENUM,EnumOperators.IS);
        map.put(FieldType.URL_FIELD,UrlOperators.IS);
        map.put(FieldType.MULTI_LOOKUP,MultiFieldOperators.CONTAINS);
        map.put(FieldType.MULTI_ENUM, MultiFieldOperators.CONTAINS);
        map.put(FieldType.LOOKUP,PickListOperators.IS);
        map.put(FieldType.ID,NumberOperators.EQUALS);
        map.put(FieldType.ENUM,EnumOperators.IS);
        map.put(FieldType.DECIMAL,StringOperators.CONTAINS);
        map.put(FieldType.DATE_TIME,DateOperators.BETWEEN);
        map.put(FieldType.DATE,DateOperators.BETWEEN);
        map.put(FieldType.CURRENCY_FIELD,CurrencyOperator.EQUALS);
        map.put(FieldType.MULTI_CURRENCY_FIELD, MultiCurrencyOperator.EQUALS);
        map.put(FieldType.BIG_STRING,StringOperators.CONTAINS);

        return map;
    }
    private static final Map<FieldType,Operator> FIELD_TYPE_VS_DEFAULT_OPERATOR_MAP=Collections.unmodifiableMap(initDefaultOperatorsMap());
    private static final List<String> OLD_FIELDS = Arrays.asList("siteId", "spaceId", "buildingId", "floorId", "spaceId1", "spaceId2", "spaceId3", "spaceId4");
    private static final List<String> OLD_FIELDS_MODULE = Arrays.asList(FacilioConstants.ContextNames.BASE_SPACE, FacilioConstants.ContextNames.RESOURCE, FacilioConstants.ContextNames.SPACE, FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.BUILDING, FacilioConstants.ContextNames.FLOOR);
    public static JSONObject getParamsForOneLevelLookup(JSONObject fieldMap)
    {
        JSONObject lookup=new JSONObject();
        String fieldName= (String) fieldMap.keySet().toArray()[0];
        lookup.put("lookupFieldname",fieldName);
        lookup.put("value",fieldMap.get(fieldName));
        return lookup;
    }
    private static String encodeFilterDataForNumberField(String value,FacilioField field) {
        StringBuilder stringBuilder = new StringBuilder();
        if(value.contains(",")){
            String arr[]=value.split(",");
            for(int i=0;i< arr.length;i++){
                String strValue = arr[i];
                if(NumberUtils.isNumber(strValue)){
                    stringBuilder.append(strValue);
                }else{
                    stringBuilder.append("-1");
                    //FacilioUtil.throwIllegalArgumentException(!NumberUtils.isNumber(strValue),strValue+" is not a number for "+field.getDisplayName() +" field");
                    LOGGER.info(strValue+" is not a number for field:"+field.toString() +" in FilterUtil");

                }
                if(i!= arr.length-1){
                    stringBuilder.append(",");
                }
            }
        }else{
            if(NumberUtils.isNumber(value)){
                stringBuilder.append(value);
            }else{
                stringBuilder.append("-1");
                LOGGER.info(value+" is not a number for field:"+field.toString()+"  in FilterUtil");
            }
        }
        return stringBuilder.toString();
    }

}
