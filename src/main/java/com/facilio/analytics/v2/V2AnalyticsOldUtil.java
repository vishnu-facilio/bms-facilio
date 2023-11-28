package com.facilio.analytics.v2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.*;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleMetricContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.report.context.*;
import com.facilio.report.module.v2.context.V2ModuleMeasureContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.FilterUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.ns.context.NsFieldType.RELATED_READING;

public class V2AnalyticsOldUtil {
    private static final List<String> MEASURE_KEYS = Arrays.asList("relationship_id" ,"fieldId","parent_lookup_fieldId", "criteriaId");
    private static final List<ReadingAnalysisContext.ReportMode> SUPPORTED_PORTFOLIO_MODES = Arrays.asList(
            ReadingAnalysisContext.ReportMode.TIME_CONSOLIDATED,
            ReadingAnalysisContext.ReportMode.SITE,
            ReadingAnalysisContext.ReportMode.BUILDING,
            ReadingAnalysisContext.ReportMode.RESOURCE,
            ReadingAnalysisContext.ReportMode.SPACE,
            ReadingAnalysisContext.ReportMode.FLOOR
    );

    private static final HashMap<V2DimensionContext.DimensionType, String> DIMENSION_TYPE_VS_MODULES = new HashMap<V2DimensionContext.DimensionType, String>(){{
            put(V2DimensionContext.DimensionType.ASSET, "asset");
            put(V2DimensionContext.DimensionType.SITE, "site");
            put(V2DimensionContext.DimensionType.METER, "meter");
            put(V2DimensionContext.DimensionType.FLOOR, "floor");
            put(V2DimensionContext.DimensionType.SPACE, "space");
    }};


    public static ReportContext constructReportOld(V2ReportContext report, ReportContext report_context)throws Exception
    {
        report_context.setName(report.getName());
        report_context.setDateOperator(report.getTimeFilter().getDateOperatorEnum() != null ? report.getTimeFilter().getDateOperatorEnum() : DateOperators.BETWEEN);
        report_context.setDateValue(new StringBuilder().append(report.getTimeFilter().getStartTime()).append(", ").append(report.getTimeFilter().getEndTime()).toString());
        String baseLinesString = report.getBaseLinesString();
        V2AnalyticsOldUtil.fetchBaseLines(report, baseLinesString,report_context);
        report_context.setChartState(report.getChartState());
        report_context.setDateOperator(report.getTimeFilter().getDateOperator() > 0 ? report.getTimeFilter().getDateOperator() : DateOperators.BETWEEN.getOperatorId());

        report_context.setxAlias(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS);
        report_context.setxAggr(report.getDimensions().getxAggr());
        report_context.setAnalyticsType(ReadingAnalysisContext.AnalyticsType.PORTFOLIO);
        if(report.getGroupBy() != null && report.getGroupBy().getTime_aggr() > 0)
        {
            report_context.setgroupByTimeAggr(report.getGroupBy().getTime_aggrEnum());
            report_context.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, report.getGroupBy().getTime_aggr());
        }
        report_context.addToReportState(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS, report.isShowAlarms());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        report_context.setModuleId(modBean.getModule("energydata").getModuleId());

