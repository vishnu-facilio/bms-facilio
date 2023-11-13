package com.facilio.flows.blockconfigcommand;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.flows.context.RawRecordData;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public abstract class BaseCreateAndUpdateBlockCommand extends FacilioCommand {
    protected static void insert(long blockId,JSONObject rawRecord,String recordModuleName) throws Exception{
        if(MapUtils.isEmpty(rawRecord)){
            return;
        }

        validateRecordData(rawRecord,recordModuleName);
        List<RawRecordData> rawRecordDataList = spreadRawRecordData(blockId,rawRecord,recordModuleName);

        //RawRecordData table entry
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
             .table(ModuleFactory.getRawRecordDataModule().getTableName())
             .fields(FieldFactory.getRawRecordDataFields());

        List<Map<String,Object>> insertProps = FieldUtil.getAsMapList(rawRecordDataList, RawRecordData.class);
        insertRecordBuilder.addRecords(insertProps);
        insertRecordBuilder.save();
    }
    protected static void delete(Long blockId) throws Exception{
        //Delete RawRecordData entry
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
             .table(ModuleFactory.getRawRecordDataModule().getTableName())
             .andCondition(CriteriaAPI.getCondition("BLOCK_ID","blockId",blockId.toString(), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }
    private static List<RawRecordData> spreadRawRecordData(long blockId,JSONObject rawRecordJSON,String moduleName) throws Exception{
        List<RawRecordData> rawRecordDataList = new ArrayList<>();

        ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        if (FieldUtil.isSiteIdFieldPresent(module, true) && fields.stream().noneMatch(field -> field.getName().equals("siteId"))) {
            fields.add(FieldFactory.getSiteIdField(module));
        }
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Set<String> fieldNames = rawRecordJSON.keySet();

        for(String fieldName:fieldNames){
            RawRecordData rawRecordData = new RawRecordData();

            FacilioField field = fieldMap.get(fieldName);
            Object data = rawRecordJSON.get(fieldName);
            if(field == null){
                continue;
            }

            switch (field.getDataTypeEnum()) {
                case MULTI_LOOKUP:{
                    try {
                        if(data instanceof List){
                            ObjectMapper objectMapper = new ObjectMapper();
                            data = objectMapper.writeValueAsString(data);
                        }
                    }catch (Exception e){

                    }
                }
            }

            rawRecordData.setBlockId(blockId);
            rawRecordData.setFieldName(fieldName);
            rawRecordData.setFieldId(field.getFieldId());
            rawRecordData.setValueStr(data.toString());
            rawRecordDataList.add(rawRecordData);
        }

        return rawRecordDataList;
    }
    private static void validateRecordData(JSONObject recordData, String moduleName) throws Exception {
        ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        if (FieldUtil.isSiteIdFieldPresent(module, true) && fields.stream().noneMatch(field -> field.getName().equals("siteId"))) {
            fields.add(FieldFactory.getSiteIdField(module));
        }
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);


        Set<String> keySet = recordData.keySet();
        for (String fieldName : keySet){
            FacilioField field = fieldMap.get(fieldName);
            Object data = recordData.get(fieldName);
            if(field == null || data==null){
                continue;
            }
            switch (field.getDataTypeEnum()) {
                case DATE:
                case DATE_TIME:
                case SYSTEM_ENUM:
                case ENUM:
                case NUMBER:
                case DECIMAL:
                case LOOKUP:{
                    throwNumberOrStringError(field,data);
                    break;
                }
                case BOOLEAN: {
                    if(!(data instanceof Boolean || data instanceof String)){
                        throw new IllegalArgumentException("Data should be in either a Boolean or Place holder string for field:"+field.getDisplayName());
                    }
                    break;
                }
                case STRING:{
                    if(!(data instanceof String)){
                        throw new IllegalArgumentException("Data should be in either a String or Place holder string for field:"+field.getDisplayName());
                    }
                    break;
                }
                case MULTI_LOOKUP:{
                    if(!(data instanceof List || data instanceof String)){
                        throw new IllegalArgumentException("Data should be in either a List or Place holder string for field:"+field.getDisplayName());
                    }
                }
                default:
                    throw new IllegalArgumentException("Un supported field type"+field.getDataTypeEnum()+"::field name:"+fieldName);
            }
        }

    }
    private static void throwNumberOrStringError(FacilioField field,Object data) throws IllegalArgumentException {
        if(!(data instanceof Number || data instanceof String)){
            throw new IllegalArgumentException("Data should be in either a Number or Place holder string for field:"+field.getDisplayName());
        }
    }
    protected List<RawRecordData> getRawRecordsFromDB(Collection<Long> blockIds) throws Exception{
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getRawRecordDataFields())
                .table(ModuleFactory.getRawRecordDataModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("BLOCK_ID","blockId", StringUtils.join(blockIds,","), NumberOperators.EQUALS));
        List<Map<String,Object>> propList = selectRecordBuilder.get();

        if(CollectionUtils.isEmpty(propList)){
            return null;
        }

        List<RawRecordData> rawRecordDataList = FieldUtil.getAsBeanListFromMapList(propList, RawRecordData.class);
        return rawRecordDataList;

    }
    protected JSONObject deserializeRawRecordList(List<RawRecordData> rawDataList,String moduleName) throws Exception {
        JSONObject jsonObject = new JSONObject();

        ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        if (FieldUtil.isSiteIdFieldPresent(module, true) && fields.stream().noneMatch(field -> field.getName().equals("siteId"))) {
            fields.add(FieldFactory.getSiteIdField(module));
        }
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        for(RawRecordData rawData:rawDataList){
            String fieldName = rawData.getFieldName();
            FacilioField field = fieldMap.get(fieldName);
            String valueStr = rawData.getValueStr();
            Object value = valueStr;

            if(valueStr!=null) {
                switch (field.getDataTypeEnum()) {
                    case DATE:
                    case DATE_TIME:
                    case SYSTEM_ENUM:
                    case ENUM:
                    case NUMBER:
                    case LOOKUP: {
                        if (NumberUtils.isNumber(valueStr)) {
                            value = ((Double) Double.parseDouble(valueStr)).longValue();
                        }
                        break;
                    }
                    case DECIMAL: {
                        if (NumberUtils.isNumber(valueStr)) {
                            value = Double.parseDouble(valueStr);
                        }
                        break;
                    }
                    case BOOLEAN: {
                        if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                            value = FacilioUtil.parseBoolean(valueStr);
                        }
                        break;
                    }
                    case STRING: {
                        break;
                    }
                    case MULTI_LOOKUP: {
                        try {
                            value = FacilioUtil.parseJsonArray(valueStr);
                        } catch (Exception e) {

                        }
                    }
                }
            }

            jsonObject.put(fieldName,value);
        }

        return jsonObject;
    }

}
