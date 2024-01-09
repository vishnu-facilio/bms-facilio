package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import com.clickhouse.data.value.UnsignedLong;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.util.ReportUtil;

public class ConstructReportDataCommand extends FacilioCommand {
    public JSONObject dayOfWeek = new JSONObject();
    protected Collection<Map<String, Object>> initList(String sortAlias, boolean isTimeSeries) { //In case we wanna implement a sorted list
        if (isTimeSeries) {
            return new TreeSet<Map<String, Object>>((data1, data2) -> Long.compare((long) data1.get(sortAlias), (Long) data2.get(sortAlias)));
        } else {
            return new ArrayList<>();
        }
    }

//	private Map<Long, Map<Long, Object>> labelMap = new HashMap<>();

    protected boolean isTimeSeries(List<ReportDataContext> reportData) { //Temporary check
        if (CollectionUtils.isNotEmpty(reportData)) {
            ReportDataPointContext dp = reportData.get(0).getDataPoints().get(0);
            if (CollectionUtils.isNotEmpty(dp.getGroupByFields())) {
                return false;
            }
            if (dp.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dp.getxAxis().getDataTypeEnum() == FieldType.DATE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        JSONObject dataPoints_enumMap = new JSONObject();
        Boolean isV2Analytics = (Boolean) context.get("isV2Analytics");
        List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        String xAlias = getxAlias(report);
        Collection<Map<String, Object>> transformedData = initList(xAlias, isTimeSeries(reportData));
        Map<String, Object> intermediateData = new HashMap<>();
        for (ReportDataContext data : reportData) {
            Map<String, List<Map<String, Object>>> reportProps = data.getProps();
            if (reportProps != null && !reportProps.isEmpty()) {
                for (ReportDataPointContext dataPoint : data.getDataPoints()) {
                    if(dataPoint.getGroupByFields() != null && report != null && report.getType() == ReportContext.ReportType.WORKORDER_REPORT.getValue() &&
                            report.getxAggrEnum() == DateAggregateOperator.WEEKDAY &&
                            report.getDateRange() != null && report.getDateRange().getStartTime() > 0)
                    {
                        getWeeklyMapFromRange(report.getDateRange().getStartTime());
                    }
                    for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
                        List<Map<String, Object>> props = entry.getValue();
                        if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
                            constructData(report, dataPoint, props, null, transformedData, intermediateData);
                        } else {
                            constructData(report, dataPoint, props, data.getBaseLineMap().get(entry.getKey()), transformedData, intermediateData);
                        }
                    }
                    this.constructOptionsMapForEnumAndBoolean(isV2Analytics, dataPoints_enumMap, dataPoint);
                }
            }
        }
        if (report.getgroupByTimeAggr() > 0 && isTimeSeries(reportData) && report.getxAggr() > 0) {
            Collection<Map<String, Object>> groupedData = new ArrayList<>();
            Map<Object, List<Map<String, Object>>> rawData = new HashMap<>();
            for (Map<String, Object> dataMap : transformedData) {
                Object xVal = ((DateAggregateOperator) report.getxAggrEnum()).getAdjustedTimestamp((long) dataMap.get(xAlias));
                if (rawData.containsKey(xVal)) {
                    (rawData.get(xVal)).add(dataMap);
                } else {
                    List<Map<String, Object>> inputData = new ArrayList<>();
                    inputData.add(dataMap);
                    rawData.put(xVal, inputData);
                }
            }
            for (Object xKey : rawData.keySet()) {
                Map<String, Object> regroup = new HashMap<>();
                regroup.put(xAlias, xKey);
                regroup.put("group", rawData.get(xKey));
                groupedData.add(regroup);
            }
            transformedData = groupedData;
        }


        JSONObject data = new JSONObject();
        data.put(FacilioConstants.ContextNames.DATA_KEY, transformedData);
        if(!dataPoints_enumMap.isEmpty())
        {
            data.put(FacilioConstants.ContextNames.OPTIONS, dataPoints_enumMap);
        }
//		data.put(FacilioConstants.ContextNames.LABEL_MAP, labelMap);
        context.put(FacilioConstants.ContextNames.REPORT_SORT_ALIAS, xAlias);
        context.put(FacilioConstants.ContextNames.REPORT_DATA, data);
        return false;
    }