        return report_context;
    }

    public static void fetchBaseLines (V2ReportContext report, String baseLineString, ReportContext reportContext) throws Exception
    {
        if(baseLineString != null)
        {
            JSONParser parser = new JSONParser();
            JSONArray reportBaseLine_arr = (JSONArray) parser.parse(baseLineString);
            List<ReportBaseLineContext> reportBaseLines = FieldUtil.getAsBeanListFromJsonArray(reportBaseLine_arr, ReportBaseLineContext.class);
            if (reportBaseLines != null && !reportBaseLines.isEmpty()) {
                List<Long> baseLineIds = new ArrayList<>();
                for (ReportBaseLineContext baseLine : reportBaseLines) {
                    if (baseLine.getBaseLineId() != -1) {
                        baseLineIds.add(baseLine.getBaseLineId());
                    } else {
                        throw new IllegalArgumentException("Give valid baseline id");
                    }
                }
                Map<Long, BaseLineContext> baseLines = BaseLineAPI.getBaseLinesAsMap(baseLineIds);

                if (baseLines == null || baseLines.isEmpty()) {
                    throw new IllegalArgumentException("Give valid baseline id");
                }

                for (ReportBaseLineContext baseLine : reportBaseLines) {
                    baseLine.setBaseLine(baseLines.get(baseLine.getBaseLineId()));

                    if (baseLine.getAdjustTypeEnum() == null) {
                        baseLine.setAdjustType(BaseLineContext.AdjustType.WEEK);
                    }

                    if (baseLine.getBaseLine() == null) {
                        throw new IllegalArgumentException("Give valid baseline id. " + baseLine.getBaseLineId() + " is invalid");
                    }
                }
                reportContext.setBaseLines(reportBaseLines);
            } else {
                reportContext.setBaseLines(null);
            }
        }
    }

    public static void setXAndDateFields(ReportDataPointContext dataPoint, ReadingAnalysisContext.ReportMode mode, Map<String, FacilioField> fieldMap) throws Exception {
        FacilioField xField = null;
        ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        switch (mode) {
            case SERIES:
                xField = fieldMap.get("parentId");
                break;
            case SITE:
            case BUILDING:
            case FLOOR:
            case SPACE:
            case RESOURCE:
            case METERS:
                if(fieldMap.get("parentId")!= null) {
                    xField = fieldMap.get("parentId");
                }else {
                    FacilioModule module = bean.getModule(mode.getStringVal());
                    xField = FieldFactory.getIdField(module);
                }
                if(mode == ReadingAnalysisContext.ReportMode.METERS){
                    dataPoint.setFetchMeters(true);
                }else {
                    if(dataPoint.getModuleName() != null && dataPoint.getModuleName().equals(FacilioConstants.Meter.METER)){
                        dataPoint.setFetchMetersWithResource(true);
                    }else {
                        dataPoint.setFetchResource(true);
                    }
                }
                break;
            case TIMESERIES:
            case CONSOLIDATED:
            case TIME_CONSOLIDATED:
            case TIME_SPLIT:
            case TIME_DURATION:
                xField = fieldMap.get("ttime");
                break;
        }
        ReportFieldContext xAxis = new ReportFieldContext();
        xAxis.setField(xField.getModule(), xField);
        dataPoint.setxAxis(xAxis);
        if(fieldMap.get("ttime")!= null) {
            ReportFieldContext dateField = new ReportFieldContext();
            dateField.setField(fieldMap.get("ttime").getModule(), fieldMap.get("ttime"));
            dataPoint.setDateField(dateField);
        }
    }
    public static Criteria setFieldInCriteria(Criteria criteria, FacilioModule module)throws Exception
    {
        if(criteria != null)
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (String key :  criteria.getConditions().keySet())
            {
                Condition condition = criteria.getConditions().get(key);
                if (module == null || condition == null || condition.getFieldName() == null) continue;
                FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
                if(field == null) continue;
                condition.setField(field);
            }
            return criteria;
        }
        return null;
    }
    public static void addNewReportV2(V2ReportContext reportContext) throws Exception
    {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .fields(FieldFactory.getV2ReportModuleFields());
        Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
        insertBuilder.insert(props);

        List<V2AnalyticsMeasureContext> context_list =V2AnalyticsOldUtil.getV2AnalyticsMeasureContexts(reportContext.getMeasures(), reportContext.getReportId());
        if(context_list.size() > 0) {
            List<Map<String, Object>> measures_list = FieldUtil.getAsMapList(context_list, V2AnalyticsMeasureContext.class);
            for(Map<String,Object> measure_prop : measures_list) {
                V2AnalyticsOldUtil.addReportMeasures(measure_prop);
            }
        }
    }
    public static List<V2AnalyticsMeasureContext> getV2AnalyticsMeasureContexts(List<V2MeasuresContext> measures, Long reportId) throws Exception
    {
        List<V2AnalyticsMeasureContext> context_list = new ArrayList<>();
        V2AnalyticsMeasureContext measure_context = null;
        for(V2MeasuresContext measure : measures)
        {
            JSONObject measure_json = FieldUtil.getAsJSON(measure);
            measure_json.keySet().removeAll(MEASURE_KEYS);
            measure_context = new V2AnalyticsMeasureContext();
            measure_context.setReportId(reportId);
            measure_context.setMeasure_field_id(measure.getFieldId());
            measure_context.setParent_lookup_field_id(measure.getParent_lookup_fieldId());
            measure_context.setCriteria_id(measure.getCriteriaId());
            measure_context.setMeasure_props(measure_json != null ? measure_json.toJSONString() : null);
            context_list.add(measure_context);
        }
        return context_list;
    }
    public static List<V2AnalyticsMeasureContext> getV2ModuleMeasureContexts(List<V2ModuleMeasureContext> measures, Long reportId) throws Exception
    {
        List<V2AnalyticsMeasureContext> context_list = new ArrayList<>();
        V2AnalyticsMeasureContext measure_context = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for(V2ModuleMeasureContext measure : measures)
        {
            FacilioField measureField = modBean.getField(measure.getFieldName(),measure.getModuleName());
            if(measureField.getId() < 0){
                measureField = FieldFactory.getIdField(modBean.getModule(measure.getModuleName()));
            }
            JSONObject measure_json = FieldUtil.getAsJSON(measure);
            measure_json.keySet().removeAll(MEASURE_KEYS);
            measure_context = new V2AnalyticsMeasureContext();
            measure_context.setReportId(reportId);
            measure_context.setMeasure_field_id(measureField.getFieldId());
            measure_context.setCriteria_id(measure.getCriteriaId());
            measure_context.setMeasure_props(measure_json != null ? measure_json.toJSONString() : null);
            context_list.add(measure_context);
        }
        return context_list;
    }
    public static void addReportMeasures(Map<String, Object> props)throws Exception
    {
        GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getReportV2MeasureModule().getTableName())
                .fields(FieldFactory.getV2ReportMeasureFields());
        insert_builder.insert(props);
    }
    public static void updateReportMeasures(V2ReportContext report)throws Exception
    {
        new GenericDeleteRecordBuilder().table(ModuleFactory.getReportV2MeasureModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", report.getReportId() + "", NumberOperators.EQUALS))
                .delete();

        List<V2AnalyticsMeasureContext> context_list = V2AnalyticsOldUtil.getV2AnalyticsMeasureContexts(report.getMeasures(), report.getReportId());
        if(context_list.size() > 0)
        {
            List<Map<String, Object>> measures_list = FieldUtil.getAsMapList(context_list, V2AnalyticsMeasureContext.class);
            for(Map<String,Object> measure_prop : measures_list) {
                V2AnalyticsOldUtil.addReportMeasures(measure_prop);
            }
        }
    }
    public static void updateNewReportV2(V2ReportContext reportContext) throws Exception
    {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .fields(FieldFactory.getV2ReportModuleFields())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportContext.getReportId() + "", NumberOperators.EQUALS));
        Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
        updateBuilder.update(props);

        if (reportContext.getReportId() > 0){
            V2AnalyticsOldUtil.updateReportMeasures(reportContext);
        }

    }
    public static void deleteNewReportV2(V2ReportContext reportContext) throws Exception
    {
        for(V2MeasuresContext measure : reportContext.getMeasures()){
            if(measure.getCriteriaId() > 0){
                CriteriaAPI.deleteCriteria(measure.getCriteriaId());
            }
        }
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportContext.getId()+"", NumberOperators.EQUALS));
        deleteBuilder.delete();
    }

    public static long generateCriteriaId(Criteria criteria, String moduleName)throws Exception
    {
        if(criteria != null)
        {
            if (moduleName != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (String key : criteria.getConditions().keySet()) {
                    Condition condition = criteria.getConditions().get(key);
                    FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
                    condition.setField(field);
                }
            }
            return CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getId());
        }
        return -1l;
    }

    public static V2ReportContext getV2Report(Long reportId)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getV2ReportModuleFields())
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> props = select.get();
        if (props != null && !props.isEmpty())
        {
            V2ReportContext v2_report = FieldUtil.getAsBeanFromMap(props.get(0), V2ReportContext.class);
            GenericSelectRecordBuilder select_measure = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getV2ReportMeasureFields())
                    .table(ModuleFactory.getReportV2MeasureModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportId.toString(), NumberOperators.EQUALS));

            List<Map<String, Object>> measure_props = select_measure.get();
            if (measure_props != null && !measure_props.isEmpty())
            {
                List<V2AnalyticsMeasureContext> measures = FieldUtil.getAsBeanListFromMapList(measure_props, V2AnalyticsMeasureContext.class);
                List<V2MeasuresContext> v2_measure_list = new ArrayList<>();
                JSONParser parser = new JSONParser();
                for(V2AnalyticsMeasureContext measure : measures)
                {
                    String measure_json_str = measure.getMeasure_props();
                    JSONObject measure_json = (JSONObject) parser.parse(measure_json_str);
                    if(measure_json != null)
                    {
                        measure_json.put(MEASURE_KEYS.get(0), measure.getRelationship_id());
                        measure_json.put(MEASURE_KEYS.get(1), measure.getMeasure_field_id());
                        measure_json.put(MEASURE_KEYS.get(2), measure.getParent_lookup_field_id());
                        measure_json.put(MEASURE_KEYS.get(3), measure.getCriteria_id());
                        V2MeasuresContext measure_context = FieldUtil.getAsBeanFromJson(measure_json, V2MeasuresContext.class);
                        if(measure.getCriteria_id() != null && measure.getCriteria_id() > 0){
                            measure_context.setCriteria(CriteriaAPI.getCriteria(measure.getCriteria_id()));
                        }
                        if(measure_context.getFieldId() != null && measure_context.getFieldId() > 0)
                        {
                            FacilioField measure_field = Constants.getModBean().getField(measure_context.getFieldId());
                            if(measure_field != null && measure_field instanceof  NumberField)
                            {
                                Unit unit = UnitsUtil.getDisplayUnit((NumberField) measure_field);
                                if(unit != null && unit.getSymbol() != null && !"".equals(unit.getSymbol())) {
                                    measure_context.setUnit(unit.getSymbol());
                                }
                            }
                        }
                        v2_measure_list.add(measure_context);
                    }
                }
                if(v2_measure_list.size() > 0) {
                    v2_report.setMeasures(v2_measure_list);
                }
                V2ReportFiltersContext globalCriteria = new V2ReportFiltersContext();
                if(v2_report != null && v2_report.getCriteriaId() != null &&  v2_report.getCriteriaId() > 0 )
                {
                    globalCriteria.setCriteria(CriteriaAPI.getCriteria(v2_report.getCriteriaId()));
                    globalCriteria.setCriteriaId(v2_report.getCriteriaId());
                    globalCriteria.setModuleName(DIMENSION_TYPE_VS_MODULES.get(v2_report.getDimensions().getDimensionTypeEnum()));
                    v2_report.setG_criteria(globalCriteria);
                    v2_report.setCriteriaId(null);
                }
                globalCriteria.setAnalytics_user_filters(V2AnalyticsOldUtil.getNewReportV2UserFilter(v2_report.getReportId()));
                return v2_report;
            }
        }

        return null;
    }
    public static void updateModuleReportMeasures(V2ModuleReportContext report)throws Exception
    {
        new GenericDeleteRecordBuilder().table(ModuleFactory.getReportV2MeasureModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", report.getReportId() + "", NumberOperators.EQUALS))
                .delete();

        List<V2AnalyticsMeasureContext> context_list = V2AnalyticsOldUtil.getV2ModuleMeasureContexts(report.getMeasures(), report.getReportId());
        if(context_list.size() > 0)
        {
            List<Map<String, Object>> measures_list = FieldUtil.getAsMapList(context_list, V2AnalyticsMeasureContext.class);
            for(Map<String,Object> measure_prop : measures_list) {
                V2AnalyticsOldUtil.addReportMeasures(measure_prop);
            }
        }
    }
    public static V2ModuleReportContext getV2ModuleReport(Long reportId)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getV2ReportModuleFields())
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> props = select.get();
        if (props != null && !props.isEmpty())
        {
            V2ModuleReportContext v2_report = FieldUtil.getAsBeanFromMap(props.get(0), V2ModuleReportContext.class);
            GenericSelectRecordBuilder select_measure = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getV2ReportMeasureFields())
                    .table(ModuleFactory.getReportV2MeasureModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportId.toString(), NumberOperators.EQUALS));

            List<Map<String, Object>> measure_props = select_measure.get();
            if (measure_props != null && !measure_props.isEmpty())
            {
                List<V2AnalyticsMeasureContext> measures = FieldUtil.getAsBeanListFromMapList(measure_props, V2AnalyticsMeasureContext.class);
                List<V2ModuleMeasureContext> v2_measure_list = new ArrayList<>();
                JSONParser parser = new JSONParser();
                for(V2AnalyticsMeasureContext measure : measures)
                {
                    String measure_json_str = measure.getMeasure_props();
                    JSONObject measure_json = (JSONObject) parser.parse(measure_json_str);
                    if(measure_json != null)
                    {
                        Long fieldId = measure.getMeasure_field_id();
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        if(fieldId != null && fieldId > 0){
                            FacilioField field = modBean.getField(fieldId);
                            measure_json.put("fieldName",field.getName());
                        }
                        V2ModuleMeasureContext measure_context = FieldUtil.getAsBeanFromJson(measure_json, V2ModuleMeasureContext.class);
                        if(measure.getCriteria_id() != null && measure.getCriteria_id() > 0){
                            Criteria criteria = CriteriaAPI.getCriteria(measure.getCriteria_id());
                            JSONObject criteriaObj = FieldUtil.getAsJSON(criteria);
                            measure_context.setCriteria(criteriaObj.toJSONString());
                        }
                        v2_measure_list.add(measure_context);
                    }
                }
                if(v2_measure_list.size() > 0) {
                    v2_report.setMeasures(v2_measure_list);
                }
                return v2_report;
            }
        }
        return null;
    }

    public static JSONArray getDimensionModes()throws Exception
    {
        JSONArray modes = new JSONArray();
        JSONArray time_aggr_arr = new JSONArray();
        JSONObject time_aggr_json = new JSONObject();
        JSONObject mode_obj = null;
        for(ReadingAnalysisContext.ReportMode mode : ReadingAnalysisContext.ReportMode.values())
        {
            if(SUPPORTED_PORTFOLIO_MODES.contains(mode)){
                mode_obj = new JSONObject();
                if(mode == ReadingAnalysisContext.ReportMode.TIME_CONSOLIDATED)
                {
                    time_aggr_json.put("name", BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getStringValue());
                    time_aggr_json.put("index", BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getValue());
                    time_aggr_arr.add(time_aggr_json);
                    for(BmsAggregateOperators.DateAggregateOperator date_aggr : BmsAggregateOperators.DateAggregateOperator.values())
                    {
                        time_aggr_json = new JSONObject();
                        time_aggr_json.put("name", date_aggr.getStringValue());
                        time_aggr_json.put("index", date_aggr.getValue());
                        time_aggr_arr.add(time_aggr_json);
                    }
                    mode_obj.put("time_aggr_list", time_aggr_arr);
                }
                mode_obj.put("mode", mode);
                mode_obj.put("mode_index", mode.getValue());
                mode_obj.put("mode_name", mode.getStringVal());
                modes.add(mode_obj);
            }
        }
        return modes;
    }

    public static GenericSelectRecordBuilder getPaginatedSelectBuilder(GenericSelectRecordBuilder select, JSONObject pagination, FacilioModule module, String orderBy)throws Exception
    {
        int perPage;
        int page;
        if (pagination != null) {
            page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
            perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
        } else {
            page = 1;
            perPage = 50;
        }
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        select.offset(offset);
        select.limit(perPage);
        String order_by = new StringBuilder().append(FieldFactory.getIdField(module).getCompleteColumnName()).append(" ").append(orderBy == null ? "desc": orderBy).toString();
        select.orderBy(order_by);
        return select;
    }


    public static boolean getAnalyticsReportSharing(long reportId)throws Exception
    {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReportShareModule().getTableName())
                .select(FieldFactory.getReportShareField())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", reportId+"", NumberOperators.EQUALS));
        List<Map<String, Object>> props = select.get();
        long currentUserId = AccountUtil.getCurrentUser().getId();
        long currentUserRoleId = AccountUtil.getCurrentUser().getRoleId();
        List<Long> currentUserGroupId = ReportUtil.getLoggedInUserGroupIds();
        if(props != null && !props.isEmpty()) {
            for (Map<String, Object> prop : props) {
                if (prop.containsKey("userId")) {
                    if (currentUserId == (Long) prop.get("userId")) {
                       return true;
                    }
                }
                else if (prop.containsKey("roleId")) {
                    if (currentUserRoleId == (Long) prop.get("roleId")) {
                      return true;
                    }
                }
                else if (prop.containsKey("groupId")) {
                    if(currentUserGroupId.contains((Long) prop.get("groupId"))){
                      return true;
                    }
                }
            }
        }
        else{
           return true;
        }
        return false;
    }

    public static List<FacilioField> getFieldsForReportList()throws Exception
    {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReport1Fields());
        Map<String, FacilioField> newReportFieldMap = FieldFactory.getAsMap(FieldFactory.getV2ReportModuleFields());
        List<FacilioField> selected_fields = new ArrayList<>();
        selected_fields.add(fieldMap.get("id"));
        selected_fields.add(fieldMap.get("reportFolderId"));
        selected_fields.add(fieldMap.get("name"));
        selected_fields.add(fieldMap.get("description"));
        selected_fields.add(fieldMap.get("type"));
        selected_fields.add(fieldMap.get("analyticsType"));
        selected_fields.add(fieldMap.get("moduleId"));
        selected_fields.add(fieldMap.get("createdTime"));
        selected_fields.add(fieldMap.get("modifiedTime"));
        selected_fields.add(fieldMap.get("createdBy"));
        selected_fields.add(fieldMap.get("modifiedBy"));
        selected_fields.add(fieldMap.get("appId"));
        selected_fields.add(fieldMap.get("type"));
        selected_fields.add(newReportFieldMap.get("kpi"));
        selected_fields.add(newReportFieldMap.get("timeFilterJson"));
        return selected_fields;
    }
    public static void calculateBaseLineRange(ReportContext report)
    {
        if (report.getDateOperatorEnum() != null)
        {
            DateRange actualRange = report.getDateRange() == null ? report.getDateOperatorEnum().getRange(report.getDateValue()) : report.getDateRange();
            report.setDateRange(actualRange);
            if (report.getBaseLines() != null && !report.getBaseLines().isEmpty())
            {
                for (ReportBaseLineContext baseLine : report.getBaseLines())
                {
                    DateRange baseLineRange = baseLine.getBaseLine().calculateBaseLineRange(actualRange, baseLine.getAdjustTypeEnum());
                    baseLine.setBaseLineRange(baseLine.getBaseLine().calculateBaseLineRange(actualRange, baseLine.getAdjustTypeEnum()));
                    baseLine.setDiff(actualRange.getStartTime() - baseLineRange.getStartTime());
                }
            }
        }
    }
    public static ReportDataPointContext getSortPoint(List<ReportDataPointContext> dataPoints)
    {
        Iterator<ReportDataPointContext> itr = dataPoints.iterator();
        while (itr.hasNext())
        {
            ReportDataPointContext dp = itr.next();
            if (dp.isDefaultSortPoint())
            {
                if (dp.getLimit() == -1 || dp.getOrderByFuncEnum() == ReportDataPointContext.OrderByFunction.NONE) {
                    throw new IllegalArgumentException("Default sort datapoint should have order by and limit");
                }
                return dp;
            }
        }
        return null;
    }
    public static ReportDataPointContext isSortDPPointIncluded(List<ReportDataPointContext> dataPoints, ReportDataPointContext sorteddp)
    {
        Iterator<ReportDataPointContext> itr = dataPoints.iterator();
        while (itr.hasNext())
        {
            ReportDataPointContext dp = itr.next();
            if (dp.isDefaultSortPoint())
            {
                if (dp.getLimit() == -1 || dp.getOrderByFuncEnum() == ReportDataPointContext.OrderByFunction.NONE) {
                    throw new IllegalArgumentException("Default sort datapoint should have order by and limit");
                }
                return dp;
            }
        }
        return null;
    }
    public static String getAndSetModuleAlias(String moduleName, HashMap<String, String> moduleVsAlias) {
        String alias = "";
        if (moduleVsAlias.containsKey(moduleName)) {
            return moduleVsAlias.get(moduleName);
        }
        alias = moduleVsAlias.values().size() > 0 ? ReportUtil.getAlias((String) moduleVsAlias.values().toArray()[moduleVsAlias.values().size() - 1]):ReportUtil.getAlias("");
        moduleVsAlias.put(moduleName, alias);
        return alias;
    }
    public static void applyJoin(HashMap<String, String> moduleVsAlias, FacilioModule baseModule, String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder)throws Exception
    {
        applyJoin(moduleVsAlias, baseModule, on, joinModule, selectBuilder, null);
    }
    public static void applyJoin(HashMap<String, String> moduleVsAlias, FacilioModule baseModule, String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception
    {
        if (joinModule != null && (joinModule.isCustom() && !baseModule.equals(joinModule))) {
            selectBuilder.innerJoin(joinModule.getTableName())
                    .alias(V2AnalyticsOldUtil.getAndSetModuleAlias(joinModule.getName(), moduleVsAlias))
                    .on(on);
            selectBuilder.addJoinModules(Collections.singletonList(joinModule));
        } else {
            if (addedModules == null || (addedModules != null && !V2AnalyticsOldUtil.isExistInAddedModules(addedModules, joinModule))) {
                selectBuilder.innerJoin(joinModule.getTableName()).on(on);
                if(addedModules != null) {
                    addedModules.add(joinModule);
                }
                selectBuilder.addJoinModules(Collections.singletonList(joinModule));
            }
        }
        FacilioModule prevModule = joinModule;
        FacilioModule extendedModule = prevModule.getExtendModule();
        while (extendedModule != null)
        {
            if (addedModules == null || (addedModules != null && !V2AnalyticsOldUtil.isExistInAddedModules(addedModules, extendedModule)))
            {
                if(addedModules != null){
                    addedModules.add(extendedModule);
                }
                selectBuilder.addJoinModules(Collections.singletonList(extendedModule));
                selectBuilder.innerJoin(extendedModule.getTableName())
                    .on(prevModule.getTableName() + ".ID = " + extendedModule.getTableName() + ".ID");
            }
            prevModule = extendedModule;
            extendedModule = extendedModule.getExtendModule();
        }
    }

    public static boolean isExistInAddedModules(Set<FacilioModule> addedModules, FacilioModule module)throws Exception
    {
        if(addedModules != null){
            for(FacilioModule existing_module: addedModules)
            {
                if(existing_module != null && existing_module.getName().equals(module.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void joinModuleIfRequired(HashMap<String, String> moduleVsAlias, FacilioModule baseModule, ReportFieldContext axis, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = StringUtils.isNotEmpty(axis.getModuleName()) ? modBean.getModule(axis.getModuleName()) : axis.getModule();
        if (!baseModule.isParentOrChildModule(module) && !isAlreadyAdded(addedModules, module))
        {
            applyJoin(moduleVsAlias, baseModule, axis.getJoinOn(), module, selectBuilder);
            addedModules.add(module);
        }
    }
    public static boolean isAlreadyAdded(Set<FacilioModule> addedModules, FacilioModule module)
    {
        for (FacilioModule m : addedModules) {
            if (m.isParentOrChildModule(module)) {
                return true;
            }
        }
        return false;
    }
    public static void applyOrderByAndLimit(ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, AggregateOperator xAggr, ReportContext.ReportType reportType)
    {
        if (dataPoint.getOrderBy() != null && !dataPoint.getOrderBy().isEmpty())
        {
            if (dataPoint.getOrderByFuncEnum() == null || dataPoint.getOrderByFuncEnum() == ReportDataPointContext.OrderByFunction.NONE) {
                throw new IllegalArgumentException("Order By function cannot be empty when order by is not empty");
            }

            StringJoiner orderBy = new StringJoiner(",");
            for (String order : dataPoint.getOrderBy()) { //MySQL requires direction be specified for each column
                orderBy.add(order + " " + dataPoint.getOrderByFuncEnum().getStringValue());
            }
            selectBuilder.orderBy(orderBy.toString());

            if (dataPoint.getLimit() != -1) {
                selectBuilder.limit(dataPoint.getLimit());
            }
        }
        else if (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE && xAggr != BmsAggregateOperators.CommonAggregateOperator.ACTUAL)
        {
            selectBuilder.orderBy(dataPoint.getyAxis().getAggr() < 1 ? dataPoint.getxAxis().getField().getCompleteColumnName() : new StringBuilder().append("MIN(").append(dataPoint.getxAxis().getField().getCompleteColumnName()).append(")").toString());
        }
    }

    public static FacilioField applySpaceAggregation(ReportDataPointContext dp, AggregateOperator aggr, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules, FacilioField field) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE));

        FacilioField spaceField = null;
        switch ((BmsAggregateOperators.SpaceAggregateOperator) aggr) {
            case SITE:
                spaceField = fieldMap.get("site").clone();
                break;
            case BUILDING:
                spaceField = fieldMap.get("building").clone();
                break;
            case FLOOR:
                spaceField = fieldMap.get("floor").clone();
                break;
            case SPACE:
                selectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName() + ".SPACE_TYPE", "SPACE_TYPE", String.valueOf(BaseSpaceContext.SpaceType.valueOf(aggr.toString()).getIntVal()), NumberOperators.EQUALS));
                spaceField = FieldFactory.getIdField(baseSpaceModule);
                break;
            default:
                throw new RuntimeException("Cannot be here!!");
        }
        spaceField.setName(field.getName());
        spaceField.setDataType(FieldType.NUMBER);
        if(dp.getModuleName() != null && dp.getModuleName().equals(FacilioConstants.Meter.METER) && dp.isFetchMetersWithResource()) {
            return spaceField;
        }
        if (!isAlreadyAdded(addedModules, resourceModule)) {
            selectBuilder.addJoinModules(Collections.singletonList(resourceModule));
            StringBuilder join_criteria = new StringBuilder(resourceModule.getTableName()).append(".ID = ").append(field.getCompleteColumnName());
            if(field.getModule() != null && field.getModule().getTableName() != null){
                join_criteria.append(" AND ").append(resourceModule.getTableName()).append(".ORGID = ").append(field.getModule().getTableName()).append(".ORGID");
            }

            selectBuilder.innerJoin(resourceModule.getTableName())
                    .on(join_criteria.toString());
            addedModules.add(resourceModule);
        }

        selectBuilder.addJoinModules(Collections.singletonList(baseSpaceModule));

        StringBuilder space_join_criteria = new StringBuilder(resourceModule.getTableName()).append(".SPACE_ID = ").append(baseSpaceModule.getTableName()).append(".ID");
        space_join_criteria.append(" AND ").append(resourceModule.getTableName()).append(".ORGID = ").append(baseSpaceModule.getTableName()).append(".ORGID");
        selectBuilder.innerJoin(baseSpaceModule.getTableName())
                .on(space_join_criteria.toString());
        addedModules.add(baseSpaceModule);


        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .table(resourceModule.getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(resourceModule)))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", "false", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), resourceModule))
                .andCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", String.valueOf("1"), NumberOperators.EQUALS));
        if (dp.getxAxis().getModuleName().equals(FacilioConstants.ContextNames.ALARM_OCCURRENCE)) {
            Criteria spaceCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ContextNames.BUILDING);
            builder.andCriteria(spaceCriteria);
        }
        selectBuilder.andCustomWhere(spaceField.getCompleteColumnName() + " in (" + builder.constructQueryString() + ")");

        return spaceField;
    }
    public static void setGroupByTimeAggregator(ReportDataPointContext dp, AggregateOperator groupByTimeAggr, StringJoiner groupBy)throws Exception
    {
        FacilioField groupByTimeField = null;
        if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0)
        {
            if (groupByTimeAggr != null && groupByTimeAggr.getValue() != 0) {
                groupByTimeField = groupByTimeAggr.getSelectField(dp.getxAxis().getField());
                groupBy.add(groupByTimeField.getCompleteColumnName());
            }
        }
    }

    public static void applyTimeFilterCriteria(ReportContext report , ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext baseLine)throws Exception
    {
        if (report.getDateOperatorEnum() != null && dp.getTypeEnum() != ReportDataPointContext.DataPointType.FIELD)
        {
            if (dp.getDateField() == null) {
                throw new IllegalArgumentException("Date Field for datapoint cannot be null when report has date filter");
            }
            if (report.getDateOperatorEnum().isValueNeeded() && (report.getDateValue() == null || report.getDateValue().isEmpty())) {
                throw new IllegalArgumentException("Date Filter value cannot be null for the Date Operator :  " + report.getDateOperatorEnum());
            }
            selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField().getField(), calculateRightInclusiveTimeRange(dp, report, baseLine != null ? baseLine : null).toString(), DateOperators.BETWEEN));
        }
    }
    public static DateRange calculateRightInclusiveTimeRange(ReportDataPointContext dp , ReportContext report, ReportBaseLineContext baseline) throws Exception
    {
        if((dp != null && dp.isRightInclusive()) && dp.getyAxis().getAggrEnum() != null && report.getxAggrEnum() != null && BmsAggregateOperators.getRightInclusiveAggr(report.getxAggrEnum().getValue()) != null)
        {
            long rightInclusiveMills = 001l;
            DateRange range = baseline != null ? baseline.getBaseLineRange() : report.getDateRange();
            long start_time = range.getStartTime() + rightInclusiveMills;
            long end_time = range.getEndTime() + rightInclusiveMills;
            return new DateRange(start_time, end_time);
        }
        return baseline != null ? baseline.getBaseLineRange() : report.getDateRange();
    }
    public static void applyDashboardUserFilterCriteria(FacilioModule baseModule, JSONObject dbUserFilter, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules)throws Exception
    {
        if(dbUserFilter != null)
        {
            List<Map<String, JSONObject>> filterMappings = (List<Map<String, JSONObject>>) dbUserFilter.get(dataPoint.getAliases().get("actual"));
            if(filterMappings != null && filterMappings.size() > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(baseModule.getName()));
                FacilioField appliedField = new FacilioField();
                Criteria criteria = new Criteria();
                for(Map<String, JSONObject> filterMap : filterMappings) {
                    for (String alias : filterMap.keySet()) {
                        Condition condition = new Condition();
                        HashMap selected_dp_map = (HashMap) filterMap.get(String.valueOf(alias));
                        condition.setOperatorId(36);
                        List<String> value = (List<String>) selected_dp_map.get("value");
                        StringJoiner joiner = new StringJoiner(",");
                        value.forEach(val -> joiner.add(val));
                        condition.setValue(String.valueOf(joiner));
                        if(dataPoint.getModuleName().equals(FacilioConstants.Meter.METER)){
                            Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getModuleName()));
                            if(alias.equals("siteId")){
                                FacilioModule meterModule = modBean.getModule(FacilioConstants.Meter.METER);
                                appliedField = modBean.getField("siteId", meterModule.getName());
                            } else if(alias.equals("meterLocation")){
                                appliedField = fieldMap.get("meterLocation");
                            } else if(alias.equals("utilitytype")){
                                appliedField = fieldMap.get("utilitytype");
                            } else {
                                appliedField = fieldsMap.get("parentId");
                            }
                        }
                        else {
                            Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getParentReadingModule().getName()));
                            if (alias.equals("space")) {
                                appliedField = fieldMap.get("space");
                            } else if(alias.equals("category")){
                                appliedField = fieldMap.get("category");
                            } else {
                                appliedField = fieldsMap.get("parentId");
                            }
                        }
                        condition.setField(appliedField);
                        criteria.addAndCondition(condition);
                        applyFilterCriteria(baseModule,dataPoint,selectBuilder,addedModules,criteria);
                    }
                }
            }
        }
    }
    public static void applyFilterCriteria(FacilioModule baseModule, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules, Criteria criteria)throws Exception
    {
        LinkedHashMap<String, String> moduleVsAlias = new LinkedHashMap<String, String>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(dataPoint.getParentReadingModule() != null)
        {
            FacilioField parent_field = FieldFactory.getIdField(dataPoint.getParentReadingModule());
            if(dataPoint.getyAxis().getField() != null)
            {
                Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName()));
                FacilioField child_field = fieldsMap.get("parentId");
                applyJoin(moduleVsAlias,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), dataPoint.getParentReadingModule(), selectBuilder, addedModules);
                Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(criteria, dataPoint.getParentReadingModule());
                if(parent_criteria != null)
                {
                    selectBuilder.andCriteria(parent_criteria);
                }
            }
        }
    }
    public static void applyMeasureCriteriaV2(HashMap<String, String> moduleVsAlias, FacilioField xAggrField, FacilioModule baseModule, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, String xValues, Set<FacilioModule> addedModules)throws Exception
    {
        if (xValues != null)
        {
            selectBuilder.andCondition(CriteriaAPI.getEqualsCondition(xAggrField, xValues));
        }
        if((dataPoint.getCriteriaType() == V2MeasuresContext.Criteria_Type.CRITERIA.getIndex() || dataPoint.getCriteriaType() == V2MeasuresContext.Criteria_Type.SPECIFIC.getIndex()) && dataPoint.getV2Criteria() != null && !dataPoint.getV2Criteria().isEmpty())
        {
            if(dataPoint.getParentReadingModule() != null)
            {
                FacilioField parent_field = FieldFactory.getIdField(dataPoint.getParentReadingModule());
                if(dataPoint.getyAxis().getField() != null)
                {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName()));
                    FacilioField child_field = fieldsMap.get("parentId");
                    applyJoin(moduleVsAlias,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), dataPoint.getParentReadingModule(), selectBuilder, addedModules);
                    Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(dataPoint.getV2Criteria(), dataPoint.getParentReadingModule());
                    if(parent_criteria != null)
                    {
                        selectBuilder.andCriteria(parent_criteria);
                    }
                }
            }
        }
    }
    public static Boolean isSameTable(ReportDataPointContext rdp, ReportDataPointContext dp) {
        if (dp.getyAxis() != null && rdp.getyAxis() != null) {
            if (dp.getyAxis().getField() != null && rdp.getyAxis().getField() != null) {
                if (dp.getyAxis().getField().getTableAlias() != null && rdp.getyAxis().getField().getTableAlias() != null) {
                    if (dp.getyAxis().getField().getTableAlias().equals(rdp.getyAxis().getField().getTableAlias())) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Long> getAssetIdsFromCriteria(String moduleName, Criteria criteria) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder()
                .select(Collections.singleton(FieldFactory.getIdField(module)))
                .module(module);

        if(criteria != null) {
            Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(criteria, module);
            if(parent_criteria != null)
            {
                selectBuilder.andCriteria(parent_criteria);
            }
            selectBuilder.andCriteria(criteria);
        }
        List<Map<String, Object>> props = selectBuilder.getAsProps();
        List<Long> assetIds = new ArrayList<>();
        if(props != null && props.size() > 0)
        {
            for(Map<String, Object> prop :props) {
                assetIds.add((Long)prop.get(FieldFactory.getIdField(module).getName()));
            }
        }
        return assetIds;
    }

    public static HashMap<String, ReportDataPointContext> getAliasVsDataPointMap(List<ReportDataPointContext> dataPoints)throws Exception
    {
        if(dataPoints != null && dataPoints.size() > 0) {
            return (HashMap<String, ReportDataPointContext>) dataPoints
                    .stream()
                    .collect(Collectors.toMap(
                            measure -> measure.getAliases().get("actual"),
                            measure -> measure
                    ));
        }
        return null;
    }
    public static String getParentIdsFromRelationship(Long relationship_id,String parent_module_name, Criteria parent_criteria)throws Exception
    {
        RelationContext relation = RelationUtil.getRelation(relationship_id, true);
        if(relation != null)
        {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule fromModule = moduleBean.getModule(parent_module_name);
            RelationMappingContext mappingData = null;
            if(relation.getMapping1() != null && relation.getMapping1().getFromModuleId() == fromModule.getModuleId()){
                mappingData = relation.getMapping1();
            }
            else if(relation.getMapping2() != null && relation.getMapping2().getFromModuleId() == fromModule.getModuleId()){
                mappingData = relation.getMapping2();
            }

           return V2AnalyticsOldUtil.getRelationshipParentIds(parent_criteria, relation.getRelationModuleId(), fromModule, mappingData);
        }
        return null;
    }

    public static void getAndSetRelationShipSubQuery(List<ReportDataPointContext> dataPoints, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, HashMap<String, String> moduleVsAlias)throws Exception
    {
        if(dataPoint.getParent_measure_alias() != null && dataPoint.getRelationship_id() > 0)
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<String, ReportDataPointContext> measures_map = V2AnalyticsOldUtil.getAliasVsDataPointMap(dataPoints);
            if (measures_map.containsKey(dataPoint.getParent_measure_alias()))
            {
                ReportDataPointContext parent_datapoint = measures_map.get(dataPoint.getParent_measure_alias());
                if (dataPoint.getyAxis().getField() != null && dataPoint.getyAxis().getField().getModule() != null)
                {
                    FacilioModule yaxis_module_name = dataPoint.getyAxis().getField().getModule();
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(yaxis_module_name.getName()));
                    if (fieldsMap.containsKey("parentId"))
                    {
                        FacilioField parentField = fieldsMap.get("parentId").clone();
                        if (moduleVsAlias.containsKey(parentField.getModule().getName())) {
                            parentField.setTableAlias(moduleVsAlias.get(parentField.getModule().getName()));
                        }
                        String relationship_subquery = V2AnalyticsOldUtil.getParentIdsFromRelationship(dataPoint.getRelationship_id(), parent_datapoint.getParentReadingModule().getName(), parent_datapoint.getV2Criteria());
                        selectBuilder.andCustomWhere(new StringBuilder(parentField.getCompleteColumnName()).append(" IN (").append(relationship_subquery).append(" )").toString());
                    }
                }
            }
        }
    }

    private static String getRelationshipParentIds(Criteria criteria, Long  relation_module_id, FacilioModule fromModule, RelationMappingContext relation_mapping)throws Exception
    {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule customRelModule = moduleBean.getModule(relation_module_id);
        List<FacilioField> selectedFields = new ArrayList<>();
        FacilioField selectField = null;
        if(relation_mapping.getPositionEnum() == RelationMappingContext.Position.LEFT){
            selectField = moduleBean.getField(RelationMappingContext.Position.RIGHT.getFieldName(), customRelModule.getName());
        }else{
            selectField = moduleBean.getField(RelationMappingContext.Position.LEFT.getFieldName(), customRelModule.getName());
        }
        selectedFields.add(selectField);

        SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder()
                .module(customRelModule)
                .select(selectedFields)
                .setAggregation()
                .innerJoin(fromModule.getTableName() )
                .on(new StringBuilder(customRelModule.getTableName()).append(".").append(relation_mapping.getPositionEnum().getColumnName()).append(" = ").append(fromModule.getTableName()).append(".ID").append(" AND ")
                        .append(customRelModule.getTableName()).append(".").append("MODULEID = ").append(relation_module_id).toString());

        if(criteria != null) {
            selectBuilder.andCriteria(criteria);
        }
        return selectBuilder.constructQueryString();
    }
    public static ScopeHandler.ScopeFieldsAndCriteria  getScopingCriteria(FacilioModule module)throws Exception
    {
        return ScopeHandler.getInstance().getFieldsAndCriteriaForSelect(module, null);
    }
    public static void checkAndApplyJoinForScopingCriteria(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules, FacilioModule baseModule)throws Exception
    {
        if(addedModules != null)
        {
            for(FacilioModule module : addedModules)
            {
                FacilioModule parentModule = V2AnalyticsOldUtil.getSubmoduleFromChild(module);
                if(parentModule != null && (parentModule.getName().equals(FacilioConstants.ContextNames.ASSET) || parentModule.getName().equals(FacilioConstants.Meter.METER)))
                {
                    ScopeHandler.ScopeFieldsAndCriteria scopingCriteria = V2AnalyticsOldUtil.getScopingCriteria(parentModule);
                    if (scopingCriteria != null && !scopingCriteria.getCriteria().isEmpty())
                    {
                        ModuleBean modBean = Constants.getModBean();
                        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(baseModule.getName()));
                        FacilioField child_field = fieldsMap.get("parentId");
                        FacilioField parent_field = FieldFactory.getIdField(parentModule);
                        String joinOn = new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString();
                        if(parentModule.getName().equals(FacilioConstants.Meter.METER)){
                            joinOn = new StringBuilder(joinOn).append(" AND (Meters.SYS_DELETED IS NULL OR Meters.SYS_DELETED = false )").toString();
                            V2AnalyticsOldUtil.applyJoin(null, baseModule, joinOn, parentModule, selectBuilder, addedModules);
                        }
                        else if(parentModule.getName().equals(FacilioConstants.ContextNames.ASSET))
                        {
                            V2AnalyticsOldUtil.applyJoin(null, baseModule, joinOn, parentModule, selectBuilder, addedModules);
                            selectBuilder.andCustomWhere(new StringBuilder().append(" (Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = false)").toString());
                        }
                    }
                }
            }
        }
    }

    public static FacilioModule  getSubmoduleFromChild(FacilioModule module)throws Exception
    {
        List<FacilioField> subModuleRelFields = new ArrayList<>();
        subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
        subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table("SubModulesRel")
                .select(subModuleRelFields)
                .andCondition(CriteriaAPI.getCondition("CHILD_MODULE_ID", "childModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props))
        {
            for(Map<String, Object> prop : props) {
                FacilioModule readingModule = Constants.getModBean().getModule((long) prop.get("parentModuleId"));
                if(readingModule != null && readingModule.getExtendModule() != null)
                {
                    return readingModule.getExtendModule();
                }
            }
        }
        return null;
    }
    public static void applyAnalyticGlobalFilterCriteria(FacilioModule baseModule,ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, V2ReportFiltersContext filterContext, Set<FacilioModule> addedModules)throws Exception
    {
        if(filterContext != null && filterContext.getCriteria() != null && !filterContext.getCriteria().isEmpty() && filterContext.getModuleName() != null)
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule criteriaModule = modBean.getModule(filterContext.getModuleName());
            Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName()));
            FacilioField child_field = fieldsMap.get("parentId");
            FacilioField parent_field = FieldFactory.getIdField(criteriaModule);
            applyJoin(null,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), criteriaModule, selectBuilder, addedModules);
            Criteria global_criteria = V2AnalyticsOldUtil.setFieldInCriteria(filterContext.getCriteria(), criteriaModule);
            if(global_criteria != null) {
                selectBuilder.andCriteria(global_criteria);
            }
        }
    }

    public static void applyResourcesJoinOnMeter(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, FacilioModule baseModule, V2DimensionContext dimension , ReportDataPointContext dataPoint, Set<FacilioModule> addedModules)throws Exception
    {
        if(dataPoint.isFetchMetersWithResource())
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule meterModule = modBean.getModule(FacilioConstants.Meter.METER);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName()));
            FacilioField child_field = fieldsMap.get("parentId");
            FacilioField parent_field = FieldFactory.getIdField(meterModule);
            applyJoin(null,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), meterModule, selectBuilder, addedModules);

            if(dimension.getDimension_type() != V2DimensionContext.DimensionType.ASSET.getIndex() && dimension.getDimension_type() != V2DimensionContext.DimensionType.METER.getIndex()) {
                FacilioField meter_child_field = null;
                if(dimension.getDimension_type() == V2DimensionContext.DimensionType.SITE.getIndex()){
                    meter_child_field = modBean.getField("siteId", meterModule.getName());
                }else {
                    Map<String, FacilioField> meter_fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(meterModule.getName()));
                    meter_child_field = meter_fieldsMap.get("meterLocation");
                }
                V2AnalyticsOldUtil.applyResourcesJoinsForMeter(selectBuilder, meter_child_field , addedModules);
            }
        }
    }

    public static void applyResourcesJoinsForMeter(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, FacilioField meter_field , Set<FacilioModule> addedModules)throws Exception
    {
        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        if (!V2AnalyticsOldUtil.isAlreadyAdded(addedModules, resourceModule)) {
            FacilioField resource_id_field = FieldFactory.getIdField(resourceModule);
            selectBuilder.addJoinModules(Collections.singletonList(resourceModule));
            StringBuilder join_criteria = new StringBuilder(resource_id_field.getCompleteColumnName()).append(" = ").append(meter_field.getCompleteColumnName());
            join_criteria.append(" AND ").append(resourceModule.getTableName()).append(".ORGID = ").append(meter_field.getModule().getTableName()).append(".ORGID");
            selectBuilder.innerJoin(resourceModule.getTableName())
                    .on(join_criteria.toString());
            addedModules.add(resourceModule);

            selectBuilder.andCustomWhere("( Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = false) ");
        }
        FacilioModule baseSpaceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE);
        selectBuilder.addJoinModules(Collections.singletonList(baseSpaceModule));
        StringBuilder space_join_criteria = new StringBuilder(resourceModule.getTableName()).append(".SPACE_ID = ").append(baseSpaceModule.getTableName()).append(".ID");
        space_join_criteria.append(" AND ").append(resourceModule.getTableName()).append(".ORGID = ").append(baseSpaceModule.getTableName()).append(".ORGID");
        selectBuilder.innerJoin(baseSpaceModule.getTableName())
                .on(space_join_criteria.toString());
        addedModules.add(baseSpaceModule);
    }
    public static String getXValues(List<Map<String, Object>> props, String key, FieldType type) {
        if (props != null && !props.isEmpty()) {
            StringJoiner xValues = new StringJoiner(",");
            for (Map<String, Object> prop : props) {
                Object val = prop.get(key);
                if (val != null) {
                    if (val instanceof Map && type == FieldType.LOOKUP) {
                        val = ((Map) val).get("id");
                    }
                    xValues.add(val.toString());
                }
            }
            return xValues.toString();
        }
        return null;
    }

    public static void CREDActionV2UserFilter(Long reportId, List<V2AnalyticUserFilterContext> user_filters, String action)throws Exception
    {
        for(V2AnalyticUserFilterContext user_filter: user_filters)
        {
            user_filter.setReportId(reportId);
            if(action.equals("create")) {
                insertNewReportV2UserFilter(user_filter);
            }
            else if(action.equals("update"))
            {
                updateNewReportV2UserFilter(user_filter);
            }
            else if(action.equals("delete")){
                deleteNewReportV2UserFilter(user_filter);
            }
        }
    }
    private static void deleteNewReportV2UserFilter(V2AnalyticUserFilterContext user_filter)throws Exception
    {
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getV2ReportUserFilterModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("ID", "id", user_filter.getId()+"", NumberOperators.EQUALS));
        deleteBuilder.delete();
    }

    private static void insertNewReportV2UserFilter(V2AnalyticUserFilterContext user_filter)throws Exception
    {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getV2ReportUserFilterModule().getTableName())
                .fields(FieldFactory.getV2ReportUserFilterFields());

        insertBuilder.insert(FieldUtil.getAsProperties(user_filter));
    }
    private static void updateNewReportV2UserFilter(V2AnalyticUserFilterContext user_filter)throws Exception
    {
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getV2ReportUserFilterModule().getTableName())
                    .fields(FieldFactory.getV2ReportUserFilterFields())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", user_filter.getId()+"", NumberOperators.EQUALS));
            updateBuilder.update(FieldUtil.getAsProperties(user_filter));
    }
    public static void splitCreateUpdateAndDeleteFilter(Long reportId, List<V2AnalyticUserFilterContext> newUserFilters)throws Exception
    {
        List<V2AnalyticUserFilterContext> old_userFilters = V2AnalyticsOldUtil.getNewReportV2UserFilter(reportId);
        if(old_userFilters != null && old_userFilters.size() > 0 && newUserFilters != null && newUserFilters.size() > 0)
        {
            Map<Long, V2AnalyticUserFilterContext> idMap = old_userFilters.stream()
                    .collect(Collectors.toMap(V2AnalyticUserFilterContext::getId, userFilter -> userFilter));
            List<V2AnalyticUserFilterContext> add = new ArrayList<>();
            List<V2AnalyticUserFilterContext> update = new ArrayList<>();
            List<V2AnalyticUserFilterContext> delete = new ArrayList<>();
            List<Long> update_filter_ids = new ArrayList<>();
            for(V2AnalyticUserFilterContext usr_filter : newUserFilters)
            {
                if(usr_filter.getId() > 0 && idMap.containsKey(usr_filter.getId()))
                {
                    update.add(idMap.get(usr_filter.getId()));
                    update_filter_ids.add(usr_filter.getId());
                }
                else {
                    add.add(usr_filter);
                }
            }
            for(V2AnalyticUserFilterContext usr_filter : old_userFilters)
            {
                if(usr_filter.getId() > 0 && !update_filter_ids.contains(usr_filter.getId())){
                    delete.add(usr_filter);
                }
            }
            V2AnalyticsOldUtil.CREDActionV2UserFilter(reportId, add, "create");
            V2AnalyticsOldUtil.CREDActionV2UserFilter(reportId, update, "update");
            V2AnalyticsOldUtil.CREDActionV2UserFilter(reportId, delete, "delete");
        }
        else if(old_userFilters == null && newUserFilters != null && newUserFilters.size() > 0){
            V2AnalyticsOldUtil.CREDActionV2UserFilter(reportId, newUserFilters, "create");
        }else if(newUserFilters == null && old_userFilters != null && old_userFilters.size() > 0){
            V2AnalyticsOldUtil.CREDActionV2UserFilter(reportId, old_userFilters, "delete");
        }
    }
    private static List<V2AnalyticUserFilterContext> getNewReportV2UserFilter(Long reportId)throws Exception
    {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getV2ReportUserFilterModule().getTableName())
                .select(FieldFactory.getV2ReportUserFilterFields())
                        .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportId+"", NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0)
        {
            return FieldUtil.getAsBeanListFromMapList(props, V2AnalyticUserFilterContext.class);
        }
        return null;
    }

    public static JSONArray getDataPointForReadingRule(Long resourceId, Long readingRuleId) throws Exception {
        NewReadingRuleContext readingRule = NewReadingRuleAPI.getReadingRule(readingRuleId);
        NameSpaceContext ns = NamespaceAPI.getNameSpaceByRuleId(readingRuleId, NSType.READING_RULE);

        JSONArray dataPoints = new JSONArray();
        dataPoints.addAll(getDataPointsJSONForReadingRule(readingRule, ns, resourceId));
        ReportUtil.setAliasForDataPoints(dataPoints, -1L);
        return dataPoints;
    }

    public static JSONArray getDataPointsJSONForReadingRule(NewReadingRuleContext readingRule, NameSpaceContext ns, Long resourceId) throws Exception {
        JSONArray measureArray = new JSONArray();

        String parentModuleName = getModuleNameFromCategory(readingRule.getCategoryId(), readingRule.getResourceTypeEnum().getName().toLowerCase(Locale.ROOT));
        List<NameSpaceField> fields = ns.getFields();
        measureArray.add(getMeasureForField(readingRule.getReadingFieldId(), readingRule.getCategoryId(), parentModuleName, resourceId));

        Set<String> fieldIdResourceId = new HashSet<>(); // to avoid duplicates
        for (NameSpaceField nsField : fields) {
            List<Long> parentIds = getParentIdsForField(resourceId, nsField);
            if (CollectionUtils.isEmpty(parentIds)) {
                continue;
            }

            Long catIdForField = getCategoryForField(nsField, readingRule);
            String categoryName = parentModuleName;
            if (!Objects.equals(catIdForField, readingRule.getCategoryId())) {
                categoryName = getModuleNameFromCategory(catIdForField, readingRule.getResourceTypeEnum().getName().toLowerCase(Locale.ROOT));
            }

            for (Long parentId : parentIds) {
                if (!fieldIdResourceId.add(nsField.getFieldId() + "_" + parentId)) {
                    continue;
                }
                measureArray.add(getMeasureForField(nsField.getFieldId(), catIdForField, categoryName, parentId));
            }
        }
        return measureArray;
    }

    private static List<Long> getParentIdsForField(Long resourceId, NameSpaceField nsField) throws Exception {
        if (nsField.getNsFieldType().equals(RELATED_READING)) {
            return RelationUtil.getAllCustomRelationsForRecId(nsField.getRelatedInfo().getRelMapContext(), resourceId);
        }
        return Collections.singletonList(nsField.getResourceId() != null ? nsField.getResourceId() : resourceId);
    }

    private static JSONObject getMeasureForField(Long readingFieldId, Long categoryId, String parentModuleName, Long parentId) throws Exception {
        JSONObject measureJson = new JSONObject();
        measureJson.putAll(constructFieldObject(Constants.getModBean().getField(readingFieldId), categoryId));
        measureJson.put("aggr", 0);
        measureJson.put("type", 1);
        measureJson.put("parentModuleName", parentModuleName);

        JSONArray parentIdsJson = FieldUtil.getAsJSONArray(Collections.singletonList(Long.toString(parentId)), String.class);
        JSONObject filterJson = new JSONObject();//getCriteriaFromFilters
        JSONObject operatorJson = new JSONObject();
        operatorJson.put("operatorId", 9);
        operatorJson.put("value", parentIdsJson);
        filterJson.put("id", operatorJson);

        measureJson.put("parentId", parentIdsJson);
        measureJson.put("criteriaType", 2);
        measureJson.put("criteria", FilterUtil.getCriteriaFromQuickFilter(filterJson, parentModuleName));

        return measureJson;
    }

    private static Long getCategoryForField(NameSpaceField nsField, NewReadingRuleContext readingRule) throws Exception {
        if (nsField.getNsFieldType() == RELATED_READING) {
            return Optional.ofNullable(AssetsAPI.getCategoryByAssetModule(nsField.getRelatedInfo().getRelMapContext().getFromModuleId()))
                    .map(ModuleBaseWithCustomFields::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid ModuleId given for fetching Category"));
        }

        if (nsField.getResourceId() == null) {
            return readingRule.getCategoryId();
        }
        AssetContext assetInfo = AssetsAPI.getAssetInfo(nsField.getResourceId());
        return Optional.ofNullable(assetInfo)
                .map(AssetContext::getCategory)
                .map(ModuleBaseWithCustomFields::getId)
                .orElse(-1L);
    }

    public static JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext, ResourceContext resource,
                                                AlarmOccurrenceContext alarm, Set readingMap) throws Exception {
        JSONArray dataPoints = new JSONArray();
        ResourceContext currentResource = resource;
        ModuleBean moduleBean = Constants.getModBean();
        String parentModuleName = V2AnalyticsOldUtil.getModuleNameFromCategory(readingruleContext.getAssetCategoryId(), "asset");
        if (readingruleContext.getRuleMetrics() != null && !readingruleContext.getRuleMetrics().isEmpty()) {

            for (ReadingRuleMetricContext ruleMetric : readingruleContext.getRuleMetrics()) {
                long resourceId = resource.getId();
                if (ruleMetric.getResourceId() > 0) {
                    resourceId = ruleMetric.getResourceId();
                }
                JSONObject measureJson = new JSONObject();
                measureJson.putAll(constructFieldObject(moduleBean.getField(ruleMetric.getFieldId()), readingruleContext.getAssetCategoryId()));
                measureJson.put("aggr", 0);

                JSONArray parentIds = FacilioUtil.getSingleTonJsonArray(resourceId);
                JSONObject filterJson = new JSONObject();//getCriteriaFromFilters
                JSONObject operatorJson = new JSONObject();
                operatorJson.put("operatorId", 9);
                operatorJson.put("value", parentIds);
                filterJson.put("id", operatorJson);

                measureJson.put("parentId", parentIds);
                measureJson.put("criteriaType", 2);
                measureJson.put("criteria", FilterUtil.getCriteriaFromQuickFilter(filterJson, parentModuleName));
                measureJson.put("parentModuleName", parentModuleName);
                dataPoints.add(measureJson);
            }
            return dataPoints;
        }

        if (readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {

            if (readingruleContext.getWorkflowId() > 0) {

                WorkflowContext workflow = new WorkflowContext();
                FacilioModule module = ModuleFactory.getWorkflowModule();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(FieldFactory.getWorkflowFields())
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(readingruleContext.getWorkflowId(), module));

                List<Map<String, Object>> props = selectBuilder.get();

                WorkflowContext workflowContext = null;
                if (props != null && !props.isEmpty() && props.get(0) != null) {
                    Map<String, Object> prop = props.get(0);
                    boolean isWithExpParsed = true;

                    workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowContext.class);
                    if (workflowContext.isV2Script()) {
                        if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.XML.getValue()) {
                            workflowContext = WorkflowUtil
                                    .getWorkflowContextFromString(workflowContext.getWorkflowString(), workflowContext);
                            if (isWithExpParsed) {
                                WorkflowUtil.parseExpression(workflowContext);
                            }
                        } else if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.GUI
                                .getValue()) {
                            workflowContext.parseScript();
                        }
                        workflow = workflowContext;
                    } else {
                        workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);
                    }
                }

                for (WorkflowExpression workflowExp : workflow.getExpressions()) {

                    if (!(workflowExp instanceof com.facilio.workflows.context.ExpressionContext)) {
                        continue;
                    }
                    com.facilio.workflows.context.ExpressionContext exp = (ExpressionContext) workflowExp;
                    if (exp.getModuleName() != null) {

                        JSONObject dataPoint = new JSONObject();

                        FacilioField readingField = null;
                        if (exp.getFieldName() != null) {
                            readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());

//                            updateTimeRangeAsPerFieldType(readingField.getFieldId());

                            JSONObject yAxisJson = new JSONObject();
                            yAxisJson.put("fieldId", readingField.getFieldId());
                            yAxisJson.put("aggr", 0);

                            dataPoint.put("yAxis", yAxisJson);

                        }
                        if (exp.getCriteria() != null) {
                            Map<String, Condition> conditions = exp.getCriteria().getConditions();

                            for (String key : conditions.keySet()) {

                                Condition condition = conditions.get(key);

                                if (condition.getFieldName().equals("parentId")) {
                                    resource = condition.getValue().equals("${resourceId}") ? currentResource
                                            : ResourceAPI.getResource(Long.parseLong(condition.getValue()));

                                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                                    break;
                                }
                            }
                        }
                        dataPoint.put("type", 1);
                        if (!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
                            readingMap.add(resource.getId() + "_" + readingField.getFieldId());
                            dataPoints.add(dataPoint);
                        }
                    }
                }
            }
        } else if (readingruleContext.getReadingFieldId() > 0
                && (readingruleContext.getWorkflow() != null || readingruleContext.getCriteria() != null)) {


            JSONObject measureJson = new JSONObject();
            measureJson.putAll(constructFieldObject(moduleBean.getField(readingruleContext.getReadingFieldId()), readingruleContext.getAssetCategoryId()));
            measureJson.put("aggr", 0);

            JSONArray parentIds = FacilioUtil.getSingleTonJsonArray(resource.getId());
            JSONObject filterJson = new JSONObject();//getCriteriaFromFilters
            JSONObject operatorJson = new JSONObject();
            operatorJson.put("operatorId", 9);
            operatorJson.put("value", parentIds);
            filterJson.put("id", operatorJson);

            measureJson.put("parentId", parentIds);
            measureJson.put("criteriaType", 2);
            measureJson.put("criteria", FilterUtil.getCriteriaFromQuickFilter(filterJson, parentModuleName));
            measureJson.put("parentModuleName", parentModuleName);
            measureJson.put("type", 1);
            dataPoints.add(measureJson);
        }
        return dataPoints;
    }
    public static Map<String, Object> constructFieldObject(FacilioField field, Long category)
    {
        Map<String, Object> details =new HashMap<>();
        details.put("name", field.getName());
        details.put("displayName", field.getDisplayName());
        details.put("categoryId", category);
        details.put("fieldId", field.getFieldId());
        details.put("id", field.getFieldId());
        details.put("dataType", field.getDataType());
        details.put("default", field.getDefault());
        details.put("module", Collections.singletonMap("type", field.getModule().getType()));
        if (field instanceof NumberField) {
            NumberField numberField = (NumberField)field;
            details.put("unit", numberField.getUnit());
        }
        return details;
    }

    public static String getModuleNameFromCategory(Long categoryId, String type)throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getCategoryModuleChain();
        FacilioContext kpi_context = chain.getContext();
        kpi_context.put("categoryId", categoryId);
        kpi_context.put("type", type);
        chain.execute();
        return (String) kpi_context.get("moduleName");
    }
}
