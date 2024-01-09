package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2DimensionContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.report.ReportDynamicKpiContext;
import com.facilio.ch.ClickhouseUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import com.facilio.service.FacilioService;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.facilio.modules.BmsAggregateOperators.*;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.readingkpi.ReadingKpiAPI.getReadingKpi;
import static com.facilio.readingkpi.ReadingKpiAPI.getResultForDynamicKpi;

public class V2FetchAnalyticsReportDataCommand extends FacilioCommand
{
    private static final Logger LOGGER = Logger.getLogger(V2FetchAnalyticsReportDataCommand.class.getName());
    private boolean isClickHouseEnabled;
    private ModuleBean modBean;
    private FacilioModule baseModule;
    private JSONObject dashboard_user_filter=null;
    private V2ReportContext report_v2;
    private int totalRecordCount=0;
    private Boolean isClickhouseAggrTableEnabled=false;
    private LinkedHashMap<String, String> moduleVsAlias = new LinkedHashMap<String, String>();
    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        isClickhouseAggrTableEnabled= (Boolean) context.get("clickhouse");
        context.put("isV2Analytics",true);
        report_v2 = context.get("report_v2") != null ? (V2ReportContext) context.get("report_v2") : (V2ReportContext) context.get("v2_report");
        dashboard_user_filter = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_USER_FILTER_VALUE);
        isClickHouseEnabled = (Boolean) context.get("isClickHouseEnabled");
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        this.setXAggrForHeatMap(report_v2, report);
        V2AnalyticsOldUtil.calculateBaseLineRange(report);
        List<ReportDataContext> reportData = new ArrayList<>();
        List<ReportDataPointContext> dataPoints = new ArrayList<>(report.getDataPoints());
        ReportDataPointContext sortPoint = V2AnalyticsOldUtil.getSortPoint(dataPoints);
        ReportDataContext sortedData = null;
        if (sortPoint != null) {
            sortedData = fetchAnalyticDataFromGroupedDP(Collections.singletonList(sortPoint), report, false, null);
            reportData.add(sortedData);
        }
        List<List<ReportDataPointContext>> groupedDataPoints = groupSameModuleDataPointsAsList(dataPoints);
        if (groupedDataPoints != null && !groupedDataPoints.isEmpty())
        {
            for (int i = 0; i < groupedDataPoints.size(); i++) {
                List<ReportDataPointContext> dataPointList = groupedDataPoints.get(i);
                if(dataPointList.get(0) != null && dataPointList.get(0).getDynamicKpi() != null && dataPointList.get(0).getDynamicKpi().getDynamicKpi() != null)
                {
                    LOGGER.info("this is for dynamic kpi");
                    ReportDataContext data = fetchLiveKpiDataForAnalytics(report, dataPointList);
                    reportData.add(data);
                }
                else
                {
                    ReportDataContext data = fetchAnalyticDataFromGroupedDP(dataPointList, report, sortPoint != null, sortPoint == null ? null : sortedData.getxValues());
                    reportData.add(data);
                }
            }
            if (dataPoints.isEmpty()) {
                dataPoints.add(groupedDataPoints.get(0).get(0));
            }
            report.setDataPoints(dataPoints);
        }
        context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
        context.put("totalRecordCount", totalRecordCount);
        return false;
    }
    private List<List<ReportDataPointContext>> groupSameModuleDataPointsAsList(List<ReportDataPointContext> dpList)throws Exception
    {
        List<List<ReportDataPointContext>> groupedList = null;
        if (dpList != null && !dpList.isEmpty())
        {
            groupedList = new ArrayList<>();
            for (ReportDataPointContext dataPoint : dpList)
            {
                if (dataPoint.getTypeEnum() == null) {
                    dataPoint.setType(ReportDataPointContext.DataPointType.MODULE.getValue());
                }
                if(dataPoint.getTypeEnum() == ReportDataPointContext.DataPointType.MODULE)
                {
                    if(dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM) && (dataPoint.getyAxis().getAggr() == CommonAggregateOperator.COUNT.getValue()))
                    {
                        dataPoint.setHandleEnum(true);
                    }
                    else if ((dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE)) && (dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)) && (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getValue())) {
                        dataPoint.getyAxis().setAggr(null);
                        dataPoint.setHandleEnum(true);
                    }
                }
                groupMatchedDataPointList(dataPoint, groupedList);
            }
        }
        return groupedList;
    }
    private void groupMatchedDataPointList(ReportDataPointContext dataPoint, List<List<ReportDataPointContext>> groupedList) throws Exception
    {
        for (List<ReportDataPointContext> dpList : groupedList)
        {
            ReportDataPointContext rdp = dpList.get(0);
            if(rdp.getDynamicKpi() == null && rdp.getxAxis().getField().equals(dataPoint.getxAxis().getField())
                    && rdp.getyAxis().getModule().equals(dataPoint.getyAxis().getModule())
                    && Objects.equals(rdp.getOrderBy(), dataPoint.getOrderBy())
                    && rdp.isHandleEnum() == dataPoint.isHandleEnum()
                    && V2AnalyticsOldUtil.isSameTable(rdp, dataPoint)
                    && (!rdp.isRightInclusive() == !dataPoint.isRightInclusive())
                    && Objects.equals(rdp.getV2Criteria() , dataPoint.getV2Criteria())
                    && Objects.equals(rdp.getDynamicKpi() , dataPoint.getDynamicKpi()))

            {
                ReportDataPointContext.OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? ReportDataPointContext.OrderByFunction.NONE : rdp.getOrderByFuncEnum();
                ReportDataPointContext.OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? ReportDataPointContext.OrderByFunction.NONE : dataPoint.getOrderByFuncEnum();
                if (rdpFunc == dataPointFunc)
                {
                    dpList.add(dataPoint);
                }
            }
        }
        List<ReportDataPointContext> dataPointList = new ArrayList<>();
        dataPointList.add(dataPoint);
        groupedList.add(dataPointList);
    }
    private ReportDataContext fetchAnalyticDataFromGroupedDP(List<ReportDataPointContext> dataPointList, ReportContext report, boolean hasSortedDp, String xValues) throws Exception
    {
        ReportDataContext data = new ReportDataContext();
        Set<FacilioModule> addedModules = new HashSet<>();
        Set<FacilioModule> baseLineAddedModules = new HashSet<>();
        /**
         * fields will be used to collect all the fields which will be available as selected columns in select query
         */
        List<FacilioField> fields = new ArrayList<>();
        data.setDataPoints(dataPointList);
        ReportDataPointContext dp = dataPointList.get(0);
        baseModule = dp.getxAxis().getModule();
        String aggregated_table_name = setCHAggrModuleAsBaseModuleBaseOnAggregation(report_v2, report, baseModule.getTypeEnum(), report.getDateRange());
        if (aggregated_table_name != null && !dp.isHandleEnum())
        {
            baseModule = V2AnalyticsOldUtil.constructAndGetAggregatedModule(baseModule, aggregated_table_name);
        }
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = setBaseModuleAggregation();
        addedModules.add(baseModule);
        V2AnalyticsOldUtil.joinModuleIfRequired(moduleVsAlias, baseModule, dp.getyAxis(), selectBuilder, addedModules);
        V2AnalyticsOldUtil.applyOrderByAndLimit(dp, selectBuilder, report.getxAggrEnum(), ReportContext.ReportType.READING_REPORT, aggregated_table_name);
        StringJoiner groupBy = new StringJoiner(",");
        FacilioField xAggrField = applyAnalyticXAggregation(dp, report.getxAggrEnum(), groupBy, selectBuilder, fields, addedModules, aggregated_table_name);
        V2AnalyticsOldUtil.setGroupByTimeAggregator(dp, report.getgroupByTimeAggrEnum(), groupBy);
        applyAnalyticYAggregation(dataPointList, fields, aggregated_table_name);
        applyGroupByAggregation(groupBy, dataPointList, fields);
        if(groupBy != null) {
            selectBuilder.groupBy(groupBy.toString());
        }
        this.setBooleanOrEnumSelectFields(dp, fields);
        List<FacilioField> cloneFields = new ArrayList<>();
        cloneFields.addAll(fields.stream().filter(field -> field != null).map(FacilioField::clone).collect(Collectors.toList()));
        selectBuilder.select(cloneFields);

        boolean noMatch = hasSortedDp && (xValues == null || xValues.isEmpty());
        Map<String, List<Map<String, Object>>> props = new HashMap<>();
        baseLineAddedModules.addAll(addedModules);
        List<Map<String, Object>> dataProps = noMatch ? Collections.EMPTY_LIST : fetchAnalyitcsReportData(report, dp, selectBuilder, null, xAggrField, xValues, addedModules, aggregated_table_name);
        props.put(FacilioConstants.Reports.ACTUAL_DATA, dataProps);

        if (dp.getLimit() != -1 && xValues == null) {
            data.setxValues(V2AnalyticsOldUtil.getXValues(dataProps, dp.getxAxis().getFieldName(), dp.getxAxis().getDataTypeEnum()));
            if (data.getxValues() == null || data.getxValues().isEmpty()) {
                noMatch = true;
            }
        }
        if (report.getBaseLines() != null && !report.getBaseLines().isEmpty())
        {
            for (ReportBaseLineContext reportBaseLine : report.getBaseLines())
            {
                props.put(reportBaseLine.getBaseLine().getName(), noMatch ? Collections.EMPTY_LIST : fetchAnalyitcsReportData(report, dp, selectBuilder, reportBaseLine, xAggrField, xValues, baseLineAddedModules, aggregated_table_name));
                data.addBaseLine(reportBaseLine.getBaseLine().getName(), reportBaseLine);
            }
        }
        data.setProps(props);
        return data;
    }

    private SelectRecordsBuilder<ModuleBaseWithCustomFields> setBaseModuleAggregation()throws Exception
    {
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(baseModule) //Assuming X to be the base module
                .setAggregation();
        return selectBuilder;
    }

    private FacilioField applyAnalyticXAggregation(ReportDataPointContext dp, AggregateOperator xAggr, StringJoiner groupBy, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, List<FacilioField> fields, Set<FacilioModule> addedModules, String aggregated_table_name)throws Exception
    {
        FacilioField xAggrField = null;
        if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0)
        {
            if(xAggr == null || !(xAggr instanceof DateAggregateOperator || xAggr instanceof SpaceAggregateOperator)){
                xAggrField = dp.getxAxis().getField().clone();
            }
            else if(xAggr != null && xAggr instanceof SpaceAggregateOperator)
            {
                xAggrField = V2AnalyticsOldUtil.applySpaceAggregation(dp, xAggr, selectBuilder, addedModules, dp.getxAxis().getField()).clone();
            }
            else if(xAggr != null && xAggr instanceof DateAggregateOperator)
            {
                if(isClickHouseEnabled)
                {
                    AggregateOperator aggr_table_operator = BmsAggregateOperators.getCHAggregatedTableOperator(xAggr.getValue());
                    if(aggr_table_operator != null && aggregated_table_name != null)
                    {
                        FacilioField aggr_XField = FieldFactory.getDefaultField("ttime", "Date", new StringBuilder().append(aggregated_table_name).append(".DATE").toString(), FieldType.DATE);
                        xAggrField = aggr_table_operator.getSelectField(aggr_XField);
                        FacilioField select_field = aggr_XField.clone();
                        select_field.setColumnName(new StringBuilder("toUnixTimestamp(MIN( ").append(aggregated_table_name).append(".DATE )) * 1000").toString());
                        fields.add(select_field);
                    }else {
                        xAggrField = dp.isRightInclusive() && BmsAggregateOperators.getRightInclusiveCHAggregateOperator(xAggr.getValue()) != null ? (BmsAggregateOperators.getRightInclusiveCHAggregateOperator(xAggr.getValue())).getSelectField(dp.getxAxis().getField()).clone() : BmsAggregateOperators.getCHAggregateOperator(xAggr.getValue()).getSelectField(dp.getxAxis().getField()).clone();
                    }
                }else{
                    xAggrField = dp.isRightInclusive() && BmsAggregateOperators.getRightInclusiveAggr(xAggr.getValue()) != null ? (BmsAggregateOperators.getRightInclusiveAggr(xAggr.getValue())).getSelectField(dp.getxAxis().getField()).clone() : xAggr.getSelectField(dp.getxAxis().getField()).clone();
                }
                if(aggregated_table_name == null)
                {
                    DayOfWeek dayOfWeek = DateTimeUtil.getWeekFields().getFirstDayOfWeek();
                    if (dayOfWeek == DayOfWeek.MONDAY && xAggr == DateAggregateOperator.WEEKANDYEAR) {
                        xAggrField = isClickHouseEnabled ? CHDateAggregateOperator.MONDAY_START_WEEKLY.getSelectField(dp.getxAxis().getField()).clone() : DateAggregateOperator.MONDAY_START_WEEKANDYEAR.getSelectField(dp.getxAxis().getField()).clone();
                    }
                }
            }

            if(aggregated_table_name != null && (xAggr == null || !(xAggr instanceof DateAggregateOperator || xAggr instanceof SpaceAggregateOperator)) && !dp.isMultiMeasureChartType())
            {
                groupBy.add(new StringBuilder(aggregated_table_name).append(".").append(xAggrField.getColumnName()).toString());
                FacilioField aggr_XField = FieldFactory.getDefaultField(xAggrField.getName(), xAggrField.getDisplayName(), new StringBuilder().append(aggregated_table_name).append(".").append(xAggrField.getColumnName()).toString(), xAggrField.getDataTypeEnum());
                fields.add(aggr_XField);
            }
            else if(!dp.isMultiMeasureChartType())
            {
                groupBy.add(xAggrField.getCompleteColumnName());
            }
            if(aggregated_table_name == null || (xAggr != null && xAggr instanceof SpaceAggregateOperator) || dp.isMultiMeasureChartType()){
                fields.add(xAggr instanceof DateAggregateOperator ? ((DateAggregateOperator) xAggr).getTimestampField(dp.getxAxis().getField()) : xAggrField);
            }
        }
        else if(xAggr == null || xAggr == CommonAggregateOperator.ACTUAL || dp.isHandleEnum()){
            fields.add(dp.getxAxis().getField().clone());
        }
        return xAggrField;
    }

    private void applyAnalyticYAggregation(List<ReportDataPointContext> dpList, List<FacilioField> fields, String aggr_tableName)throws Exception
    {
        for (ReportDataPointContext dataPoint : dpList)
        {
            if (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == 0) {
                fields.add(dataPoint.getyAxis().getField().clone());
            }
            else
            {
                FacilioField facilioField = dataPoint.getyAxis().getField().clone();
                FacilioField aggrField = dataPoint.getyAxis().getAggrEnum().getSelectField(facilioField).clone();
                if(aggr_tableName != null && dataPoint.getyAxis().getAggrEnum() instanceof NumberAggregateOperator) {
                    aggrField = V2AnalyticsOldUtil.getAggregatedYField(facilioField, baseModule, dataPoint.getyAxis().getAggrEnum().getStringValue().toUpperCase());
                }
                aggrField.setName(ReportUtil.getAggrFieldName(aggrField, dataPoint.getyAxis().getAggrEnum()));
                fields.add(aggrField);
            }
        }
    }

    private void applyGroupByAggregation(StringJoiner groupBy, List<ReportDataPointContext> dpList, List<FacilioField> fields)throws Exception
    {
        if (groupBy.length() > 0)
        {
            for (ReportDataPointContext dataPoint : dpList)
            {
                if (dataPoint.getyAxis().isFetchMinMax())
                {
                    FacilioField minField = NumberAggregateOperator.MIN.getSelectField(dataPoint.getyAxis().getField());
                    minField.setName(dataPoint.getyAxis().getFieldName() + "_min");
                    fields.add(minField);

                    FacilioField maxField = NumberAggregateOperator.MAX.getSelectField(dataPoint.getyAxis().getField());
                    maxField.setName(dataPoint.getyAxis().getFieldName() + "_max");
                    fields.add(maxField);
                }
            }
        }
    }

    private List<Map<String, Object>> fetchAnalyitcsReportData(ReportContext report, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext baseLine, FacilioField xAggrField, String xValues, Set<FacilioModule> addedModules, String aggr_table_name)throws Exception
    {
        V2AnalyticsOldUtil.applyResourcesJoinOnMeter(selectBuilder, baseModule, report_v2.getDimensions(), dataPoint, addedModules);
        SelectRecordsBuilder<ModuleBaseWithCustomFields> newSelectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(selectBuilder);
        V2AnalyticsOldUtil.applyTimeFilterCriteria(report, dataPoint, newSelectBuilder, baseLine, aggr_table_name);
        V2AnalyticsOldUtil.applyDashboardUserFilterCriteria(baseModule, dashboard_user_filter, dataPoint, newSelectBuilder,addedModules);
        V2AnalyticsOldUtil.applyMeasureCriteriaV2(moduleVsAlias, xAggrField, baseModule, dataPoint, newSelectBuilder, xValues, addedModules);
        V2AnalyticsOldUtil.getAndSetRelationShipSubQuery(report.getDataPoints(), dataPoint, newSelectBuilder, moduleVsAlias);
        V2AnalyticsOldUtil.applyAnalyticGlobalFilterCriteria(baseModule, dataPoint, newSelectBuilder, report_v2 != null ? report_v2.getG_criteria() : null, addedModules);
        if(addedModules != null && addedModules.size() == 1) {
            V2AnalyticsOldUtil.checkAndApplyJoinForScopingCriteria(newSelectBuilder, addedModules, baseModule);
        }else{
            V2AnalyticsOldUtil.addDeletedCriteria(addedModules, newSelectBuilder);
        }

        List<Map<String, Object>> props = null;
        if(isClickHouseEnabled) {
            if(V2AnalyticsOldUtil.isWithCount(report_v2.getPagination())){
                SelectRecordsBuilder<ModuleBaseWithCustomFields> countBuilder = new SelectRecordsBuilder<>(newSelectBuilder);
                props = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.CLICKHOUSE,
                        () -> countBuilder.getAsProps());
                if(props != null && props.size() > 0){
                    totalRecordCount = props.size();
                }
            }
            V2AnalyticsOldUtil.setPaginationForReportSelectBuilder(report_v2, newSelectBuilder);
            props = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.CLICKHOUSE,
                    () -> newSelectBuilder.getAsProps());
            LOGGER.debug("SELECT BUILDER EXECUTED IN CLICKHOUSE--- " + newSelectBuilder);
        }
        else
        {
            if(V2AnalyticsOldUtil.isWithCount(report_v2.getPagination()))
            {
                SelectRecordsBuilder<ModuleBaseWithCustomFields> countBuilder = new SelectRecordsBuilder<>(newSelectBuilder);
                props = countBuilder.getAsProps();
                if(props != null && props.size() > 0){
                    totalRecordCount = props.size();
                }
            }
            V2AnalyticsOldUtil.setPaginationForReportSelectBuilder(report_v2, newSelectBuilder);
            props = newSelectBuilder.getAsProps();
            LOGGER.debug("SELECT BUILDER EXECUTED IN MYSQL--- " + newSelectBuilder);
        }
        return props;
    }
    private ReportDataContext fetchLiveKpiDataForAnalytics(ReportContext report, List<ReportDataPointContext> dataPointList)throws Exception
    {
        ReportDynamicKpiContext dynamicKpi = dataPointList.get(0).getDynamicKpi();
        ReadingKPIContext dynKpi = getReadingKpi(Long.valueOf(dynamicKpi.getDynamicKpi()));
        DateRange dateRange = report.getDateRange();
        Map<Long, List<Map<String, Object>>> resultForDynamicKpi = null;
        Map<String, List<Map<String, Object>>> props = new HashMap<>();
        ReportDataContext kpiData = new ReportDataContext();
        ReportBaseLineContext reportBaseLine = null;
        DateRange dynamicBaseLineRange = null;
        Map<Long, List<Map<String, Object>>> resultForBaseLine = null;
        if(report.getBaseLines() != null && !report.getBaseLines().isEmpty())
        {
            reportBaseLine = report.getBaseLines().get(0);
            dynamicBaseLineRange = reportBaseLine.getBaseLineRange();
            kpiData.addBaseLine(reportBaseLine.getBaseLine().getName(), reportBaseLine);
        }

        FacilioChain chain = V2AnalyticsTransactionChain.getCategoryModuleChain();
        FacilioContext kpi_context = chain.getContext();
        kpi_context.put("categoryId", dynamicKpi.getCategory());
        kpi_context.put("type", dynKpi.getResourceTypeEnum().getName().toLowerCase());
        chain.execute();
        List<Long> parentIds = V2AnalyticsOldUtil.getAssetIdsFromCriteria((String)kpi_context.get("moduleName"), dataPointList.get(0).getV2Criteria());

        if(parentIds != null && parentIds.size() > 0)
        {
            dynamicKpi.setParentId(parentIds);
            resultForDynamicKpi = getResultForDynamicKpi(Collections.singletonList(dynamicKpi.getParentId().get(0)), dateRange, report.getxAggrEnum(), dynKpi.getNs());
            props.put(FacilioConstants.Reports.ACTUAL_DATA, resultForDynamicKpi.get(dynamicKpi.getParentId().get(0)));
            if (reportBaseLine!=null) {
                resultForBaseLine = getResultForDynamicKpi(Collections.singletonList(dynamicKpi.getParentId().get(0)), dynamicBaseLineRange,report.getxAggrEnum(), dynKpi.getNs());
                props.put(reportBaseLine.getBaseLine().getName(), resultForBaseLine.get(dynamicKpi.getParentId().get(0)));
            }
        }
        else
        {
            resultForDynamicKpi = getResultForDynamicKpi(parentIds, dateRange, report.getxAggrEnum(), dynKpi.getNs());
            if (reportBaseLine!=null) {
                resultForBaseLine = getResultForDynamicKpi(parentIds, dynamicBaseLineRange,report.getxAggrEnum(), dynKpi.getNs());
            }
            for(Long parentId : parentIds){
                props.put(FacilioConstants.Reports.ACTUAL_DATA, resultForDynamicKpi.get(parentId));
                if(resultForBaseLine!=null){
                    props.put(reportBaseLine.getBaseLine().getName(), resultForBaseLine.get(parentId));
                }
            }
        }
        kpiData.setDataPoints(dataPointList);
        kpiData.setProps(props);
        return kpiData;
    }

    private void setBooleanOrEnumSelectFields(ReportDataPointContext dataPoint, List<FacilioField> fields)throws Exception
    {
        if(dataPoint.isHandleEnum() && dataPoint.getyAxis().getAggrEnum() != null)
        {
            if(dataPoint.getyAxis().getAggr() == CommonAggregateOperator.COUNT.getValue())
            {
                if(dataPoint.getyAxis().getField() != null &&  dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.BOOLEAN) {
                    fields.add(this.getTrueCountSelectField(dataPoint.getyAxis().getField()));
                    fields.add(this.getFalseCountSelectField(dataPoint.getyAxis().getField()));
                }
                else if(dataPoint.getyAxis().getField() != null &&  dataPoint.getyAxis().getField().getDataTypeEnum() == FieldType.ENUM){
                    fields.addAll(getEnumSelectField(dataPoint.getyAxis().getField()));
                }
            }
        }
    }
    private FacilioField getTrueCountSelectField(FacilioField field)
    {
        StringBuilder selectFieldString = new StringBuilder().append("SUM(CASE WHEN ").append(field.getCompleteColumnName()).append(" = 1 THEN 1 ELSE 0 END)");
        FacilioField selectField =  new FacilioField();
        selectField.setName("count_1");
        selectField.setDisplayName(field.getDisplayName());
        selectField.setColumnName(selectFieldString.toString());
        selectField.setFieldId(field.getFieldId());
        selectField.setDataType(FieldType.STRING);
        return selectField;
    }
    private FacilioField getFalseCountSelectField(FacilioField field)
    {
        StringBuilder selectFieldString = new StringBuilder().append("SUM(CASE WHEN ").append(field.getCompleteColumnName()).append(" = 0 THEN 1 ELSE 0 END)");
        FacilioField selectField =  new FacilioField();
        selectField.setName("count_0");
        selectField.setDisplayName(field.getDisplayName());
        selectField.setColumnName(selectFieldString.toString());
        selectField.setFieldId(field.getFieldId());
        selectField.setDataType(FieldType.STRING);
        return selectField;
    }

    private List<FacilioField> getEnumSelectField(FacilioField field) throws Exception
    {
        List<FacilioField> enum_field_list = null;
        Map<Integer, Object> enumMap = ((EnumField)field).getEnumMap();
        if(enumMap != null)
        {
            enum_field_list = new ArrayList<>();
            for(Map.Entry<Integer, Object> pair : enumMap.entrySet())
            {
                StringBuilder selectFieldString = new StringBuilder().append("SUM(CASE WHEN ").append(field.getCompleteColumnName()).append(" = ").append(pair.getKey()).append(" THEN ").append( 1 ).append(" END)");
                FacilioField selectField =  new FacilioField();
                selectField.setName((String)pair.getValue());
                selectField.setDisplayName(field.getDisplayName());
                selectField.setColumnName(selectFieldString.toString());
                selectField.setFieldId(field.getFieldId());
                selectField.setDataType(FieldType.STRING);
                enum_field_list.add(selectField);
            }
        }
        return enum_field_list;
    }
    private void setXAggrForHeatMap(V2ReportContext v2_report, ReportContext report)throws Exception
    {
        if(v2_report !=null && v2_report.getMeasures() != null && v2_report.getMeasures().size() == 1)
        {
            V2MeasuresContext measure = v2_report.getMeasures().get(0);
            if(measure.getHmAggr() != null && !"".equals(measure.getHmAggr()))
            {
                if(measure.getHmAggr().equals("hours"))
                {
                    report.setxAggr(BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY);
                }
                else if((measure.getHmAggr().equals("days") || measure.getHmAggr().equals("weeks")))
                {
                    report.setxAggr(BmsAggregateOperators.DateAggregateOperator.FULLDATE);
                }
            }
        }
    }

    private String setCHAggrModuleAsBaseModuleBaseOnAggregation(V2ReportContext v2Report, ReportContext report, FacilioModule.ModuleType moduleType, DateRange range)throws Exception
    {
        String aggregated_table_name = null;
        if(isClickHouseEnabled && isClickhouseAggrTableEnabled && moduleType == FacilioModule.ModuleType.READING)
        {
            if(v2Report.getDimensions().getDimensionTypeEnum() == V2DimensionContext.DimensionType.TIME && report.getxAggrEnum() != null && report.getxAggrEnum() == CommonAggregateOperator.ACTUAL && range != null && range.getStartTime() > 0 && range.getEndTime() > 0)
            {
                long diff = range.getEndTime() - range.getStartTime();
                if(diff > 259200000)
                {
                    report.setxAggr(DateAggregateOperator.FULLDATE);
                    v2Report.getDimensions().setxAggr(DateAggregateOperator.FULLDATE.getValue());
                }else{
                    isClickHouseEnabled = false;
                }
            }

            if(report.getxAggrEnum() != null && report.getxAggrEnum().getValue() > 0)
            {
                if (report.getxAggrEnum() instanceof DateAggregateOperator && report.getxAggrEnum() == DateAggregateOperator.HOURSOFDAYONLY) {
                    aggregated_table_name = ClickhouseUtil.getAggregatedTableName(baseModule.getTableName(), AccountUtil.getCurrentOrg().getTimezone(), "hourly");
                } else if(report.getxAggrEnum() instanceof DateAggregateOperator || report.getxAggrEnum() instanceof SpaceAggregateOperator){
                    aggregated_table_name = ClickhouseUtil.getAggregatedTableName(baseModule.getTableName(), AccountUtil.getCurrentOrg().getTimezone(), "daily");
                }
            }
        }
        return aggregated_table_name;
    }
}