    private void constructData(ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> props, ReportBaseLineContext baseLine, Collection<Map<String, Object>> transformedData, Map<String, Object> directHelperData) throws Exception {
        HashMap<ReportFieldContext, List<Long>> dpLookUpMap = new HashMap();
        HashMap<ReportFieldContext, List<Long>> dpMultiLookUpMap = new HashMap();
        if (props != null && !props.isEmpty()) {
            for (Map<String, Object> prop : props) {
                Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
                if(dataPoint.getxAxis().getField() instanceof MultiEnumField)
                {
                    xVal = prop.containsKey("index") ? prop.get("index") : null;
				}
                else if(dataPoint.getxAxis().getField() instanceof MultiLookupField){
                    xVal = prop.containsKey("right") ? prop.get("right") : null;
                }
                if (xVal != null) {
                    AggregateOperator x_report_Aggr = report.getxAggrEnum();
                    if(dataPoint.isRightInclusive() && x_report_Aggr != null && BmsAggregateOperators.getRightInclusiveAggr(x_report_Aggr.getValue()) != null){
                        x_report_Aggr = BmsAggregateOperators.getRightInclusiveAggr(x_report_Aggr.getValue());
                    }
                    xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine, x_report_Aggr);
                    Object formattedxVal = formatVal(dataPoint.getxAxis(), x_report_Aggr, xVal, null, false, dpLookUpMap, dpMultiLookUpMap);
                    Object yVal = null;
                    if(dataPoint.getDynamicKpi() != null){
                        yVal = prop.get("result");
                    }else {
                        yVal = prop.get(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
                    }
                    Object minYVal = null, maxYVal = null;
                    if (yVal != null) {
                        yVal = dataPoint.getDynamicKpi() != null ? DECIMAL_FORMAT.format(yVal) : formatVal(dataPoint.getyAxis(), dataPoint.getyAxis().getAggrEnum(), yVal, xVal, dataPoint.isHandleEnum(), dpLookUpMap, dpMultiLookUpMap, prop);
                        if (dataPoint.getyAxis() != null && dataPoint.getyAxis().isFetchMinMax()) {
                            minYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MIN, prop.get(dataPoint.getyAxis().getField().getName() + "_min"), xVal, dataPoint.isHandleEnum(), dpLookUpMap, dpMultiLookUpMap);
                            maxYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MAX, prop.get(dataPoint.getyAxis().getField().getName() + "_max"), xVal, dataPoint.isHandleEnum(), dpLookUpMap, dpMultiLookUpMap);
                        }

                        StringJoiner key = new StringJoiner("|");
                        Map<String, Object> data = null;
                        if (dataPoint.getGroupByFields() != null && !dataPoint.getGroupByFields().isEmpty()) {
                            data = new HashMap<>();
                            for (ReportGroupByField groupBy : dataPoint.getGroupByFields()) {
                                FacilioField field = groupBy.getField();
                                Object groupByVal = prop.get(field.getName());
                                if(field instanceof MultiEnumField)
                                {
                                    groupByVal = prop.containsKey("index") ? prop.get("index") : groupByVal;
                                }
                                else if(field instanceof MultiLookupField){
                                    groupByVal = prop.containsKey("right") ? prop.get("right") : groupByVal;
                                }
                                groupByVal = formatVal(groupBy, null, groupByVal, xVal, dataPoint.isHandleEnum(), dpLookUpMap, dpMultiLookUpMap);
                                data.put(groupBy.getAlias(), groupByVal);
                                key.add(groupBy.getAlias() + "_" + groupByVal.toString());
                            }
                        }
                        if (report.getgroupByTimeAggr() > 0 && !formattedxVal.equals("deleted")) {
                            key.add(xVal.toString());
                            constructAndAddData(key.toString(), data, xVal, yVal, minYVal, maxYVal, getyAlias(dataPoint, baseLine), report, dataPoint, transformedData, directHelperData);

                        } else {
                            if(!formattedxVal.equals("deleted")) {
                                key.add(formattedxVal.toString());
                                constructAndAddData(key.toString(), data, formattedxVal, yVal, minYVal, maxYVal, getyAlias(dataPoint, baseLine), report, dataPoint, transformedData, directHelperData);
                            }

                        }
                    }
                }
            }
            updateLookupMap(dpLookUpMap, report.getxAggrEnum(), dpMultiLookUpMap);
        }
    }

    private void constructAndAddData(String key, Map<String, Object> existingData, Object xVal, Object yVal, Object minYVal, Object maxYVal, String yAlias, ReportContext report, ReportDataPointContext dataPoint, Collection<Map<String, Object>> transformedData, Map<String, Object> intermediateData) {
        Map<String, Object> data = (Map<String, Object>) intermediateData.get(key);
        if (data == null) {
            data = existingData == null ? new HashMap<>() : existingData;
            data.put(getxAlias(report), xVal);
            intermediateData.put(key, data);
            transformedData.add(data);
        }

        if (dataPoint.isHandleEnum())
        {
            if(dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue())
            {
                List<SimpleEntry<Long, HashMap<Integer, Long>>> value_map = (List<SimpleEntry<Long, HashMap<Integer, Long>>>) data.get(yAlias);
                if (value_map == null) {
                    value_map = new ArrayList<SimpleEntry<Long, HashMap<Integer, Long>>>();
                    data.put(yAlias, value_map);
                }
                value_map.add((SimpleEntry<Long, HashMap<Integer, Long>>) yVal);
            }
            else
            {
                List<SimpleEntry<Long, Integer>> value = (List<SimpleEntry<Long, Integer>>) data.get(yAlias);
                if (value == null) {
                    value = new ArrayList<>();
                    data.put(yAlias, value);
                }
                value.add((SimpleEntry<Long, Integer>) yVal);
            }
        } else {
            data.put(yAlias, yVal);

            if (dataPoint.getyAxis() != null && dataPoint.getyAxis().isFetchMinMax()) {
                data.put(yAlias + ".min", minYVal);
                data.put(yAlias + ".max", maxYVal);
            }
        }
    }

    protected String getyAlias(ReportDataPointContext dataPoint, ReportBaseLineContext baseLine) {
        return baseLine == null ? dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) : dataPoint.getAliases().get(baseLine.getBaseLine().getName());
    }

    protected String getxAlias(ReportContext report) {
        return report.getxAlias() == null ? FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS : report.getxAlias();
    }
    protected Object getBaseLineAdjustedXVal(Object xVal, ReportFieldContext xAxis, ReportBaseLineContext baseLine) throws Exception {
        return getBaseLineAdjustedXVal(xVal, xAxis, baseLine, null);
    }
    protected Object getBaseLineAdjustedXVal(Object xVal, ReportFieldContext xAxis, ReportBaseLineContext baseLine, AggregateOperator aggr) throws Exception {
        if (baseLine != null) {
            switch (xAxis.getField().getDataTypeEnum()) {
                case DATE:
                case DATE_TIME:
                    BaseLineContext.AdjustType adjustType = baseLine.getAdjustTypeEnum();
                    if(adjustType != null && adjustType == BaseLineContext.AdjustType.FULL_MONTH_DATE &&
                            aggr != null && aggr instanceof DateAggregateOperator &&
                            (((DateAggregateOperator) aggr) == DateAggregateOperator.MONTHANDYEAR)
                    )
                    {
                        ZonedDateTime xValZdt = DateTimeUtil.getDateTime((long)xVal);
                        xValZdt = xValZdt.plusMonths(1);
                        return DateTimeUtil.getMonthEndTime(xValZdt.getMonthValue(), xValZdt.getYear());
                    }
                    if(xVal instanceof UnsignedLong){
                        xVal = ((UnsignedLong)xVal).longValue();
                    }
                    if(xVal != null && aggr != null && aggr instanceof DateAggregateOperator &&
                            (((DateAggregateOperator) aggr) == DateAggregateOperator.MONTHANDYEAR) && adjustType != null && adjustType == BaseLineContext.AdjustType.MONTH_AND_DATE)
                    {
                        long long_value = (long) xVal + baseLine.getDiff();
                        ZonedDateTime dateTime = DateTimeUtil.getZonedDateTime(long_value);
                        if(dateTime.getYear() % 4 == 0 && dateTime.getMonthValue() == 1){
                            return (long) xVal + baseLine.getDiff();
                        }else{
                            return (long) xVal + baseLine.getDiff() + 86400000;
                        }
                    }
                    return (long) xVal + baseLine.getDiff();
                default:
                    break;
            }
        }
        return xVal;
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val, Object actualxVal, boolean handleEnum, HashMap<ReportFieldContext, List<Long>> dpLookUpMap, HashMap<ReportFieldContext, List<Long>> dpMultiLookUpMap) throws Exception
    {
        return formatVal(reportFieldContext, aggr, val, actualxVal, handleEnum, dpLookUpMap, dpMultiLookUpMap, null);
    }
    private Object constructBooleanResponse(FacilioField field, Map<String, Object> prop, AggregateOperator aggr, Object xVal)throws Exception
    {
        Object true_val_count = prop.get("count_1");
        Object false_val_count = prop.get("count_0");

//        String true_label = ((BooleanField)field).getTrueVal();
//        String false_label = ((BooleanField)field).getFalseVal();
        Map<Integer, Object> bool_val_map = new HashMap<>();
        bool_val_map.put(0, false_val_count);
        bool_val_map.put(1, true_val_count);
        return new SimpleEntry<>((Long)xVal, bool_val_map);
    }
    private Object constructEnumResponse(FacilioField field, Map<String, Object> prop, AggregateOperator aggr, Object xVal)throws Exception
    {
        Map<Integer, Object> bool_val_map = new HashMap<>();
        Map<Integer, Object> enumMap = ((EnumField) field).getEnumMap();
        for(Map.Entry<Integer, Object> pair: enumMap.entrySet())
        {
            String option_label = (String) pair.getValue();
            if(prop != null && prop.containsKey(option_label))
            {
                Object option_value = prop.get(option_label);
                bool_val_map.put(pair.getKey() , option_value);
            }
        }
        return new SimpleEntry<>((Long)xVal, bool_val_map);
    }
    private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val, Object actualxVal, boolean handleEnum, HashMap<ReportFieldContext, List<Long>> dpLookUpMap, HashMap<ReportFieldContext, List<Long>> dpMultiLookUpMap, Map<String, Object> prop) throws Exception {
        FacilioField field = reportFieldContext.getField();
        if(field != null && field.getDataTypeEnum() == FieldType.BOOLEAN && aggr != null && aggr == BmsAggregateOperators.CommonAggregateOperator.COUNT)
        {
            return constructBooleanResponse(field, prop, aggr, actualxVal);
        }
        else if(field != null && field.getDataTypeEnum() == FieldType.ENUM && aggr != null && aggr == BmsAggregateOperators.CommonAggregateOperator.COUNT){
            return constructEnumResponse(field, prop, aggr, actualxVal);
        }

        if (val == null) {
            return "";
        }

        switch (field.getDataTypeEnum()) {
            case DECIMAL:
                val = DECIMAL_FORMAT.format(val);
                break;
            case BOOLEAN:
                if (val.toString().equals("true")) {
                    val = 1;
                } else if (val.toString().equals("false")) {
                    val = 0;
                }
                if (handleEnum && actualxVal != null) {
                    val = new SimpleEntry<Long, Integer>((Long) actualxVal, (Integer) val);
                }
                break;
            case ENUM:
                if (handleEnum && actualxVal != null) {
                    val = new SimpleEntry<Long, Integer>((Long) actualxVal, (Integer) val);
                }
                break;
            case DATE:
            case DATE_TIME:
                if (aggr != null && aggr instanceof DateAggregateOperator) {
                    if(val instanceof UnsignedLong){
                        val = ((UnsignedLong) val).longValue();
                    }
                    val = ((DateAggregateOperator) aggr).getAdjustedTimestamp((long) val);
                    if(((DateAggregateOperator) aggr) == DateAggregateOperator.WEEKDAY && (long) val > 0 ){
                        ZonedDateTime dateTime = DateTimeUtil.getDateTime((long)val);
                        if(dateTime != null && dayOfWeek != null && dayOfWeek.containsKey(dateTime.getDayOfWeek())){
                            val = dayOfWeek.get(dateTime.getDayOfWeek());
                        }
                    }
                }

                break;
            case NUMBER:
                if (StringUtils.isNotEmpty(field.getName()) && field.getName().equals("siteId")) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    updateLookupMap(reportFieldContext, field.getFieldId(), null, modBean.getModule("site"));
                }
                break;
            case LOOKUP:
                if (val instanceof Map) {
                    val = ((Map) val).get("id");
                }
                List<Long> ids = dpLookUpMap.getOrDefault(reportFieldContext, new ArrayList<Long>());
                LookupField lookupField = (LookupField) field;
                FacilioModule lookupModule = lookupField.getLookupModule();
                if(lookupModule != null && lookupModule.getName().equals("vendors")) {
                    List<Long> vals = new ArrayList<>();
                    if(val != null) {
                        vals.add((Long) val);
                    }
                    Boolean isDeleted = isDeletedLookup(lookupField.getSpecialType(), lookupField.getLookupModule(), vals);
                    if(isDeleted){
                        return "deleted";
                    }
                }
                ids.add((Long) val);
                dpLookUpMap.put(reportFieldContext, ids);
                break;
            case MULTI_LOOKUP:
                if (val instanceof Map) {
                    val = ((Map) val).get("id");
                }
                List<Long> multi_lookup_val_ids = dpMultiLookUpMap.getOrDefault(reportFieldContext, new ArrayList<Long>());
                multi_lookup_val_ids.add((Long) val);
                dpMultiLookUpMap.put(reportFieldContext, multi_lookup_val_ids);
                break;
            default:
                break;
        }
        return val;
    }

    private void updateLookupMap(ReportFieldContext reportFieldContext, long fieldId, String specialType, FacilioModule lookupModule) throws Exception {
        if (MapUtils.isEmpty(reportFieldContext.getLookupMap())) {
            Map<Long, Object> lookupMap = new HashMap<>();
            if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
                List list = LookupSpecialTypeUtil.getObjects(specialType, null);
                lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
            } else {
                lookupMap = this.getLookUpMap(specialType, lookupModule, null);
            }
//			labelMap.put(fieldId, lookupMap);
            reportFieldContext.setLookupMap(lookupMap);
        }
    }
    private void updateLookupMap(HashMap<ReportFieldContext, List<Long>> dpLookUpMap, AggregateOperator aggr, HashMap<ReportFieldContext, List<Long>> dpMultiLookUpMap)throws Exception{
        updateLookupMap(dpLookUpMap, aggr);
        updateLookupMap(dpMultiLookUpMap, aggr);
    }
    private void updateLookupMap(HashMap<ReportFieldContext, List<Long>> dpLookUpMap, AggregateOperator aggr) throws Exception {
        if (MapUtils.isNotEmpty(dpLookUpMap)) {
            for (Map.Entry<ReportFieldContext, List<Long>> entry : dpLookUpMap.entrySet()) {
                Map<Long, Object> lookupMap;

                ReportFieldContext reportFieldContext = entry.getKey();
                List<Long> ids = entry.getValue();
                LookupField lookupField = null;
                MultiLookupField multiLookupField = null;
                String specialType = null;
                FacilioModule lookupModule = null;
                if(reportFieldContext.getField() instanceof LookupField)
                {
                    lookupField = (LookupField)reportFieldContext.getField();
                    specialType = lookupField.getSpecialType();
                    lookupModule = lookupField.getLookupModule();
                }
                else if(reportFieldContext.getField() instanceof MultiLookupField){
                    multiLookupField = (MultiLookupField) reportFieldContext.getField();
                    specialType = multiLookupField.getSpecialType();
                    lookupModule = multiLookupField.getLookupModule();
                }

                if (aggr != null && aggr instanceof SpaceAggregateOperator && (reportFieldContext.getModuleName().equals(FacilioConstants.ModuleNames.ASSET_BREAKDOWN))) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    lookupModule = modBean.getModule(aggr.getStringValue());
                }
                if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
                    List list = LookupSpecialTypeUtil.getObjects(specialType, null);
                    lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
                } else {
                    lookupMap = this.getLookUpMap(specialType, lookupModule, ids);
                }
                reportFieldContext.setLookupMap(lookupMap);
            }
        }
    }

    protected Map<Long, Object> getLookUpMap(String specialType, FacilioModule lookupModule, List<Long> ids) throws Exception {
        Map<Long, Object> lookupMap = new HashMap<>();

        String moduleName = null;
        if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
            moduleName = specialType;
        } else {
            moduleName = lookupModule.getName();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField mainField = modBean.getPrimaryField(moduleName);

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(mainField);
        selectFields.add(FieldFactory.getIdField(lookupModule));
        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder()
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(lookupModule, false))
                .select(selectFields)
