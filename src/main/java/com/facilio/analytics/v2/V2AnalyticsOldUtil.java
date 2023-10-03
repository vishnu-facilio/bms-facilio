package com.facilio.analytics.v2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.context.V2AnalyticsReportResponseContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class V2AnalyticsOldUtil {
    private static final List<ReadingAnalysisContext.ReportMode> SUPPORTED_PORTFOLIO_MODES = Arrays.asList(
            ReadingAnalysisContext.ReportMode.TIME_CONSOLIDATED,
            ReadingAnalysisContext.ReportMode.SITE,
            ReadingAnalysisContext.ReportMode.BUILDING,
            ReadingAnalysisContext.ReportMode.RESOURCE,
            ReadingAnalysisContext.ReportMode.SPACE,
            ReadingAnalysisContext.ReportMode.FLOOR
    );
    public static ReportContext constructReportOld(V2ReportContext report, ReportContext report_context)throws Exception
    {
        report_context.setName(report.getName());
        report_context.setDateOperator(DateOperators.BETWEEN);
        report_context.setDateValue(new StringBuilder().append(report.getTimeFilter().getStartTime()).append(", ").append(report.getTimeFilter().getEndTime()).toString());
        String baseLinesString = report.getBaseLinesString();
        V2AnalyticsOldUtil.fetchBaseLines(report, baseLinesString,report_context);
        report_context.setChartState(report.getChartState());
        if(report.getTimeFilter().getDateOperator() != -1)
        {
            report_context.setDateOperator(report.getTimeFilter().getDateOperator());
        }
        report_context.setxAlias(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS);
        report_context.setxAggr(report.getDimensions().getxAggr());
        report_context.setAnalyticsType(ReadingAnalysisContext.AnalyticsType.PORTFOLIO);
        if(report.getGroupBy() != null && report.getGroupBy().getTime_aggr() > 0)
        {
            report_context.setgroupByTimeAggr(report.getGroupBy().getTime_aggrEnum());
            report_context.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, report.getGroupBy().getTime_aggr());
        }
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
                if(fieldMap.get("parentId")!= null) {
                    xField = fieldMap.get("parentId");
                }else {
                    FacilioModule module = bean.getModule(mode.getStringVal());
                    xField = FieldFactory.getIdField(module);
                }
                dataPoint.setFetchResource(true);
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
    }
    public static void updateNewReportV2(V2ReportContext reportContext) throws Exception
    {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getV2ReportModule().getTableName())
                .fields(FieldFactory.getV2ReportModuleFields())
                .andCondition(CriteriaAPI.getCondition("REPORT_ID", "reportId", reportContext.getReportId()+"", NumberOperators.EQUALS));
        Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
        updateBuilder.update(props);
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
            return FieldUtil.getAsBeanFromMap(props.get(0), V2ReportContext.class);
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
        return selected_fields;
    }
}
