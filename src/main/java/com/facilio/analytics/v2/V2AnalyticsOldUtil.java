package com.facilio.analytics.v2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.analytics.v2.context.V2AnalyticsReportResponseContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BaseLineAPI;
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
import com.facilio.modules.fields.*;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

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
        report_context.setDateOperator(report.getTimeFilter().getDateOperatorEnum() != null ? report.getTimeFilter().getDateOperatorEnum() : DateOperators.BETWEEN);
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
    public static String getAndSetModuleAlias(String moduleName, HashMap<String, String> moduleVsAlias) {
        String alias = "";
        if (moduleVsAlias.containsKey(moduleName)) {
            return moduleVsAlias.get(moduleName);
        }
        alias = moduleVsAlias.values().size() > 0 ? ReportUtil.getAlias((String) moduleVsAlias.values().toArray()[moduleVsAlias.values().size() - 1]):ReportUtil.getAlias("");
        moduleVsAlias.put(moduleName, alias);
        return alias;
    }
    public static void applyJoin(HashMap<String, String> moduleVsAlias, FacilioModule baseModule, String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder)
    {
        if (joinModule != null && (joinModule.isCustom() && !baseModule.equals(joinModule))) {
            selectBuilder.innerJoin(joinModule.getTableName())
                    .alias(V2AnalyticsOldUtil.getAndSetModuleAlias(joinModule.getName(), moduleVsAlias))
                    .on(on);
        } else {
            selectBuilder.innerJoin(joinModule.getTableName()).on(on);
        }
        selectBuilder.addJoinModules(Collections.singletonList(joinModule));
        FacilioModule prevModule = joinModule;
        FacilioModule extendedModule = prevModule.getExtendModule();
        while (extendedModule != null)
        {
            selectBuilder.addJoinModules(Collections.singletonList(extendedModule));
            selectBuilder.innerJoin(extendedModule.getTableName())
                    .on(prevModule.getTableName() + ".ID = " + extendedModule.getTableName() + ".ID");
            prevModule = extendedModule;
            extendedModule = extendedModule.getExtendModule();
        }
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
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(resourceModule.getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(resourceModule)))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", "false", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), resourceModule))
                .andCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", String.valueOf("1"), NumberOperators.EQUALS));
        if (dp.getxAxis().getModuleName().equals(FacilioConstants.ContextNames.ALARM_OCCURRENCE)) {
            Criteria spaceCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ContextNames.BUILDING);
            builder.andCriteria(spaceCriteria);
        }
        selectBuilder.andCustomWhere(spaceField.getCompleteColumnName() + " in (" + builder.constructSelectStatement() + ")");
        spaceField.setName(field.getName());
        spaceField.setDataType(FieldType.NUMBER);
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
    public static void applyDashboardUserFilterCriteria(FacilioModule baseModule, JSONObject dbUserFilter, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder)throws Exception
    {
        if(dbUserFilter != null)
        {
            Map<String, JSONObject> filterMap = (Map<String, JSONObject>) dbUserFilter.get(dataPoint.getAliases().get("actual"));
            if(filterMap != null && !filterMap.isEmpty())
            {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField appliedField = modBean.getField("parentId", baseModule.getName());
                Criteria criteria = new Criteria();
                for(String alias: filterMap.keySet())
                {
                    Condition condition = new Condition();
                    condition.setField(appliedField);
                    HashMap selected_dp_map = (HashMap) filterMap.get(String.valueOf(alias));
                    Long operatorId = (Long) selected_dp_map.get("operatorId");
                    condition.setOperatorId(operatorId.intValue());
                    StringJoiner parentIds = new StringJoiner(",");
                    if (alias.equals("space")) {
                        List<Long> baseSpaceIds = new ArrayList<>();
                        List<String> value = (List<String>) selected_dp_map.get("value");
                        value.forEach(val -> baseSpaceIds.add(Long.parseLong(val)));
                        List<Long> values = AssetsAPI.getAssetIdsFromBaseSpaceIds(baseSpaceIds);
                        if (values != null && values.size() > 0) {
                            values.forEach(id -> parentIds.add(String.valueOf(id)));
                            condition.setValue(parentIds.toString());
                        }
                    } else {
                        List<String> selected_values = (List<String>) selected_dp_map.get("value");
                        selected_values.forEach(id -> parentIds.add(id));
                        condition.setValue(parentIds.toString());
                    }
                    criteria.addAndCondition(condition);
                }
                selectBuilder.andCriteria(criteria);
            }

        }
    }
    public static void applyMeasureCriteriaV2(HashMap<String, String> moduleVsAlias, FacilioField xAggrField, FacilioModule baseModule, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, String xValues)throws Exception
    {
        if (xValues != null)
        {
            selectBuilder.andCondition(CriteriaAPI.getEqualsCondition(xAggrField, xValues));
        }
        if(dataPoint.getCriteriaType() == V2MeasuresContext.Criteria_Type.CRITERIA.getIndex() && dataPoint.getV2Criteria() != null && !dataPoint.getV2Criteria().isEmpty())
        {
            if(dataPoint.getParentReadingModule() != null)
            {
                FacilioField parent_field = FieldFactory.getIdField(dataPoint.getParentReadingModule());
                if(dataPoint.getyAxis().getField() != null)
                {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName()));
                    FacilioField child_field = fieldsMap.get("parentId");
                    applyJoin(moduleVsAlias,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), dataPoint.getParentReadingModule(), selectBuilder);
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
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);

        SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder()
                .select(Collections.singleton(FieldFactory.getIdField(module)))
                .table(module.getTableName());

        if(criteria != null) {
            selectBuilder.andCriteria(criteria);
        }
        List<Map<String, Object>> props = selectBuilder.get();
        List<Long> assetIds = new ArrayList<>();
        if(props != null && props.size() > 0)
        {
            for(Map<String, Object> prop :props) {
                assetIds.add((Long)prop.get(FieldFactory.getIdField(module).getName()));
            }
        }
        return assetIds;
    }
}