//                .fetchDeleted()
                .module(lookupModule);
        if (CollectionUtils.isNotEmpty(ids)) {
            builder.andCondition(CriteriaAPI.getIdCondition(ids, lookupModule));
        }

        List<Map<String, Object>> asProps = builder.getAsProps();

        Map<Long, Object> lookupValueMap = null;
        if (mainField instanceof LookupField) {
            LookupField lookupField = (LookupField) mainField;
            lookupValueMap = getLookUpMap(lookupField.getSpecialType(), lookupField.getLookupModule(), null);
        }

        lookupMap = new HashMap<>();
        for (Map<String, Object> map : asProps) {
            if (mainField instanceof LookupField) {
                long id = (Long) map.get("id");
                long lookupFieldId = (Long) ((Map<String, Object>) map.get(mainField.getName())).get("id");
                lookupMap.put(id, (String) lookupValueMap.get(lookupFieldId));
            } else {
                lookupMap.put((Long) map.get("id"), (String) map.get(mainField.getName()));
            }
        }
        return lookupMap;
    }
    private Boolean isDeletedLookup(String specialType, FacilioModule lookupModule, List<Long> ids)
    {
        try
        {
            String moduleName = null;
            if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
                moduleName = specialType;
            } else {
                moduleName = lookupModule.getName();
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField mainField = modBean.getPrimaryField(moduleName);

            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.add(mainField);
            selectFields.add(FieldFactory.getIdField(lookupModule));
            SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder()
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(lookupModule, false))
                    .select(selectFields)
                    .module(lookupModule);
            if (CollectionUtils.isNotEmpty(ids)) {
                builder.andCondition(CriteriaAPI.getIdCondition(ids, lookupModule));
            }

            List<Map<String, Object>> asProps = builder.getAsProps();
            if (asProps == null || (asProps != null && asProps.size() <= 0)) {
               return true;
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    private void getWeeklyMapFromRange(long startTime)throws Exception
    {
        dayOfWeek = new JSONObject();
        ZonedDateTime dateTime =DateTimeUtil.getDateTime(startTime);
        for(int i=0 ;i < 7 ; i++)
        {
            dayOfWeek.put(dateTime.getDayOfWeek(), DateTimeUtil.getDayStartTimeOf(dateTime.toEpochSecond() * 1000));
            dateTime = dateTime.plusDays(1);
        }
    }

    private void constructOptionsMapForEnumAndBoolean(Boolean report_v2, JSONObject dataPoints_enumMap, ReportDataPointContext dataPoint)throws Exception
    {
        if(report_v2 != null && dataPoint.isHandleEnum() && (dataPoint.getyAxis() != null  && dataPoint.getyAxis().getField() != null && (dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.ENUM) && dataPoint.getyAxis().getAggrEnum() != null && dataPoint.getyAxis().getAggrEnum() == BmsAggregateOperators.CommonAggregateOperator.COUNT))
        {
            Map<Integer, Object> enumMap = new HashMap<>();
            if(dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.BOOLEAN)
            {
                enumMap.put(0, ((BooleanField) dataPoint.getyAxis().getField()).getFalseVal() != null ? ((BooleanField) dataPoint.getyAxis().getField()).getFalseVal() : "False");
                enumMap.put(1, ((BooleanField) dataPoint.getyAxis().getField()).getTrueVal() != null ? ((BooleanField) dataPoint.getyAxis().getField()).getTrueVal() : "True");
            }
            else if(dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.ENUM )
            {
                enumMap = ((EnumField) dataPoint.getyAxis().getField()).getEnumMap();
            }
            if(!enumMap.isEmpty())
            {
                for(Map.Entry<String, String> aliasPair : dataPoint.getAliases().entrySet())
                {
                    dataPoints_enumMap.put(new StringBuilder(aliasPair.getValue()).append(".options").toString(), enumMap);
                }
            }
        }
    }
}
