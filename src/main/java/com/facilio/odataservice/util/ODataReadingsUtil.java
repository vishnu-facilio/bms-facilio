package com.facilio.odataservice.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.action.ODataReadingsDataAction;
import com.facilio.v3.context.Constants;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.ParseException;
import org.apache.commons.chain.Context;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class ODataReadingsUtil {

   // get Reading view List
    public static List<String> getReadingViewsList() throws Exception{
        return ODataReadingViewsUtil.getReadingViewsList();
    }

    //get values for specific readingView
    public static List<Map<String, Object>> getReadingViewData(String readingView) throws Exception{
        ODataReadingsDataAction readingsDataAction = new ODataReadingsDataAction(readingView);
        return readingsDataAction.getReadingData(readingView);
    }

    //get fields for reading views
    public static Map<String, FacilioField> getFields(String readingView) throws Exception{
        List<FacilioField> fields = new ArrayList<>();
        List<Long> fieldIds = new ArrayList<>();
        ODataReadingsContext context1 = ODataReadingViewsUtil.getReadingView(readingView);
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String fieldObj = context1.getReadingFields();
        if(fieldObj != null&& !fieldObj.isEmpty() && !fieldObj.equalsIgnoreCase("[]") && !fieldObj.equalsIgnoreCase("null")) {
            JSONParser jsonParser = new JSONParser();
            List<String> resObj = (List<String>) jsonParser.parse(fieldObj);
            for(int i=0;i< resObj.size();i++) {
                Map<String,Object> resMap = (Map<String, Object>) jsonParser.parse(resObj.get(i));
                fieldIds.add((Long) resMap.get("fieldId"));
            }
            fields = moduleBean.getFields(fieldIds);
        }
        return FieldFactory.getAsMap(fields);
    }
    public static Map<String, Object> getWeatherStation(long parentId) throws Exception{
        String moduleName = FacilioConstants.ContextNames.NEW_WEATHER_READING;
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        String table = module.getTableName();
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder();
        genericSelectRecordBuilder.select(fields).table(table).andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), Collections.singleton(parentId), NumberOperators.EQUALS));
        Map<String,Object> obj = genericSelectRecordBuilder.fetchFirst();
        return obj;
    }
    public static List<FacilioField> getFields(long fieldId) throws Exception{
        List<FacilioField> fields = FieldFactory.getFieldFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table("Fields")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), Collections.singleton(fieldId),NumberOperators.EQUALS));
        List<Map<String,Object>> fieldProps = builder.get();
        if(fieldProps != null && !fieldProps.isEmpty()) {
            Map<String, Object> fieldProp = fieldProps.get(0);
            Long parentId = (Long)fieldProp.get("moduleId");
            FacilioModule module = Constants.getModBean().getModule(parentId);
            Map<Long, FacilioModule> moduleMap = new HashMap<>();
            moduleMap.put(parentId,module);
            List<FacilioField> fields1 = Constants.getModBean().getFieldFromPropList(fieldProps, moduleMap);
            return fields1;
        }
        return new ArrayList<>();
    }
    public static List<FacilioField> getFields(List<Long> fieldIds) throws Exception{
        List<FacilioField> fields = FieldFactory.getFieldFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table("Fields")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), fieldIds,NumberOperators.EQUALS));
        List<Map<String,Object>> fieldProps = builder.get();
        if(fieldProps != null && !fieldProps.isEmpty()) {
            Map<String, Object> fieldProp = fieldProps.get(0);
            Long parentId = (Long)fieldProp.get("moduleId");
            FacilioModule module = Constants.getModBean().getModule(parentId);
            Map<Long, FacilioModule> moduleMap = new HashMap<>();
            moduleMap.put(parentId,module);
            List<FacilioField> fields1 = Constants.getModBean().getFieldFromPropList(fieldProps, moduleMap);
            return fields1;
        }
        return new ArrayList<>();
    }
    public static List<FacilioField> getModuleFields(String moduleName) throws Exception{
        long moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
        List<FacilioField> fields = FieldFactory.getFieldFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table("Fields")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), Collections.singleton(moduleId),NumberOperators.EQUALS));;
        List<Map<String,Object>> fieldProps = builder.get();
        if(fieldProps != null && !fieldProps.isEmpty()) {
            Map<String, Object> fieldProp = fieldProps.get(0);
            Long parentId = (Long)fieldProp.get("moduleId");
            FacilioModule module = Constants.getModBean().getModule(parentId);
            Map<Long, FacilioModule> moduleMap = new HashMap<>();
            moduleMap.put(parentId,module);
            List<FacilioField> fields1 = Constants.getModBean().getFieldFromPropList(fieldProps, moduleMap);
            return fields1;
        }
        return new ArrayList<>();
    }
    public static void getParentName(Context context, Map<String, Object> timeMap) throws Exception{
        ODataReadingsContext readingsContext = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        long type = readingsContext.getReadingType();
        String name;
        long parentId = (long)timeMap.get("parentId");
        if(type == 1){
            AssetContext asset = AssetsAPI.getAssetInfo(parentId);
            name = asset.getName();
            timeMap.put("Asset",name);
        }else if(type == 2){
            SpaceContext space = SpaceAPI.getSpace(parentId);
            name = space.getName();
            timeMap.put("Space",name);
        }else{
            Map<String, Object> obj = getWeatherStation(parentId);
            name = (String)obj.get("name");
            timeMap.put("WeatherStation",name);
        }
    }
    public static void getFilters(Context context, ODataReadingsContext context1) throws ParseException {
        long aggr =  context1.getAggregateOperator();
        long dateOp = context1.getDateOperator();
        String dateRange;
        if(dateOp == -2){
            dateRange = context1.getDateRange();
            net.minidev.json.parser.JSONParser jsonParser = new net.minidev.json.parser.JSONParser();
            JSONArray obj = (JSONArray) jsonParser.parse(dateRange);
            long startTime = (long)obj.get(0);
            long endTime = (long)obj.get(1);
            context.put("startTime",startTime);
            context.put("isDateValue",true);
            context.put("endTime",endTime);
        }else{
            context.put("isDateValue",false);
            context.put("DateOperator",dateOp);
        }
        context.put("Aggr",aggr);
    }

    public static Criteria getCriteria(List<Long> fieldIds, List<FacilioField> existingFields) throws Exception{
        List<FacilioField> fields = new ArrayList<>();
        if(existingFields.isEmpty()) {
            fields = ODataReadingsUtil.getFields(fieldIds);
        }else{
            fields.addAll(existingFields);
        }
        Criteria criteria = new Criteria();
        for(FacilioField field:fields){
            Condition condition = new Condition();
            condition.setField(field);
            condition.setOperator(CommonOperators.IS_NOT_EMPTY);
            criteria.addOrCondition(condition);
        }
        return criteria;
    }

    public static List<Map<String, Object>> getAdjustedTimeStammp(List<Map<String, Object>> totalProps, BmsAggregateOperators.DateAggregateOperator aggregateOperator) {
        for (Map<String, Object> prop : totalProps) {
            for (Map.Entry<String, Object> entry : prop.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("ttime")) {
                    long adjustedTime = aggregateOperator.getAdjustedTimestamp((Long) entry.getValue()) + TimeZone.getDefault().getRawOffset();
                    prop.replace(entry.getKey(), adjustedTime);
                }
            }
        }
        return totalProps;
    }
    public  static List<Map<String,Object>> getListAsTimeMap(List<Map<String, Object>> totalProps, Context context) throws Exception {
        Map<String,Object> timeMap = new HashMap<>();
        for (Map<String, Object> prop : totalProps) {
            String key = String.valueOf(prop.get("parentId")).concat(String.valueOf(prop.get("ttime")));
            if(timeMap.containsKey(key)){
                Map<String,Object> map = (Map<String,Object>) timeMap.get(key);
                map.putAll(prop);
                timeMap.replace(key,map);
            }else{
                timeMap.put(key,prop);
                getParentName(context, (Map<String, Object>) timeMap.get(key));
            }
        }
        List<Map<String,Object>> newProps = new ArrayList<>();
        for(Map.Entry<String,Object> entry: timeMap.entrySet()){
            Map<String,Object> listValue = (Map<String, Object>) entry.getValue();
            newProps.add(listValue);
        }
        return newProps;
    }
    public static int getReadingType(String readingViewName) throws Exception{
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(fieldMap.get("readingType")))
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),readingViewName, StringOperators.IS));
        Map<String,Object> obj = selectRecordBuilder.fetchFirst();
        return  (int)obj.get("readingType");
    }
}
