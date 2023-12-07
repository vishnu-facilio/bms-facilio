package com.facilio.ch;

import lombok.extern.log4j.Log4j;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;


@Log4j
public class ClickhouseUtil {

    public static List<Map<String, Object>> fetchSampleSelectQuery(boolean isCh) throws Exception {
        LOGGER.info("CLICKHOUSE :: sample select query "+isCh);

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ModuleNames.WEATHER_STATION);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.WEATHER_STATION);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .limit(3);
        List<Map<String, Object>> result;

        if(isCh) {
            result = FacilioService.runAsServiceWihReturn(
                    FacilioConstants.Services.CLICKHOUSE, () -> selectBuilder.get());
            LOGGER.info("Results in clickhouse ::"+result);
        } else {
            result = selectBuilder.get();
            LOGGER.info("Results in non-clickhouse ::"+result);
        }
        return result;
    }


    public static List<Map<String, Object>> fetchAggrReadingInfo() throws Exception {
        FacilioModule module = ModuleFactory.getAggregationReadingInfoModule();
        List<FacilioField> fields = FieldFactory.getAggregationReadingInfoFields();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TIMEZONE","timezone", "Europe/London", StringOperators.IS));
        List<Map<String, Object>> result = selectRecordBuilder.get();
        LOGGER.info("Agg res :: "+result);
        return result;
    }

    public static JSONObject fetchSampleAggrQuery() throws Exception {
        JSONObject response = new JSONObject();

        LOGGER.info("CLICKHOUSE :: sample aggregation query ");
        List<Map<String, Object>> fetchAggrReadingInfo = fetchAggrReadingInfo();
        response.put("aggregationInfo", fetchAggrReadingInfo);
        if(fetchAggrReadingInfo.isEmpty()) {
            LOGGER.info("No aggregation info available");
            return null;
        }
        Map<String, Object> fetchAggrReadingRecord = fetchAggrReadingInfo.get(0);
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.ELECTRICITY_DATA_READING);

        FacilioModule module = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_DATA_READING);

        // on the fly module creation
        FacilioModule dynamicModule = new FacilioModule();
        dynamicModule.setName("aggr"+module.getName());
        dynamicModule.setDisplayName("Aggregated "+module.getDisplayName());
        dynamicModule.setTableName((String) fetchAggrReadingRecord.get("aggregatedTableName"));

        // on the fly fields creation
        List<FacilioField> subFields = getDefaultAggrFields(fields, dynamicModule);
        updateAggrFields(fields, subFields, dynamicModule);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table((String) fetchAggrReadingRecord.get("aggregatedTableName"))
                .select(subFields)
                .limit(10);

        List<Map<String, Object>> result = FacilioService.runAsServiceWihReturn(
                FacilioConstants.Services.CLICKHOUSE, () -> selectBuilder.get());

        LOGGER.info("Results in clickhouse ::"+result);
        response.put("aggregationResultSet", result);
        return response;
    }


    private static void updateAggrFields(List<FacilioField> fields, List<FacilioField> subFields, FacilioModule module) {
        List<String> subList = Arrays.asList(new String[] {
                "totalEnergyConsumptionDelta"
        });

        for(FacilioField f : fields) {
            if(subList.contains(f.getName())) {
                if(f.getDataTypeEnum().equals(FieldType.DECIMAL) || f.getDataTypeEnum().equals(FieldType.NUMBER)) {
                    subFields.add(getAggrField(f, module, "MIN"));
                    subFields.add(getAggrField(f, module, "MAX"));
                    subFields.add(getAggrField(f, module, "SUM"));
                    subFields.add(getAggrField(f, module, "COUNT"));
                } else if(f.getDataTypeEnum().equals(FieldType.BOOLEAN)) {
                    subFields.add(getAggrField(f, module, "TRUE_COUNT"));
                    subFields.add(getAggrField(f, module, "FALSE_COUNT"));
                }
            }
        }
    }

    private static FacilioField getAggrField(FacilioField f, FacilioModule module, String aggr) {
        FacilioField newField = f.clone();
        newField.setModule(module);
        newField.setName(aggr.toLowerCase()+f.getName());
        newField.setColumnName(aggr+"_"+f.getColumnName());
        newField.setDisplayName(aggr+" "+f.getDisplayName());
        return newField;
    }

    private static List<FacilioField> getDefaultAggrFields(List<FacilioField> fields, FacilioModule module) {
        List<String> commonList = Arrays.asList(new String[] {
                "MODULEID",
                "PARENT_METER_ID",
                "TTIME",
                "DATE"
        });
        List<FacilioField> commonFields =  new ArrayList<>();
        for(FacilioField f : fields) {
            if(commonList.contains(f.getColumnName())) {
                f.setModule(module);
                commonFields.add(f);
            }
        }

        return commonFields;
    }

    public static String getAggregatedTableName(String tableName, String timezone) throws Exception {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(tableName), "Table name can't be empty");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(timezone), "timezone can't be empty");
        FacilioModule module = ModuleFactory.getAggregationReadingInfoModule();
        List<FacilioField> fields = FieldFactory.getAggregationReadingInfoFields();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TABLE_NAME", "tableName", tableName, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("TIMEZONE", "timezone", timezone, StringOperators.IS));
        List<Map<String, Object>> result = selectRecordBuilder.get();
        String aggrTableName = null;
        if(CollectionUtils.isNotEmpty(result)) {
            aggrTableName =  (String) result.get(0).get("aggregatedTableName");
        }
        return aggrTableName;
    }
}
