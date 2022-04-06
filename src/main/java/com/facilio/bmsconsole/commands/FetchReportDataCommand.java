package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AggregationColumnMetaContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AggregationAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.*;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.util.DemoHelperUtil;
import com.facilio.report.util.FilterUtil;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


public class FetchReportDataCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(FetchReportDataCommand.class.getName());
    private FacilioModule baseModule;
    private ReportType reportType;
    private FacilioModule reportModule;
    private ModuleBean modBean;
    private Boolean enableFutureData = false;
    private LinkedHashMap<String, String> moduleVsAlias = new LinkedHashMap<String, String>();
    private DateRange originalDateRange;    // this is used for demo accounts
    private Context globalContext;
    private Boolean isSubmoduleDataFetched = false;
    private List<Map<String, Object>> subModuleQueryResult = new ArrayList<>();
    private List<FacilioField> globalFields = new ArrayList<>();
    private Boolean isBaseModuleJoined = false;
    private List<Map<String, Object>> pivotFieldsList = new ArrayList<>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        globalContext = context;
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        if (report.getTypeEnum() != null) {
            reportType = report.getTypeEnum();
        }

        if (report.getDataPoints() == null || report.getDataPoints().isEmpty()) {
            return false;
        }

        reportModule = modBean.getModule(report.getModuleId());

        LinkedHashMap<String, String> tableAlias = (LinkedHashMap<String, String>) context.get(FacilioConstants.ContextNames.TABLE_ALIAS);

        if (tableAlias != null) {
            moduleVsAlias = tableAlias;
        }

        Boolean handleBooleanFields = (Boolean) context.get(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN);
        if (handleBooleanFields == null) {
            handleBooleanFields = false;
        }

        calculateBaseLineRange(report);

        JSONObject dateField = (JSONObject) context.get("date-field");

        if (report != null && dateField != null && (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) != null) {
            JSONObject reportState;
            if (report.getReportState() != null && !report.getReportState().isEmpty()) {
                reportState = report.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA, (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA));
            report.setReportState(reportState);
        }

        Boolean shouldIncludeMarked = (Boolean)
                context.get(FacilioConstants.ContextNames.SHOULD_INCLUDE_MARKED);
        Boolean getModuleFromDp = (Boolean)
                context.get(FacilioConstants.ContextNames.GET_MODULE_FROM_DP);
        if (shouldIncludeMarked == null) {
            shouldIncludeMarked = false;
        }
        if (getModuleFromDp == null) {
            getModuleFromDp = false;
        }


        if (report != null && report.getReportState() != null && !report.getReportState().isEmpty()) {
            if (report.getReportState().containsKey(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA)
                    && (Boolean) report.getReportState().get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) == true) {
                enableFutureData = true;
            }
        }

        List<ReportDataContext> reportData = new ArrayList<>();
        List<ReportDataPointContext> dataPoints = new ArrayList<>(report.getDataPoints());
        if (report.getReportTemplate() != null) {
            Long reportParentId = report.getReportTemplate().getParentId();
            Long templateType = report.getReportTemplate().getTemplateType();
            if (templateType != null && templateType == 2) {
                Map<String, ReportTemplateCategoryFilterContext> categoryTemplate = report.getReportTemplate()
                        .getCategoryTemplate();
                Map<String, Object> metaData = new HashMap<>();
                List<Long> parentIds = new ArrayList<>();
                for (ReportDataPointContext dataPoint : dataPoints) {
                    ReportTemplateCategoryFilterContext filter = categoryTemplate.get(dataPoint.getAliases().get("actual"));
                    if (filter != null) {
                        parentIds.add(filter.getParentId());
                        metaData.put("parentIds", parentIds);
                        dataPoint.setMetaData(metaData);
                        dataPoint.setName(dataPoint.getyAxis().getLabel());
                    }

                }
            } else {
                if (reportParentId != null) {
                    Map<String, Object> metaData = new HashMap<>();
                    List<Long> parentIds = new ArrayList<>();
                    parentIds.add(reportParentId);
                    metaData.put("parentIds", parentIds);
                    for (ReportDataPointContext dataPoint : dataPoints) {
                        dataPoint.setMetaData(metaData);
                        dataPoint.setName(dataPoint.getyAxis().getLabel());
                    }
                }
            }
        }
        ReportDataPointContext sortPoint = getSortPoint(dataPoints);
        ReportDataContext sortedData = null;
        if (sortPoint != null) {
            sortedData = fetchDataForGroupedDPList(Collections.singletonList(sortPoint), report, false, null, shouldIncludeMarked, getModuleFromDp);
            reportData.add(sortedData);
        }
        List<List<ReportDataPointContext>> groupedDataPoints = groupDataPoints(dataPoints, handleBooleanFields, report.getTypeEnum(), report.getxAggrEnum(), report.getDateRange());
        if (groupedDataPoints != null && !groupedDataPoints.isEmpty()) {
            for (int i = 0; i < groupedDataPoints.size(); i++) {
                List<ReportDataPointContext> dataPointList = groupedDataPoints.get(i);
                if (ReportContext.ReportType.READING_REPORT.getValue() == report.getType() && handleUserScope(dataPointList.get(0))) {
                    dataPoints.remove(dataPointList.get(0));
                    report.setHasEdit(false);
                } else if (isSortPointIncluded(dataPointList.get(0), report.getTypeEnum())) {
                    ReportDataContext data = fetchDataForGroupedDPList(dataPointList, report, sortPoint != null, sortPoint == null ? null : sortedData.getxValues(), shouldIncludeMarked, getModuleFromDp);
                    reportData.add(data);
                }
            }
            if (dataPoints.isEmpty()) {
                dataPoints.add(groupedDataPoints.get(0).get(0));
            }
            report.setDataPoints(dataPoints);
        }

        if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
            LOGGER.info("Report Data : " + reportData);
        }

        context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
        return false;
    }

    private ReportDataPointContext getSortPoint(List<ReportDataPointContext> dataPoints) {
        Iterator<ReportDataPointContext> itr = dataPoints.iterator();
        while (itr.hasNext()) {
            ReportDataPointContext dp = itr.next();
            if (dp.isDefaultSortPoint()) {

                if (dp.getLimit() == -1 || dp.getOrderByFuncEnum() == OrderByFunction.NONE) {
                    throw new IllegalArgumentException("Default sort datapoint should have order by and limit");
                }

//				itr.remove();
                return dp;
            }
        }
        return null;
    }

    private ReportDataContext fetchDataForGroupedDPList(List<ReportDataPointContext> dataPointList, ReportContext report, boolean hasSortedDp, String xValues, boolean shouldIncludeMarked, boolean getModuleFromDp) throws Exception {
        ReportDataContext data = new ReportDataContext();
        data.setDataPoints(dataPointList);

        ReportDataPointContext dp = dataPointList.get(0); //Since order by, criteria are same for all dataPoints in a group, we can consider only one for the builder

        if (report.getTypeEnum() == ReportType.WORKORDER_REPORT || report.getTypeEnum() == ReportType.PIVOT_REPORT) {
            if (report.getModuleId() > 0) {
                baseModule = modBean.getModule(report.getModuleId());
            } else {
                baseModule = dp.getxAxis().getModule();
            }
        } else {
            baseModule = dp.getxAxis().getModule();
        }

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(baseModule) //Assuming X to be the base module
                .setAggregation();
        Set<FacilioModule> addedModules = new HashSet<>();
        addedModules.add(baseModule);


//		if (report.getModuleId() > 0 && report.getTypeEnum() == ReportType.PIVOT_REPORT) {
//			reportModule = modBean.getModule(report.getModuleId());
//			if(!baseModule.equals(reportModule)) {
//				List<FacilioField> allFields = modBean.getAllFields(baseModule.getName()); // for now base module is enough
//				Map<String, LookupField> lookupFields = getLookupFields(allFields);
//				handleLookupJoin(lookupFields, reportModule, selectBuilder, addedModules, null);
//			}
//		}

        if (!shouldIncludeMarked) {
            FacilioField marked = modBean.getField("marked", baseModule.getName());
            if (marked != null) {
                selectBuilder.andCondition(CriteriaAPI.getCondition(marked, "false", BooleanOperators.IS));
            }
        }
        List<FacilioField> fields = new ArrayList<>();
        if (report.getTypeEnum() == ReportType.PIVOT_REPORT) {
            for (ReportDataPointContext point : dataPointList) {
                if (point.getyAxis().getModule() != null) {
                    if (point.getyAxis().getModule().getTypeEnum() == ModuleType.READING) {
                        applyReadingResourceJoin(point, selectBuilder, fields, addedModules);
                    } else {
                        handleJoin(point.getyAxis(), selectBuilder, addedModules);
                    }
                } else {
                    handleJoin(point.getyAxis(), selectBuilder, addedModules);
                }
            }

        } else {
            joinModuleIfRequred(dp.getyAxis(), selectBuilder, addedModules);
        }
        applyOrderByAndLimit(dp, selectBuilder, report.getxAggrEnum());
        StringJoiner groupBy = new StringJoiner(",");
        FacilioField xAggrField = applyXAggregation(dp, report.getxAggrEnum(), groupBy, selectBuilder, fields, addedModules);
        if (report.getgroupByTimeAggr() > 0) {
            applygroupByTimeAggr(dp, report.getgroupByTimeAggrEnum(), groupBy);
        }
        setYFieldsAndGroupByFields(dataPointList, fields, xAggrField, groupBy, dp, selectBuilder, addedModules);
        List<FacilioField> cloneFields = new ArrayList<>();
        if (reportType == ReportType.PIVOT_REPORT) {
            fields.clear();
            for (Map<String, Object> pivotField : pivotFieldsList) {
                FacilioField facilioField = (FacilioField) pivotField.get("field");
                cloneFields.add(facilioField);
                fields.add(facilioField);
            }
        } else {
            for (FacilioField field : fields) {
                if (field != null) {
                    FacilioField cloneField = field.clone();
                   if(cloneField instanceof MultiEnumField)
                   {
                        List<FacilioField> list = modBean.getAllFields(((MultiEnumField)cloneField).getRelModule().getName());
                        for(FacilioField multi_select_fld : list)
                        {
                           if(multi_select_fld.getColumnName().equals("INDEX_ID"))
                           {
                               cloneFields.add(multi_select_fld);
                           }
                        }
                    }
                    else if(cloneField instanceof MultiLookupField)
                    {
                        List<FacilioField> list = modBean.getAllFields(((MultiLookupField)cloneField).getRelModule().getName());
                        for(FacilioField multi_select_fld : list)
                        {
                            if(multi_select_fld.getColumnName().equals("RIGHT_ID"))
                            {
                                cloneFields.add(multi_select_fld);
                            }
                        }
                    }
                    else {
                        cloneFields.add(cloneField);
                    }
                }
            }
        }
        globalFields = cloneFields;
        if (report.getTypeEnum() == ReportType.PIVOT_REPORT) {
            cloneFields.add(FieldFactory.getIdField(baseModule));
        }
        selectBuilder.select(cloneFields);

        if (reportType == ReportType.PIVOT_REPORT) {
            pivotFieldsList = pivotFieldsList.stream().distinct().collect(Collectors.toList());
//            filterDuplicateItems(pivotFieldsList);
            for (Map<String, Object> pivotField : pivotFieldsList) {
                handlePivotJoin(pivotField, selectBuilder);
            }
        }

        boolean noMatch = hasSortedDp && (xValues == null || xValues.isEmpty());
        Map<String, List<Map<String, Object>>> props = new HashMap<>();
        List<Map<String, Object>> dataProps = noMatch ? Collections.EMPTY_LIST : fetchReportData(report, dp, selectBuilder, null, xAggrField, xValues, addedModules);
        props.put(FacilioConstants.Reports.ACTUAL_DATA, dataProps);
        if (AccountUtil.getCurrentOrg().getId() == 75) {
            LOGGER.info("Date Props : " + dataProps);
        }
        if (dp.getLimit() != -1 && xValues == null) {
            data.setxValues(getXValues(dataProps, dp.getxAxis().getFieldName(), dp.getxAxis().getDataTypeEnum()));
            if (data.getxValues() == null || data.getxValues().isEmpty()) {
                noMatch = true;
            }
        }

        if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
            LOGGER.info("X Values : " + xValues);
            LOGGER.info("Data X Values : " + data.getxValues());
        }

        if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
            for (ReportBaseLineContext reportBaseLine : report.getBaseLines()) {
                props.put(reportBaseLine.getBaseLine().getName(), noMatch ? Collections.EMPTY_LIST : fetchReportData(report, dp, selectBuilder, reportBaseLine, xAggrField, data.getxValues() == null ? xValues : data.getxValues(), addedModules));
                data.addBaseLine(reportBaseLine.getBaseLine().getName(), reportBaseLine);
            }
        }

        data.setProps(props);
        return data;
    }

    private String getXValues(List<Map<String, Object>> props, String key, FieldType type) {
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

    private boolean applyFilters(ReportContext report, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) throws Exception {
        if (report.getFilters() != null && !report.getFilters().isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (ReportFilterContext filter : report.getFilters()) {
                if (!filter.isDataFilter() && (filter.getFilterValue() == null || filter.getFilterValue().isEmpty())) {
                    return true;
                }
                if (dataPoint.getTypeEnum() != DataPointType.FIELD) {
                    FacilioField filterField = modBean.getField(filter.getFilterFieldName(), dataPoint.getxAxis().getModuleName());
                    if (filter.isDataFilter()) {
                        selectBuilder.andCondition(CriteriaAPI.getCondition(filterField, filter.getFilterValue(), filter.getFilterOperatorEnum()));
                    } else {
                        selectBuilder.andCondition(CriteriaAPI.getEqualsCondition(filterField, filter.getFilterValue()));
                    }
                }
            }
        }
        return false;
    }

    private void joinModuleIfRequred(ReportFieldContext axis, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
        FacilioModule module;
        if (StringUtils.isNotEmpty(axis.getModuleName())) {
            module = modBean.getModule(axis.getModuleName());
        } else {
            module = axis.getModule();
        }
        if (!baseModule.isParentOrChildModule(module) && !isAlreadyAdded(addedModules, module)) {
            applyJoin(axis.getJoinOn(), module, selectBuilder);
            addedModules.add(module);
        }
    }

    private void applyJoin(String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
        if (joinModule != null && (joinModule.isCustom() && !baseModule.equals(joinModule))) {
            selectBuilder.innerJoin(joinModule.getTableName())
                    .alias(getAndSetModuleAlias(joinModule.getName()))
                    .on(on);
        } else {
            selectBuilder.innerJoin(joinModule.getTableName())
                    .on(on);
        }
        selectBuilder.addJoinModules(Collections.singletonList(joinModule));

        FacilioModule prevModule = joinModule;
        FacilioModule extendedModule = prevModule.getExtendModule();
        while (extendedModule != null) {
            selectBuilder.addJoinModules(Collections.singletonList(extendedModule));
            selectBuilder.innerJoin(extendedModule.getTableName())
                    .on(prevModule.getTableName() + ".ID = " + extendedModule.getTableName() + ".ID");
            prevModule = extendedModule;
            extendedModule = extendedModule.getExtendModule();
        }
    }

    private List<Map<String, Object>> fetchReportData(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext reportBaseLine, FacilioField xAggrField, String xValues, Set<FacilioModule> addedModules) throws Exception {
        SelectRecordsBuilder<ModuleBaseWithCustomFields> newSelectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(selectBuilder);
        if (FacilioProperties.isProduction() && (AccountUtil.getCurrentOrg().getOrgId() == 210 || AccountUtil.getCurrentOrg().getOrgId() == 321l) && !enableFutureData) {
            DateRange dateRange = report.getDateRange();
            if (dateRange != null && originalDateRange != null) {
                Long endTine = DemoHelperUtil.getEndTime(dp.getxAxis().getModule(), originalDateRange.getEndTime());
                dateRange.setEndTime(endTine);
            }
        }
        applyDateCondition(report, dp, newSelectBuilder, reportBaseLine);


        if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
            LOGGER.info("Fetch Data X Values : " + xValues);
        }

        Long parentId = null;
        if (report.getReportTemplate() != null) {
            System.out.println("catergory Filter" + report.getReportTemplate().getCategoryFillter());
            Long templateType = report.getReportTemplate().getTemplateType();
            Map<String, ReportTemplateCategoryFilterContext> template = report.getReportTemplate().getCategoryTemplate();
            if (templateType != null && templateType == 2) {
                if (template.get(dp.getAliases().get("actual")) != null) {
                    parentId = template.get(dp.getAliases().get("actual")).getParentId();
                    ReportTemplateCategoryFilterContext filter = template.get(dp.getAliases().get("actual"));
                    report.getReportTemplate().setParentId(parentId);
                    Criteria templateCriteria = report.getReportTemplate().getCriteria(report, dp);
                    if (templateCriteria != null) {
                        newSelectBuilder.andCriteria(filter.getCriteria(dp));
                    }
                }

            } else {
                  Criteria templateCriteria = report.getReportTemplate().getCriteria(report, dp);
                  if (templateCriteria != null) {
                      parentId = report.getReportTemplate().getParentId();
                      newSelectBuilder.andCriteria(templateCriteria);
                  }
            }
        } else {
            if (xValues == null || report.getTypeEnum() == ReportType.PIVOT_REPORT) {
                if (dp.getAllCriteria() != null) {
                    Criteria allCriteria = dp.getAllCriteria();
                    if(!baseModule.getExtendedModuleIds().contains((dp.getyAxis().getModule().getModuleId()))){
                        Map<String,Condition> conditions = (Map<String, Condition>) allCriteria.getConditions();
                        for (String key : allCriteria.getConditions().keySet()) {
                            Condition condition = conditions.get(key);
                            if(dp.getCriteria() != null && dp.getCriteria().getConditions().containsValue(condition)) {
                                String tableNameAndColumnName = condition.getColumnName();
                                String columnName = tableNameAndColumnName;
                                if(columnName.split("\\.").length > 1){
                                    columnName = columnName.split("\\.")[1];
                                }
                                tableNameAndColumnName = getAndSetModuleAlias(dp.getyAxis().getModuleName()) + "." + columnName;
                                dp.getAllCriteria().getConditions().get(key).setColumnName(tableNameAndColumnName);
                            }
                        }
                    }
                    newSelectBuilder.andCriteria(allCriteria);
                }
            } else {
                newSelectBuilder.andCondition(CriteriaAPI.getEqualsCondition(xAggrField, xValues));
            }
            boolean noMatch = applyFilters(report, dp, newSelectBuilder);
            if (noMatch) {
                return Collections.EMPTY_LIST;
            }
        }

        if (CollectionUtils.isNotEmpty(report.getUserFilters())) {
            for (ReportUserFilterContext userFilter : report.getUserFilters()) {
                Criteria criteria = userFilter.getCriteria();
                if (criteria != null) {
                    newSelectBuilder.andCriteria(criteria);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(dp.getHavingCriteria())) {
            Criteria criteria = new Criteria();
            for (ReportHavingContext reportHavingContext : dp.getHavingCriteria()) {
                FacilioField selectField = reportHavingContext.getAggregateOperatorEnum().getSelectField(reportHavingContext.getField());
                Condition condition = CriteriaAPI.getCondition(selectField, String.valueOf(reportHavingContext.getValue()), reportHavingContext.getOperatorEnum());
                criteria.addAndCondition(condition);
            }
            String havingCondition = criteria.computeWhereClause();
            if (CollectionUtils.isNotEmpty(criteria.getComputedValues())) {
                throw new IllegalArgumentException("Having doesn't supported with operator");
            }
            newSelectBuilder.having(havingCondition);
        }

        if (report.getCriteria() != null) {
            newSelectBuilder.andCriteria(report.getCriteria());
        }

        if (dp.getDpCriteria() != null) {
            newSelectBuilder.andCriteria(dp.getDpCriteria());
        }

        JSONObject timeFilter = report.getTimeFilterJSON();
        if (timeFilter != null && !timeFilter.isEmpty()) {
            Criteria timeFilterCriteria = FilterUtil.getTimeFilterCriteria(dp.getxAxis().getModuleName(), timeFilter);
            if (timeFilterCriteria != null && !timeFilterCriteria.isEmpty()) {
                newSelectBuilder.andCriteria(timeFilterCriteria);
            }
        }
        JSONObject dataFilter = report.getDataFilterJSON();
        if (dataFilter != null && !dataFilter.isEmpty()) {
            FilterUtil.setDataFilterCriteria(dp.getxAxis().getModuleName(), dataFilter, report.getDateRange(), newSelectBuilder, parentId);
        }

        //applying Report drilldown criteria
        ReportDrilldownParamsContext drilldownParams = report.getDrilldownParams();
        if (drilldownParams != null) {
            //drill step criteria can be from different module. join modules from all drill steps
            for (ReportDrilldownParamsContext.DrilldownCriteria drilldownCriteria : drilldownParams.getDrilldownCriteria()) {
                this.handleJoin(drilldownCriteria.getxAxis(), newSelectBuilder, addedModules);
            }


            Criteria drilldownCriteria = drilldownParams.getCriteria();
            if (drilldownCriteria != null) {
                newSelectBuilder.andCriteria(drilldownCriteria);
            }

        }


        List<Map<String, Object>> props = new ArrayList<>();

        ReportFieldContext reportFieldContext = dp.getxAxis();
        boolean outerJoin = reportFieldContext.isOuterJoin();
        if (outerJoin) {
            FacilioField outerJoinField = dp.getxAxis().getField();
            if (!(outerJoinField instanceof LookupField)) {
                throw new IllegalArgumentException("Invalid configuration");
            }

            String aggrFieldName = ReportUtil.getAggrFieldName(dp.getyAxis().getField(), dp.getyAxis().getAggrEnum());
            FacilioModule lookupModule = ((LookupField) outerJoinField).getLookupModule();

            FacilioField idField;
            if (LookupSpecialTypeUtil.isSpecialType(lookupModule.getName())) {
                idField = LookupSpecialTypeUtil.getIdField(lookupModule.getName());
            } else {
                idField = FieldFactory.getIdField(lookupModule);
            }
            FacilioField countField = new FacilioField();
            countField.setColumnName("COALESCE(inn." + aggrFieldName + ", 0)");
            countField.setName(aggrFieldName);

            idField.setName(outerJoinField.getName());

            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.add(idField);
            selectFields.add(countField);
            if (CollectionUtils.isNotEmpty(dp.getGroupByFields())) {
                for (ReportGroupByField groupByField : dp.getGroupByFields()) {
                    FacilioField groupField = new FacilioField();
                    groupField.setColumnName("inn." + groupByField.getField().getName());
                    groupField.setName(groupByField.getField().getName());
                    selectFields.add(groupField);
                }
            }
            String queryString = newSelectBuilder.constructQueryString();
            GenericSelectRecordBuilder newSelect = new GenericSelectRecordBuilder()
                    .table(lookupModule.getTableName())
                    .select(selectFields)
                    .leftJoinQuery(queryString, "inn")
                    .on(lookupModule.getTableName() + "." + idField.getColumnName() + " = inn." + outerJoinField.getName())
                    .andCondition(CriteriaAPI.getCondition(idField.getCompleteColumnName(), idField.getName(), StringUtils.join(dp.getxAxis().getSelectValuesOnly(), ","), NumberOperators.EQUALS));
            newSelect.addWhereValue(Arrays.asList(newSelectBuilder.paramValues()), 0);
            if (CollectionUtils.isNotEmpty(dp.getOrderBy()) && (dp.getOrderByFuncEnum() != null && dp.getOrderByFuncEnum() != OrderByFunction.NONE)) {
                newSelect.orderBy(countField.getCompleteColumnName() + " " + dp.getOrderByFuncEnum().getStringValue());
            }

            props = newSelect.get();
        } else {
            if (report.getTypeEnum() == ReportType.PIVOT_REPORT) {
                List<SupplementRecord> customLookupFields = new ArrayList<>();
                for (FacilioField field : globalFields) {
                    if (field != null && field.getDataTypeEnum() == FieldType.MULTI_ENUM) {
                        customLookupFields.add((SupplementRecord) field.clone());
                    }
                }
                newSelectBuilder.fetchSupplements(customLookupFields);
//                newSelectBuilder.fetchSupplements(lookupFieldList);
            }
//            String query = newSelectBuilder.constructQueryString();
            if (reportType == ReportType.PIVOT_REPORT && globalContext.get(FacilioConstants.ContextNames.FILTER_CRITERIA) != null) {
                newSelectBuilder.andCriteria((Criteria) globalContext.get(FacilioConstants.ContextNames.FILTER_CRITERIA));
            }
            props = newSelectBuilder.getAsProps();
            pivotFieldsList.clear();

            isBaseModuleJoined = false;
        }
        LOGGER.debug("SELECT BUILDER --- " + newSelectBuilder);

        if (!FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getId() == 75l) {
            LOGGER.info("DATE FROM QUERY : " + props);
        }

//        return querySubmoduleRows(props);
        return props;
    }

    private boolean isAlreadyAdded(Set<FacilioModule> addedModules, FacilioModule module) {
        if (CollectionUtils.isEmpty(addedModules)) {
            return false;
        }

        for (FacilioModule m : addedModules) {
            if (m.isParentOrChildModule(module)) {
                return true;
            }
        }

        return false;
    }

    private void setYFieldsAndGroupByFields(List<ReportDataPointContext> dataPointList, List<FacilioField> fields, FacilioField xAggrField, StringJoiner groupBy, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
        for (ReportDataPointContext dataPoint : dataPointList) {
            applyYAggregation(dataPoint, fields);
        }

        if (dp.getGroupByFields() != null && !dp.getGroupByFields().isEmpty()) {
            for (ReportGroupByField groupByField : dp.getGroupByFields()) {
                if (groupByField.getField() == null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    groupByField.setField(groupByField.getModule(), modBean.getField(groupByField.getFieldId()));
                }
                FacilioField gField;
                if (reportType == ReportType.PIVOT_REPORT) {
                    if (groupByField.getLookupFieldId() > 0) {
                        gField = modBean.getField(groupByField.getLookupFieldId()).clone();
                        handlePivotFields(groupByField.getField(), (LookupField) gField, false, groupByField.getModule(), false);
                    } else {
                        gField = groupByField.getField().clone();
                        handlePivotFields(groupByField.getField(), null, false, groupByField.getModule(), false);
                    }
                } else {
                    gField = groupByField.getField().clone();
                }
                if (groupByField.getAggrEnum() != null) {
                    if (groupByField.getAggrEnum() instanceof SpaceAggregateOperator && !groupByField.getAggrEnum().getStringValue().equalsIgnoreCase(baseModule.getName())) {
                        gField = applySpaceAggregation(dp, groupByField.getAggrEnum(), selectBuilder, addedModules, groupByField.getField());
                    } else {
                        gField = groupByField.getAggrEnum().getSelectField(gField);
                    }
                }
                if (gField.getModule() != null && (gField.getModule().isCustom() && !baseModule.equals(gField.getModule()))) {
                    gField.setTableAlias(getAndSetModuleAlias(gField.getModule().getName()));
                } else if (gField.getModule() != null && groupByField.getAlias() != null && reportType == ReportType.PIVOT_REPORT) {
                    gField.setTableAlias(getAndSetModuleAlias(gField.getModule().getName()));
                }
                fields.add(gField);
                if (reportType == ReportType.PIVOT_REPORT && gField.getModule() != null && gField.getModule().equals(baseModule) && !gField.getCompleteColumnName().endsWith("null")) {
                    groupBy.add(gField.getCompleteColumnName());
                } else if (reportType != ReportType.PIVOT_REPORT) {
                    if(gField instanceof MultiEnumField )
                    {
                        if( ((MultiEnumField) gField).getRelModule().getName() != null) {
                            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                            FacilioModule relModule = modBean.getModule(((MultiEnumField) gField).getRelModule().getName());
                            addedModules.add(relModule);
                            groupBy.add(new StringBuilder().append(relModule.getTableName()).append('.').append("INDEX_ID").toString());
                            FacilioField idField = FieldFactory.getIdField(gField.getModule());
                            applyJoin(new StringBuilder(idField.getCompleteColumnName()).append("=").append(relModule.getTableName()).append('.').append("PARENT_ID").toString(), relModule, selectBuilder);
                        }
                    }
                    else if(gField instanceof MultiLookupField ){
                        if( ((MultiLookupField) gField).getRelModule().getName() != null) {
                            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                            FacilioModule relModule = modBean.getModule(((MultiLookupField) gField).getRelModule().getName());
                            addedModules.add(relModule);
                            groupBy.add(new StringBuilder().append(relModule.getTableName()).append('.').append("RIGHT_ID").toString());
                            FacilioField idField = FieldFactory.getIdField(gField.getModule());
                            applyJoin(new StringBuilder(idField.getCompleteColumnName()).append("=").append(relModule.getTableName()).append('.').append("LEFT_ID").toString(), relModule, selectBuilder);
                        }
                    }
                    else {
                        groupBy.add(gField.getCompleteColumnName());
                    }
                } else if(groupByField.getAggrEnum() != null && reportType == ReportType.PIVOT_REPORT) {
                    groupBy.add(gField.getCompleteColumnName());
                }
                handleJoin(groupByField, selectBuilder, addedModules);
            }
        }

        if (groupBy.length() > 0) {

            for (ReportDataPointContext dataPoint : dataPointList) {
                //handling min/max if group by is present
                if (dataPoint.getyAxis().isFetchMinMax()) {
                    FacilioField minField = NumberAggregateOperator.MIN.getSelectField(dataPoint.getyAxis().getField());
                    minField.setName(dataPoint.getyAxis().getFieldName() + "_min");
                    fields.add(minField);

                    FacilioField maxField = NumberAggregateOperator.MAX.getSelectField(dataPoint.getyAxis().getField());
                    maxField.setName(dataPoint.getyAxis().getFieldName() + "_max");
                    fields.add(maxField);
                }
            }

            selectBuilder.groupBy(groupBy.toString());
        }
    }

    private void applyDateCondition(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext baseLine) throws Exception {
        if (report.getDateOperatorEnum() != null && dp.getTypeEnum() != DataPointType.FIELD) {
            if (dp.getDateField() == null) {
                throw new IllegalArgumentException("Date Field for datapoint cannot be null when report has date filter");
            }
            if (report.getDateOperatorEnum().isValueNeeded() && (report.getDateValue() == null || report.getDateValue().isEmpty())) {
                throw new IllegalArgumentException("Date Filter value cannot be null for the Date Operator :  " + report.getDateOperatorEnum());
            }

            if (baseLine != null) {
                selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField().getField(), baseLine.getBaseLineRange().toString(), DateOperators.BETWEEN));
            } else {
                selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField().getField(), report.getDateRange().toString(), DateOperators.BETWEEN));
            }
        }
    }

    private List<List<ReportDataPointContext>> groupDataPoints(List<ReportDataPointContext> dataPoints, boolean handleBooleanField, ReportType reportType, AggregateOperator aggregateOperator, DateRange dateRange) throws Exception {
        if (dataPoints != null && !dataPoints.isEmpty()) {
            if ((!FacilioProperties.isProduction() ||
                    (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 173l))) {
                if ((reportType != null && reportType == ReportType.READING_REPORT)
                        && aggregateOperator instanceof DateAggregateOperator) {
                    // replace live reading data with aggregated data
                    Set<Long> fieldIds = dataPoints.stream().filter(point -> point.getxAxis().getField().getName().equalsIgnoreCase("ttime"))
                            .map(point -> point.getyAxis().getField().getFieldId()).collect(Collectors.toSet());
                    List<AggregationColumnMetaContext> aggregateFields = AggregationAPI.getAggregateFields(fieldIds, dateRange);
                    if (CollectionUtils.isNotEmpty(aggregateFields)) {
                        Map<Long, List<AggregationColumnMetaContext>> columnMap = new HashMap<>();
                        for (AggregationColumnMetaContext aggregateField : aggregateFields) {
                            List<AggregationColumnMetaContext> list = columnMap.get(aggregateField.getFieldId());
                            if (list == null) {
                                list = new ArrayList<>();
                                columnMap.put(aggregateField.getFieldId(), list);
                            }
                            list.add(aggregateField);
                        }
                        for (ReportDataPointContext dataPoint : dataPoints) {
                            ReportFieldContext xAxis = dataPoint.getxAxis();
                            if (xAxis.getField().getName().equalsIgnoreCase("ttime")) {
                                ReportYAxisContext yAxis = dataPoint.getyAxis();
                                if (columnMap.containsKey(yAxis.getFieldId())) {
                                    AggregationColumnMetaContext columnMetaContext = null;

                                    List<AggregationColumnMetaContext> aggregationColumnMetaContexts = columnMap.get(yAxis.getFieldId());
                                    int minVersion = Integer.MAX_VALUE;
                                    for (AggregationColumnMetaContext aggregationColumnMetaContext : aggregationColumnMetaContexts) {
                                        if (aggregationColumnMetaContext.getAggregateOperatorEnum() != yAxis.getAggrEnum()) {
                                            continue;
                                        }

                                        int allowed = aggregationColumnMetaContext.getAggregationMeta().getFrequencyTypeEnum().isAllowed((DateAggregateOperator) aggregateOperator);
                                        if (allowed >= 0) {
                                            if (allowed < minVersion) {
                                                minVersion = allowed;
                                                columnMetaContext = aggregationColumnMetaContext;
                                                if (allowed == 0) { // we cannot get less than this
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (columnMetaContext == null) {
                                        continue;
                                    }

                                    FacilioModule storageModule = columnMetaContext.getAggregationMeta().getStorageModule();
                                    yAxis.setField(storageModule, columnMetaContext.getStorageField());

                                    FacilioField aggregatedTimeField = modBean.getField("aggregatedTime", storageModule.getName());
                                    FacilioField parentIdField = modBean.getField("parentId", storageModule.getName());
                                    xAxis.setField(storageModule, aggregatedTimeField);

                                    ReportFieldContext aggregatedDateField = new ReportFieldContext();
                                    aggregatedDateField.setField(storageModule, aggregatedTimeField);
                                    dataPoint.setDateField(aggregatedDateField);

                                    Criteria allCriteria = dataPoint.getAllCriteria();
                                    if (allCriteria != null && !allCriteria.isEmpty()) {
                                        Map<String, Condition> conditions = allCriteria.getConditions();
                                        for (Condition cond : conditions.values()) {
                                            if ("parentId".equals(cond.getFieldName())) {
                                                cond.setField(parentIdField);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            List<List<ReportDataPointContext>> groupedList = new ArrayList<>();
            for (ReportDataPointContext dataPoint : dataPoints) {
                if (dataPoint.getTypeEnum() == null) {
                    dataPoint.setType(DataPointType.MODULE.getValue());
                }
                switch (dataPoint.getTypeEnum()) {
                    case MODULE:
                    case FIELD:
                    case ADDITIONAL:
                        if (handleBooleanField) {
                            handleBooleanField(dataPoint);
                        }
                        addToMatchedList(dataPoint, groupedList);
                        break;
                    case DERIVATION:
                        break;
                }
            }
            return groupedList;
        }
        return null;
    }

    private void handleBooleanField(ReportDataPointContext dataPoint) {
        if ((dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)) {
            dataPoint.getyAxis().setAggr(null);
            dataPoint.setHandleEnum(true);
        }
    }

    private void addToMatchedList(ReportDataPointContext dataPoint, List<List<ReportDataPointContext>> groupedList) throws Exception {
        for (List<ReportDataPointContext> dataPointList : groupedList) {
            ReportDataPointContext rdp = dataPointList.get(0);
            if (rdp.getxAxis().getField().equals(dataPoint.getxAxis().getField()) &&                                    // xaxis should be same
                    rdp.getyAxis().getModule().equals(dataPoint.getyAxis().getModule()) &&                              // yaxis Module should be same
                    Objects.equals(rdp.getOrderBy(), dataPoint.getOrderBy()) &&                                        // Order BY should be same
                    rdp.isHandleEnum() == dataPoint.isHandleEnum() &&                                            // Both should be of same type
                    isSameTable(rdp, dataPoint)                                                    //Both should have same table alias - case of pivot table
            ) {
                OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? OrderByFunction.NONE : rdp.getOrderByFuncEnum();
                OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? OrderByFunction.NONE : dataPoint.getOrderByFuncEnum();
//                Map<String, Condition> rdpCondition = rdp.getOtherCriteria().getConditions();
//                Map<String, Condition> dataPointCondition = dataPoint.getOtherCriteria().getConditions();
//				int rdpAggr = rdp.getxAxis().getAggrEnum() == null && rdp.getyAxis().getAggrEnum() == null ? 0 : 1;
//				int dataPointAggr = dataPoint.getxAxis().getAggrEnum() == null && dataPoint.getyAxis().getAggrEnum() == null ? 0 : 1;
                if (rdpFunc == dataPointFunc &&                                                                        // order by function should be same
                        Objects.equals(rdp.getCriteria(), dataPoint.getCriteria()) &&                                    // criteria should be same
//						rdpAggr == dataPointAggr &&																		// x and y aggregation (either both null or both should be not null)
//						(rdpAggr == 0 || (rdp.getxAxis().getAggrEnum() == dataPoint.getxAxis().getAggrEnum())) &&		// x aggregation should be same;
                        Objects.equals(rdp.getGroupByFields(), dataPoint.getGroupByFields()) &&
                        rdp.getOtherCriteria() == null && dataPoint.getOtherCriteria() == null) {                            // group by field should be same;
                    dataPointList.add(dataPoint);
                    return;
                }
            }
        }
        List<ReportDataPointContext> dataPointList = new ArrayList<>();
        dataPointList.add(dataPoint);
        groupedList.add(dataPointList);
    }

//    private boolean compareCriteria(Criteria criteria1, Criteria criteria2){
//        Map<String, Condition> conditions1 = criteria1.getConditions();
//        Map<String, Condition> conditions2 = criteria2.getConditions();
//        return conditions1.get("1").equals(conditions2.get("1"));
//    }

    private FacilioField applySpaceAggregation(ReportDataPointContext dp, AggregateOperator aggr, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules, FacilioField field) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE));

        if (!isAlreadyAdded(addedModules, resourceModule)) {
            selectBuilder.addJoinModules(Collections.singletonList(resourceModule));
            selectBuilder.innerJoin(resourceModule.getTableName())
                    .on(resourceModule.getTableName() + ".ID = " + field.getCompleteColumnName());
            addedModules.add(resourceModule);
        }

        selectBuilder.addJoinModules(Collections.singletonList(baseSpaceModule));
        selectBuilder.innerJoin(baseSpaceModule.getTableName())
                .on(resourceModule.getTableName() + ".SPACE_ID = " + baseSpaceModule.getTableName() + ".ID");
        addedModules.add(baseSpaceModule);

        FacilioField spaceField = null;
        switch ((SpaceAggregateOperator) aggr) {
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
                selectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName() + ".SPACE_TYPE", "SPACE_TYPE", String.valueOf(SpaceType.valueOf(aggr.toString()).getIntVal()), NumberOperators.EQUALS));
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

    private String getJoinOnPivot(LookupField lookupField) {
        FacilioField idField = null;
        if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
            idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
        } else {
            idField = FieldFactory.getIdField(lookupField.getLookupModule());
        }
        if (idField.getModule() != null && (idField.getModule().isCustom() && !baseModule.equals(idField.getModule()))) {
            String alias = getAndSetModuleAlias(lookupField.getName());
            idField = idField.clone();
            idField.setTableAlias(alias);
        } else {
            String alias = getAndSetModuleAlias(lookupField.getLookupModule().getName());
            idField = idField.clone();
            idField.setTableAlias(alias);
        }

        return lookupField.getCompleteColumnName() + " = " + idField.getCompleteColumnName();
    }

    private String getJoinOn(LookupField lookupField) {
        FacilioField idField = null;
        if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
            idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
        } else {
            idField = FieldFactory.getIdField(lookupField.getLookupModule());
        }
        if (idField.getModule() != null && (idField.getModule().isCustom() && !baseModule.equals(idField.getModule()))) {
            String alias = getAndSetModuleAlias(idField.getModule().getName());
            idField = idField.clone();
            idField.setTableAlias(alias);
        }
        return lookupField.getCompleteColumnName() + " = " + idField.getCompleteColumnName();
    }

    private String getJoinOn(FacilioField facilioField, String idFieldAlias) throws Exception {
        FacilioField idField = null;
        if (facilioField instanceof LookupField) {
            LookupField lookupField = (LookupField) facilioField;
            return getJoinOnPivot(lookupField);
        } else {
            idField = FieldFactory.getIdField(this.baseModule);
            FacilioField submoduleLookupField = null;
            List<FacilioField> submoduleFields = modBean.getAllFields(facilioField.getModule().getName());
            for (FacilioField field : submoduleFields) {
                if (this.baseModule.getName().equals(field.getName())) {
                    submoduleLookupField = field.clone();
                }
            }

            if (idFieldAlias != null) {
                String alias = getAndSetModuleAlias(idField.getModule().getName());
                idField = idField.clone();
                idField.setTableAlias(alias);
            }
            if (submoduleLookupField != null) {
                submoduleLookupField.setTableAlias(facilioField.getTableAlias());
            } else {
                submoduleLookupField = facilioField;
            }
            return submoduleLookupField.getCompleteColumnName() + " = " + idField.getCompleteColumnName();
        }
    }

    private Map<String, ArrayList<LookupField>> getLookupFields(List<FacilioField> fields) {
        Map<String, ArrayList<LookupField>> lookupFields = new HashMap<>();
        ArrayList<LookupField> lookupFieldList = null;
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP) {
                LookupField lookupField = (LookupField) field;
                if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
                    if(lookupFields.containsKey(lookupField.getSpecialType()))
                    {
                        lookupFieldList = lookupFields.get(lookupField.getSpecialType());
                        lookupFieldList.add(lookupField);
                        lookupFields.put(lookupField.getSpecialType(), lookupFieldList);
                    }
                    else {
                        lookupFieldList = new ArrayList<LookupField>();
                        lookupFieldList.add(lookupField);
                        lookupFields.put(lookupField.getSpecialType(), lookupFieldList);
                    }
                } else {
                    if(lookupFields.containsKey(lookupField.getLookupModule().getName())){
                        lookupFieldList = lookupFields.get(lookupField.getLookupModule().getName());
                        lookupFieldList.add(lookupField);
                        lookupFields.put(lookupField.getLookupModule().getName(), lookupFieldList);
                    }
                    else {
                        lookupFieldList = new ArrayList<LookupField>();
                        lookupFieldList.add(lookupField);
                        lookupFields.put(lookupField.getLookupModule().getName(), lookupFieldList);
                    }
                }
            }
        }
        return lookupFields;
    }

    private void handleLookupJoin(Map<String, ArrayList<LookupField>> lookupFields, FacilioModule module, SelectRecordsBuilder builder, Set<FacilioModule> addedModules, Long lookupfieldId) throws Exception {
        Stack<FacilioModule> stack = null;
        FacilioModule prevModule = null;
        while (module != null) {
            if (lookupFields.containsKey(module.getName())) {
                LookupField lookupFieldClone = null;
                ArrayList<LookupField> lookupFieldsList = lookupFields.get(module.getName());
                for(LookupField lookupField : lookupFieldsList)
                {
                    if (lookupfieldId != null && lookupfieldId != -1 && lookupField.getFieldId() == lookupfieldId) {
                        lookupFieldClone = lookupField.clone();
                        break;
                    }
                    lookupFieldClone = lookupField.clone();
                }
                if (lookupfieldId != null && lookupfieldId != -1) {
                    if (lookupFieldClone.getFieldId() == lookupfieldId) {
                        String joinOn = getJoinOn(lookupFieldClone);

                        applyJoin(joinOn, module, builder);
                        prevModule = module;
                        break;
                    }

                } else {
                    String joinOn = getJoinOn(lookupFieldClone);

                    applyJoin(joinOn, module, builder);
                    prevModule = module;
                    break;
                }
            }
            if (stack == null) {
                stack = new Stack<>();
            }
            stack.push(module);
            module = module.getExtendModule();
        }

        while (prevModule != null && CollectionUtils.isNotEmpty(stack)) {
            FacilioModule pop = stack.pop();
            builder.addJoinModules(Collections.singletonList(pop));
            builder.innerJoin(pop.getTableName())
                    .on(prevModule.getTableName() + ".ID = " + pop.getTableName() + ".ID");
            prevModule = pop;
        }

        if (prevModule != null) {
            addedModules.add(prevModule);
        }
    }

    private FacilioField applyXAggregation(ReportDataPointContext dp, AggregateOperator xAggr, StringJoiner groupBy, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, List<FacilioField> fields, Set<FacilioModule> addedModules) throws Exception {
        FacilioField xAggrField = null;
        if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0) {
            if (xAggr != null) {
                if (xAggr instanceof SpaceAggregateOperator && !xAggr.getStringValue().equalsIgnoreCase(baseModule.getName())) {
                    xAggrField = applySpaceAggregation(dp, xAggr, selectBuilder, addedModules, dp.getxAxis().getField()).clone();
                } else {
                    xAggrField = xAggr.getSelectField(dp.getxAxis().getField()).clone();
                }
            } else {
                xAggrField = dp.getxAxis().getField();
            }
            if (xAggrField.getModule() != null && (xAggrField.getModule().isCustom() && !baseModule.equals(xAggrField.getModule()))) {
                xAggrField.setTableAlias(getAndSetModuleAlias(xAggrField.getModule().getName()));
                groupBy.add(xAggrField.getCompleteColumnName());
            } else if (xAggrField.getModule() != null && dp.getxAxis().getAlias() != null && dp.getxAxis().getLookupFieldId() > 0 && reportType == ReportType.PIVOT_REPORT) {
                xAggrField.setTableAlias(getAndSetModuleAlias(xAggrField.getModule().getName() + "_" + dp.getxAxis().getField().getName()));
                groupBy.add(xAggrField.getCompleteColumnName());
            } else if (xAggrField.getModule() != null && dp.getxAxis().getAlias() != null && reportType == ReportType.PIVOT_REPORT) {
                xAggrField.setTableAlias(getAndSetModuleAlias(xAggrField.getModule().getName()));
                groupBy.add(xAggrField.getCompleteColumnName());
            }
            else if( xAggrField instanceof MultiEnumField && ((MultiEnumField) xAggrField).getRelModule() != null && ((MultiEnumField) xAggrField).getRelModule().getName() != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule relModule = modBean.getModule(((MultiEnumField) xAggrField).getRelModule().getName());
                addedModules.add(relModule);
                groupBy.add(new StringBuilder().append(relModule.getTableName()).append('.').append("INDEX_ID").toString());
                FacilioField idField = FieldFactory.getIdField(xAggrField.getModule());
                applyJoin(new StringBuilder(idField.getCompleteColumnName()).append("=").append(relModule.getTableName()).append('.').append("PARENT_ID").toString(), relModule, selectBuilder);
            }
            else if( xAggrField instanceof MultiLookupField && ((MultiLookupField) xAggrField).getRelModule() != null && ((MultiLookupField) xAggrField).getRelModule().getName() != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule relModule = modBean.getModule(((MultiLookupField) xAggrField).getRelModule().getName());
                addedModules.add(relModule);
                groupBy.add(new StringBuilder().append(relModule.getTableName()).append('.').append("RIGHT_ID").toString());
                FacilioField idField = FieldFactory.getIdField(xAggrField.getModule());
                applyJoin(new StringBuilder(idField.getCompleteColumnName()).append("=").append(relModule.getTableName()).append('.').append("LEFT_ID").toString(), relModule, selectBuilder);
            }

            if (xAggr instanceof DateAggregateOperator) {
                fields.add(((DateAggregateOperator) xAggr).getTimestampField(dp.getxAxis().getField()));
            } else {
                fields.add(xAggrField);
            }
        } else {
            if (xAggr == null || xAggr == CommonAggregateOperator.ACTUAL || dp.isHandleEnum()) { //Return x field as aggr field as there's no X aggregation
                xAggrField = dp.getxAxis().getField();
                fields.add(xAggrField);
            } else {
                throw new IllegalArgumentException("You can't apply X Aggr when Y Aggr is empty");
            }
        }
        handleJoin(dp.getxAxis(), selectBuilder, addedModules);
        if (reportType == ReportType.PIVOT_REPORT) {
            if (dp.getxAxis().getLookupFieldId() > 0) {
                LookupField lookupField = (LookupField) modBean.getField(dp.getxAxis().getLookupFieldId());
                handlePivotFields(dp.getxAxis().getField(), lookupField, false, dp.getxAxis().getModule(), true);
            } else {
                handlePivotFields(dp.getxAxis().getField(), null, false, dp.getxAxis().getModule(), false);
            }
        }
        return xAggrField;
    }

    private void applyReadingResourceJoin(ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, List<FacilioField> fields, Set<FacilioModule> addedModules) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule yAxisModule = dp.getyAxis().getModule();
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yAxisModule.getName()));

        if (!addedModules.contains(reportModule)) {
            if (reportModule != null && reportModule.getName().equalsIgnoreCase(FacilioConstants.ContextNames.ASSET)) {
                selectBuilder.addJoinModules(Collections.singletonList(reportModule));
                selectBuilder.innerJoin(reportModule.getTableName())
                        .on(resourceModule.getTableName() + ".ID = " + reportModule.getTableName() + ".ID");
                addedModules.add(reportModule);
            } else {
                selectBuilder.addJoinModules(Collections.singletonList(reportModule));
                selectBuilder.innerJoin(reportModule.getTableName())
                        .on(resourceModule.getTableName() + ".SPACE_ID = " + reportModule.getTableName() + ".ID");
                addedModules.add(reportModule);
            }
        }

        if (!isAlreadyAdded(addedModules, yAxisModule)) {
            selectBuilder.addJoinModules(Collections.singletonList(yAxisModule));
            selectBuilder.innerJoin(yAxisModule.getTableName())
                    .on(fieldMap.get("parentId").getCompleteColumnName() + " = " + resourceModule.getTableName() + ".ID");
            addedModules.add(yAxisModule);
        }
    }

    private void applygroupByTimeAggr(ReportDataPointContext dp, AggregateOperator groupByTimeAggr, StringJoiner groupBy) throws Exception {
        FacilioField groupByTimeField = null;
        if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0) {
            if (groupByTimeAggr != null && groupByTimeAggr.getValue() != 0) {
                groupByTimeField = groupByTimeAggr.getSelectField(dp.getxAxis().getField());
                groupBy.add(groupByTimeField.getCompleteColumnName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleJoin(ReportFieldContext reportField, SelectRecordsBuilder selectBuilder, Set<FacilioModule> addedModules) throws Exception {
        if (reportType == ReportType.PIVOT_REPORT) {
            return;
        }

        if (!reportField.getModule().equals(baseModule) && !isAlreadyAdded(addedModules, reportField.getModule()) && reportType != ReportType.PIVOT_REPORT) {        // inter-module support
            List<FacilioField> allFields = modBean.getAllFields(baseModule.getName()); // for now base module is enough
            Map<String, ArrayList<LookupField>> lookupFields = getLookupFields(allFields);
            handleLookupJoin(lookupFields, reportField.getModule(), selectBuilder, addedModules, reportField.getLookupFieldId());
        } else {
            joinModuleIfRequred(reportField, selectBuilder, addedModules);
        }
    }

    private boolean applyYAggregation(ReportDataPointContext dataPoint, List<FacilioField> fields) throws Exception {
        if (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == 0) {
            fields.add(dataPoint.getyAxis().getField());
            return false;
        } else {
            FacilioField facilioField = dataPoint.getyAxis().getField().clone();
            if(reportType == ReportType.PIVOT_REPORT && dataPoint.getyAxis().getModule().getTypeEnum() != ModuleType.READING) {
                facilioField.setTableAlias(getAndSetModuleAlias(facilioField.getModule().getName()));
            } else if(reportType == ReportType.PIVOT_REPORT && dataPoint.getyAxis().getModule().getTypeEnum() == ModuleType.READING) {
                facilioField.setTableAlias(null);
            }
            FacilioField aggrField = dataPoint.getyAxis().getAggrEnum().getSelectField(facilioField);
            aggrField.setName(ReportUtil.getAggrFieldName(aggrField, dataPoint.getyAxis().getAggrEnum()));
            fields.add(aggrField);
            if (reportType == ReportType.PIVOT_REPORT) {
                handlePivotFields(aggrField, null, true, dataPoint.getyAxis().getModule(), false);
            }
            return true;
        }
    }

    private void applyOrderByAndLimit(ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, AggregateOperator xAggr) {
        if (dataPoint.getOrderBy() != null && !dataPoint.getOrderBy().isEmpty()) {

            if (dataPoint.getOrderByFuncEnum() == null || dataPoint.getOrderByFuncEnum() == OrderByFunction.NONE) {
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
        } else if (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE && xAggr != CommonAggregateOperator.ACTUAL) {

            String orderBy = null;
            if (dataPoint.getyAxis().getAggr() < 1) {
                orderBy = dataPoint.getxAxis().getField().getCompleteColumnName();
            } else {
                orderBy = "MIN(" + dataPoint.getxAxis().getField().getCompleteColumnName() + ")";
            }
            selectBuilder.orderBy(orderBy);
        }
    }

    private void calculateBaseLineRange(ReportContext report) {
        if (report.getDateOperatorEnum() != null) {
            DateRange actualRange = null;
            if (report.getDateRange() == null) {
                actualRange = report.getDateOperatorEnum().getRange(report.getDateValue());
                report.setDateRange(actualRange);
            } else {
                actualRange = report.getDateRange();
            }

            originalDateRange = new DateRange(actualRange.getStartTime(), actualRange.getEndTime());
            if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
                for (ReportBaseLineContext baseLine : report.getBaseLines()) {

                    DateRange baseLineRange = baseLine.getBaseLine().calculateBaseLineRange(actualRange, baseLine.getAdjustTypeEnum());
                    long diff = actualRange.getStartTime() - baseLineRange.getStartTime();

                    baseLine.setBaseLineRange(baseLineRange);
                    baseLine.setDiff(diff);
                }
            }
        }
    }

    private boolean handleUserScope(ReportDataPointContext dataPoint) throws Exception {
        Collection<Long> parentIds = dataPoint.getMetaData() != null ? (Collection<Long>) dataPoint.getMetaData().get("parentIds") : null;
        if (parentIds != null && !(dataPoint.getModuleName() != null && dataPoint.getModuleName().equals("mvproject"))) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE);

            FacilioField idField = FieldFactory.getIdField(module);

            SelectRecordsBuilder<ResourceContext> builder = new SelectRecordsBuilder<ResourceContext>()
                    .select(Collections.singletonList(idField))
                    .module(module)
                    .beanClass(ResourceContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(parentIds, module));

            Criteria spaceCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ContextNames.ASSET);
            if (spaceCriteria != null) {
                Condition condition = new Condition();
                condition.setColumnName("SITE_ID");
                condition.setFieldName("siteId");
                condition.setOperator(NumberOperators.EQUALS);
                condition.setValue(StringUtils.join(parentIds, ","));

                spaceCriteria.addOrCondition(condition);
                builder.andCriteria(spaceCriteria);
            } else {
                return false;
            }

            List<Map<String, Object>> assetList = builder.getAsProps();
            return assetList.isEmpty();
        } else {
            return false;
        }
    }

     private boolean isTemplateDatapoint(ReportDataPointContext dataPoint, Long categoryId) throws Exception {
    	 Long dpCategoryId = dataPoint.getAssetCategoryId();
    	if (dpCategoryId != null && dpCategoryId > 0 && categoryId != null && categoryId > 0) {
    		if (dpCategoryId == categoryId) {
    			return true;
    		}
    	}
    	return false;
    }

    private boolean isSortPointIncluded(ReportDataPointContext dataPoint, ReportType type) {
        if (type == ReportType.PIVOT_REPORT) {
            if (dataPoint.isDefaultSortPoint()) {
                return false;
            }
        }
        return true;
    }

    private String getAndSetModuleAlias(String moduleName) {
        String alias = "";
        if (moduleVsAlias.containsKey(moduleName)) {
            return moduleVsAlias.get(moduleName);
        }

        if (moduleVsAlias.values().size() > 0) {
            alias = ReportUtil.getAlias((String) moduleVsAlias.values().toArray()[moduleVsAlias.values().size() - 1]);
        } else {
            alias = ReportUtil.getAlias("");
        }

        moduleVsAlias.put(moduleName, alias);

        return alias;
    }

    private void handleExtendedModuleJoin(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
        FacilioModule module = baseModule;
        if (isBaseModuleJoined) {
            return;
        }
        while (module != null) {
            String tableName = module.getTableName() + " " + getAndSetModuleAlias(module.getName());
            String joinOn = getAndSetModuleAlias(module.getName()) + ".ID = " + module.getTableName() + ".ID";
            selectBuilder.innerJoin(tableName)
                    .on(joinOn);
            module = module.getExtendModule();
        }
        isBaseModuleJoined = true;
    }

    public FacilioField getSubModuleField(String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == baseModule.getModuleId())).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fields)) {
            return fields.get(0);
        }
        return null;
    }

    private void handlePivotJoin(Map<String, Object> pivotField, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) throws Exception {
        handleExtendedModuleJoin(selectBuilder);
        FacilioModule module = (FacilioModule) pivotField.get("module");
        if ((Boolean) pivotField.get("isLookupField")) {
            List<FacilioModule> extendedModules = (List<FacilioModule>) pivotField.get("extendedModules");
            LookupField lookupFieldClone = (LookupField) pivotField.get("lookupField");
            FacilioField facilioField = (FacilioField) pivotField.get("field");

            boolean reverseModules = (boolean) pivotField.get("reverseFields");

            if (reverseModules) {
                Collections.reverse(extendedModules);
            }

            FacilioModule prevModule = extendedModules.get(extendedModules.size() - 1);

            String currentModuleAlias;
            String prevModuleAlias;
            for (FacilioModule poll : extendedModules) {
                if (poll != null && poll.equals(lookupFieldClone.getLookupModule())) {
                    currentModuleAlias = getAndSetModuleAlias(poll.getName() + "_" + facilioField.getName());
                    String LookupJoinOn = lookupFieldClone.getCompleteColumnName() + " = " + currentModuleAlias + ".ID";
                    selectBuilder.leftJoin(lookupFieldClone.getLookupModule().getTableName() + " " + currentModuleAlias).on(LookupJoinOn);
                } else {
                    currentModuleAlias = getAndSetModuleAlias(poll.getName() + "_" + facilioField.getName());
                    prevModuleAlias = getAndSetModuleAlias(prevModule.getName() + "_" + facilioField.getName());
                    selectBuilder.leftJoin(poll.getTableName() + " " + currentModuleAlias)
                            .on(prevModuleAlias + ".ID = " + currentModuleAlias + ".ID");
                }
                prevModule = poll;
            }
        } else if((Boolean) pivotField.get("isDataField") && !this.baseModule.getExtendedModuleIds().contains(module.getModuleId()) && module.getTypeEnum() != ModuleType.READING) {
            FacilioField dataField = FieldFactory.getIdField(module).clone();

            dataField.setTableAlias(getAndSetModuleAlias(dataField.getModule().getName()));

            String submoduleJoinOn = getJoinOn(dataField, null);
            FacilioModule lookupFieldModule = dataField.getModule();

            Stack<FacilioModule> stack = new Stack<>();
            FacilioModule prevModule = null;
            FacilioModule subModule = module;

            while (subModule != null) {
                if (subModule.equals(lookupFieldModule)) {
                    prevModule = subModule;
                    String tableName = module.getTableName() + " " + getAndSetModuleAlias(module.getName());
                    selectBuilder.leftJoin(tableName).on(submoduleJoinOn);
                    break;
                }
                stack.push(subModule);
                subModule = subModule.getExtendModule();
            }

            while (prevModule != null && CollectionUtils.isNotEmpty(stack)) {
                FacilioModule pop = stack.pop();
                FacilioField subModuleField = getSubModuleField(pop.getName());
                selectBuilder.innerJoin(pop.getTableName() + " " + getAndSetModuleAlias(pop.getName()))
                        .on(getAndSetModuleAlias(prevModule.getName()) + "."+ subModuleField.getColumnName() +" = " + getAndSetModuleAlias(pop.getName()) + ".ID");
                prevModule = pop;
            }
        }

        if(module.getTypeEnum() == ModuleType.READING){
            FacilioField facilioField = (FacilioField) pivotField.get("field");
            FacilioField moduleField = modBean.getField(facilioField.getFieldId());
            Condition moduleCondition = CriteriaAPI.getModuleIdIdCondition(moduleField.getModuleId(), moduleField.getModule());
            selectBuilder.andCondition(moduleCondition);
        }
    }

    private Boolean isSameTable(ReportDataPointContext rdp, ReportDataPointContext dp) {
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

    private void handlePivotFields(FacilioField field, LookupField lookupField, boolean isDataField, FacilioModule module, boolean firstRow) throws Exception {

        Map<String, Object> tempMap = new HashMap<>();
        field = field.clone();
        if (firstRow) {
            tempMap.put("lookupField", lookupField.clone());
            tempMap.put("lookupModule", lookupField.getModule());
            tempMap.put("isLookupField", true);
            field.setTableAlias(getAndSetModuleAlias(field.getModule().getName() + "_" + field.getName()));
        } else if (lookupField != null) {
            tempMap.put("lookupField", lookupField.clone());
            tempMap.put("lookupModule", lookupField.getModule());
            tempMap.put("isLookupField", true);
            field.setName(lookupField.getName() + "_" + module.getName() + "_" + field.getName());
            field.setTableAlias(getAndSetModuleAlias(field.getModule().getName() + "_" + field.getName()));
        } else {
            tempMap.put("isLookupField",false);
            if(field.getModule() != null && field.getModule().getTypeEnum() != ModuleType.READING) {
                field.setName(field.getName());
                field.setTableAlias(getAndSetModuleAlias(field.getModule().getName()));
            }
        }
        tempMap.put("field", field);

        List<FacilioModule> tempModulesList = new ArrayList<>();

        if (module != null) {
            FacilioModule lookupModule = module;
            while (lookupModule != null) {
                tempModulesList.add(lookupModule);
                lookupModule = lookupModule.getExtendModule();
            }
        }

        if (lookupField != null && !tempModulesList.contains(lookupField.getLookupModule())) {
            tempModulesList.clear();
            FacilioModule lookupModule = lookupField.getLookupModule();
            while (lookupModule != null) {
                tempModulesList.add(lookupModule);
                lookupModule = lookupModule.getExtendModule();
            }
            tempMap.put("reverseFields", false);
        }

        if (lookupField != null && field.getModule().isParentOrChildModule(baseModule)) {
            tempModulesList.clear();
            FacilioModule lookupModule = lookupField.getModule();
            while (lookupModule != null) {
                tempModulesList.add(lookupModule);
                lookupModule = lookupModule.getExtendModule();
            }
            tempMap.put("reverseFields", false);
        }

        if (lookupField != null && lookupField.getLookupModule().equals(field.getModule())) {
            tempModulesList.clear();
            tempModulesList.add(lookupField.getLookupModule());
        }

        if (field.getModule() != null && field.getModule().equals(tempModulesList.get(tempModulesList.size() - 1))) {
            tempMap.put("reverseFields", false);
        }

        if (!tempMap.containsKey("reverseFields")) {
            tempMap.put("reverseFields", true);
        }

        tempMap.put("isDataField", isDataField);
        tempMap.put("firstRow", firstRow);
        tempMap.put("module", module);
        tempMap.put("extendedModules", tempModulesList);
        pivotFieldsList.add(tempMap);
    }
}