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
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
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
        if(report.getTypeEnum() != ReportType.PIVOT_REPORT) {
            addedModules.add(baseModule);
        }

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
        for (FacilioField field : fields) {
            if (field != null && field.getModule() != null && field.getModule().isCustom()) {
                String alias = getAndSetModuleAlias(field.getModule().getName());
                FacilioField cloneField = field.clone();
                if(report.getTypeEnum() == ReportType.PIVOT_REPORT) {
                    cloneField.setTableAlias(alias);
                }
                cloneFields.add(cloneField);
            } else if(field != null ){
                cloneFields.add(field.clone());
            }
        }
        globalFields = cloneFields;
        cloneFields.add(FieldFactory.getIdField(baseModule));
        selectBuilder.select(cloneFields);

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
                    newSelectBuilder.andCriteria(dp.getAllCriteria());
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
            if(report.getTypeEnum() == ReportType.PIVOT_REPORT) {
                List<SupplementRecord> customLookupFields = new ArrayList<>();
                for (FacilioField field : globalFields) {
                    if (field != null && field.getDataTypeEnum() == FieldType.MULTI_ENUM) {
                        customLookupFields.add((SupplementRecord) field.clone());
                    }
                }
                newSelectBuilder.fetchSupplements(customLookupFields);
            }
//            String query = newSelectBuilder.constructQueryString();
            props = newSelectBuilder.getAsProps();
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

                FacilioField gField = groupByField.getField().clone();

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
                if (gField.getModule().equals(baseModule) && !gField.getCompleteColumnName().endsWith("null")) {
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
                    rdp.getyAxis().getModule().equals(dataPoint.getyAxis().getModule()) &&        // yaxis Module should be same
                    Objects.equals(rdp.getOrderBy(), dataPoint.getOrderBy()) &&                                        // Order BY should be same
                    rdp.isHandleEnum() == dataPoint.isHandleEnum() &&                                            // Both should be of same type
                    isSameTable(rdp, dataPoint)                                                    //Both should have same table alias - case of pivot table
            ) {
                OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? OrderByFunction.NONE : rdp.getOrderByFuncEnum();
                OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? OrderByFunction.NONE : dataPoint.getOrderByFuncEnum();
//				int rdpAggr = rdp.getxAxis().getAggrEnum() == null && rdp.getyAxis().getAggrEnum() == null ? 0 : 1;
//				int dataPointAggr = dataPoint.getxAxis().getAggrEnum() == null && dataPoint.getyAxis().getAggrEnum() == null ? 0 : 1;
                if (rdpFunc == dataPointFunc &&                                                                        // order by function should be same
                        Objects.equals(rdp.getCriteria(), dataPoint.getCriteria()) &&                                    // criteria should be same
//						rdpAggr == dataPointAggr &&																		// x and y aggregation (either both null or both should be not null)
//						(rdpAggr == 0 || (rdp.getxAxis().getAggrEnum() == dataPoint.getxAxis().getAggrEnum())) &&		// x aggregation should be same;
                        Objects.equals(rdp.getGroupByFields(), dataPoint.getGroupByFields())) {                            // group by field should be same;
                    dataPointList.add(dataPoint);
                    return;
                }
            }
        }
        List<ReportDataPointContext> dataPointList = new ArrayList<>();
        dataPointList.add(dataPoint);
        groupedList.add(dataPointList);
    }

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


    private String getJoinOn(LookupField lookupField, String idFieldAlias) {
        FacilioField idField = null;
        if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
            idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
        } else {
            idField = FieldFactory.getIdField(lookupField.getLookupModule());
        }
        if (idFieldAlias != null) {
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
            if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
                idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
            } else {
                idField = FieldFactory.getIdField(facilioField.getModule());
            }
            if (idFieldAlias != null) {
                String alias = getAndSetModuleAlias(idField.getModule().getName());
                idField = idField.clone();
                idField.setTableAlias(alias);
            }
            return facilioField.getCompleteColumnName() + " = " + idField.getCompleteColumnName();
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
//        else {
//            idField = FieldFactory.getIdField(this.baseModule);
//            if (idFieldAlias != null) {
//                String alias = getAndSetModuleAlias(idField.getModule().getName(), idFieldAlias);
//                idField = idField.clone();
//                idField.setTableAlias(alias);
//            }
//            return facilioField.getCompleteColumnName() + " = " + idField.getCompleteColumnName();
//        }
    }

//	private String getDataJoinOn(FacilioField facilioField, String idFieldAlias){
//		if(facilioField instanceof LookupField){
//			return getJoinOn(facilioField,idFieldAlias);
//		}
//		FacilioField idField = FieldFactory.getIdField(facilioField.getModule());
//		if(idFieldAlias != null) {
//			String alias = getAndSetModuleAlias(idField.getModule().getName(),idFieldAlias);
//			idField = idField.clone();
//			idField.setTableAlias(alias);
//		}
//		return facilioField.getCompleteColumnName()+" = "+idField.getCompleteColumnName();
//	}

    private Map<String, LookupField> getLookupFields(List<FacilioField> fields) {
        Map<String, LookupField> lookupFields = new HashMap<>();
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP) {
                LookupField lookupField = (LookupField) field;
                if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
                    lookupFields.put(lookupField.getSpecialType(), lookupField);
                } else {
                    lookupFields.put(lookupField.getLookupModule().getName(), lookupField);
                }
            }
        }
        return lookupFields;
    }

    private void handleLookupJoin(Map<String, LookupField> lookupFields, FacilioModule module, SelectRecordsBuilder builder, Set<FacilioModule> addedModules, Long lookupfieldId) throws Exception {
        Stack<FacilioModule> stack = null;
        FacilioModule prevModule = null;
        while (module != null) {
            if (lookupFields.containsKey(module.getName())) {
                LookupField lookupFieldClone = lookupFields.get(module.getName()).clone();
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
            } else if (xAggrField.getModule() != null && dp.getxAxis().getAlias() != null && reportType == ReportType.PIVOT_REPORT) {
                xAggrField.setTableAlias(getAndSetModuleAlias(xAggrField.getModule().getName()));
            }
            groupBy.add(xAggrField.getCompleteColumnName());

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
        return xAggrField;
    }

    private void applyReadingResourceJoin(ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, List<FacilioField> fields, Set<FacilioModule> addedModules) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule yAxisModule = dp.getyAxis().getModule();
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yAxisModule.getName()));

        if (!addedModules.contains(reportModule)) {
            if (reportModule.getName().equalsIgnoreCase(FacilioConstants.ContextNames.ASSET)) {
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
        if (reportType == ReportType.PIVOT_REPORT && !isAlreadyAdded(addedModules, reportField.getModule())) {
            handlePivotJoin(reportField, selectBuilder, addedModules);
        } else if (!reportField.getModule().equals(baseModule) && !isAlreadyAdded(addedModules, reportField.getModule()) && reportType != ReportType.PIVOT_REPORT) {        // inter-module support
            List<FacilioField> allFields = modBean.getAllFields(baseModule.getName()); // for now base module is enough
            Map<String, LookupField> lookupFields = getLookupFields(allFields);
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
            FacilioField facilioField = dataPoint.getyAxis().getField();
            FacilioField aggrField = dataPoint.getyAxis().getAggrEnum().getSelectField(facilioField);
            aggrField.setName(ReportUtil.getAggrFieldName(aggrField, dataPoint.getyAxis().getAggrEnum()));
            fields.add(aggrField);
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

    private boolean isSortPointIncluded(ReportDataPointContext dataPoint, ReportType type) {
        if (type == ReportType.PIVOT_REPORT) {
            if (dataPoint.isDefaultSortPoint()) {
                return false;
            }
        }
        return true;
    }

    private String getAndSetModuleAlias(String moduleName){
        String alias = "";
        if(moduleVsAlias.containsKey(moduleName))
        {
            return moduleVsAlias.get(moduleName);
        }

        if (moduleVsAlias.values().size() > 0) {
            alias = ReportUtil.getAlias((String) moduleVsAlias.values().toArray()[moduleVsAlias.values().size() - 1]);
        } else {
            alias = ReportUtil.getAlias("");
        }

        moduleVsAlias.put(moduleName,alias);

        return alias;
    }

    private void handleExtendedModuleJoin(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
        FacilioModule module = baseModule;
        if(isBaseModuleJoined)
        {
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

    private void handlePivotJoin(ReportFieldContext reportField, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
        FacilioModule module;
        if (StringUtils.isNotEmpty(reportField.getModuleName())) {
            module = modBean.getModule(reportField.getModuleName());
        } else if (reportField.getField() != null && reportField.getField().getModule() != null) {
            module = reportField.getField().getModule();
        } else {
            module = reportField.getModule();
        }
        String tableAlias = reportField.getAlias();
        handleExtendedModuleJoin(selectBuilder);
        addedModules.add(module);
//        if (reportField.getSubModuleFieldId() > 0) {
//            FacilioField numberFieldClone = (FacilioField) modBean.getField(reportField.getSubModuleFieldId()).clone();
//            numberFieldClone.setTableAlias(getAndSetModuleAlias(numberFieldClone.getModule().getName(), tableAlias));
//            String SubmodulejoinOn = getJoinOn(numberFieldClone, tableAlias);
//            FacilioModule lookupFieldModule = numberFieldClone.getModule();
//
//            Stack<FacilioModule> stack = new Stack<>();
//            FacilioModule prevModule = null;
//            FacilioModule subModule = module;
//
//            while (subModule != null) {
//                if (subModule.equals(lookupFieldModule)) {
//                    prevModule = subModule;
//                    selectBuilder.leftJoin(module.getTableName() + " " + getAndSetModuleAlias(module.getName(), tableAlias)).on(SubmodulejoinOn);
//                    break;
//                }
//                stack.push(subModule);
//                subModule = subModule.getExtendModule();
//            }
//
//            while (prevModule != null && CollectionUtils.isNotEmpty(stack)) {
//                FacilioModule pop = stack.pop();
//                selectBuilder.innerJoin(pop.getTableName() + " " + getAndSetModuleAlias(pop.getName(), tableAlias))
//                        .on(getAndSetModuleAlias(prevModule.getName(), tableAlias) + ".ID = " + getAndSetModuleAlias(pop.getName(), tableAlias) + ".ID");
//                prevModule = pop;
//            }
//        } else
        if (reportField.getLookupFieldId() > 0) {

            LookupField lookupFieldClone = (LookupField) modBean.getField(reportField.getLookupFieldId()).clone();
            lookupFieldClone.setTableAlias(getAndSetModuleAlias(lookupFieldClone.getModule().getName()));
            String LookupjoinOn = getJoinOn(lookupFieldClone, tableAlias);
            FacilioModule lookupFieldModule = lookupFieldClone.getLookupModule();

            Queue<FacilioModule> queue = new LinkedList<>();
            FacilioModule prevModule = null;

            while (lookupFieldModule != null) {
                if (module.equals(lookupFieldModule)) {
                    prevModule = lookupFieldModule;
                    queue.add(lookupFieldModule);
                    break;
                }
                queue.add(lookupFieldModule);
                lookupFieldModule = lookupFieldModule.getExtendModule();
            }

            while (prevModule != null && CollectionUtils.isNotEmpty(queue)) {
                FacilioModule poll = queue.poll();
                if (poll.equals(lookupFieldClone.getLookupModule())) {
                    selectBuilder.leftJoin(lookupFieldClone.getLookupModule().getTableName() + " " + getAndSetModuleAlias(poll.getName())).on(LookupjoinOn);
                } else {
                    selectBuilder.innerJoin(poll.getTableName() + " " + getAndSetModuleAlias(poll.getName()))
                            .on(getAndSetModuleAlias(prevModule.getName()) + ".ID = " + getAndSetModuleAlias(poll.getName()) + ".ID");
                }
                prevModule = poll;
            }

        } else if (!this.baseModule.getName().equals(reportField.getModuleName()) && reportField.getAlias().contains("data_")) {

            String moduleName = reportField.getModuleName();
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            FacilioField dataField = null;
            for (FacilioField field : fields) {
                FacilioField cloneField = field.clone();
                if (cloneField.getName().equals(reportField.getField().getName())) {
                    dataField = cloneField;
                }
            }
            if (dataField == null) {
                dataField = FieldFactory.getIdField(reportField.getModule()).clone();
            }

            dataField.setTableAlias(getAndSetModuleAlias(dataField.getModule().getName()));
            String submoduleJoinOn = getJoinOn(dataField, tableAlias);
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
                selectBuilder.innerJoin(pop.getTableName() + " " + getAndSetModuleAlias(pop.getName()))
                        .on(getAndSetModuleAlias(prevModule.getName()) + ".ID = " + getAndSetModuleAlias(pop.getName()) + ".ID");
                prevModule = pop;
            }
        }
    }

    private List<Map<String, Object>> querySubmoduleRows(List<Map<String, Object>> props) throws Exception {
        List<ReportPivotTableRowsContext> rows = (List<ReportPivotTableRowsContext>) globalContext.get(FacilioConstants.Reports.ROWS);
        List<Map<String, Object>> fieldsList = new ArrayList<>();
        if (isSubmoduleDataFetched) {
            for (Map<String, Object> record : subModuleQueryResult) {
                for (String key : record.keySet()) {
                    for (Map<String, Object> prop : props) {
                        if (!prop.containsKey(key)) {
                            prop.put(key, record.get(key));
                        }
                    }
                }
            }
            return props;
        }
//      List<FacilioField> submoduleFields = new ArrayList<>();
        for (ReportPivotTableRowsContext row : rows) {
            FacilioField field = null;
            if (!baseModule.getName().equals(row.getModuleName())) {
                field = modBean.getField(row.getField().getId());
//                    submoduleFields.add(field);
                Map<String, Object> fieldsMap = buildSubmoduleQueryFields(props, field, rows);
                fieldsList.add(fieldsMap);
            }
        }

        for (Map<String, Object> submoduleMap : fieldsList) {
            List<FacilioField> fields = new ArrayList<>();
            FacilioField subField = (FacilioField) submoduleMap.get("field");
            FacilioField baseField = FieldFactory.getIdField(baseModule);
            ReportPivotTableRowsContext row = (ReportPivotTableRowsContext) submoduleMap.get("row");
            fields.add(subField);
            fields.add(baseField);
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().select(fields).table(subField.getTableName());
            if (row.getCriteria() != null) {
                selectRecordBuilder.andCriteria(row.getCriteria());
            }
            List<Long> conditionList = (List<Long>) submoduleMap.get("condition");

            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(baseField,conditionList,NumberOperators.EQUALS));

            getSubmoduleJoinOn(subField, selectRecordBuilder);
            if (row.getSortField() != null) {
                FacilioField orderByField = modBean.getField(row.getSortField().getId());
                selectRecordBuilder.orderBy(subField.getTableName() + "." + orderByField.getColumnName());
            }
//                if(row.getSortOrder() > 0){
//                    selectRecordBuilder.
//                }

            List<Map<String, Object>> result = selectRecordBuilder.get();
            for(Map<String,Object> record: props) {
                if (result != null) {
                    record.put(subField.getName(), result.get(0));
                }
            }

            System.out.println("query -> " + selectRecordBuilder);
        }

        isSubmoduleDataFetched = true;
        subModuleQueryResult = props;
        return props;
    }

    private Map<String, Object> buildSubmoduleQueryFields(List<Map<String, Object>> conditions, FacilioField field, List<ReportPivotTableRowsContext> rows) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Long> conditionList = new ArrayList<>();
        result.put("field", field);
        for (ReportPivotTableRowsContext row : rows) {
            if (!row.getModuleName().equals(baseModule.getName())) {
                result.put("row", row);
            }
        }
        for(Map<String,Object> record: conditions)
        {
            FacilioField baseModuleField = FieldFactory.getIdField(baseModule);
            conditionList.add((Long) record.get(baseModuleField.getName()));
        }
        result.put("condition", conditionList);
        return result;
    }

    private void getSubmoduleJoinOn(FacilioField submoduleField, GenericSelectRecordBuilder selectRecordBuilder) throws Exception {
        String moduleName = submoduleField.getModule().getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        FacilioField dataField = null;
        for (FacilioField field : fields) {
            if (field.getName().equals(baseModule.getName())) {
                dataField = field.clone();
            }
        }
        if (dataField == null) {
            dataField = FieldFactory.getIdField(submoduleField.getModule()).clone();
        }

        FacilioField idField = FieldFactory.getIdField(baseModule);
        selectRecordBuilder.rightJoin(baseModule.getTableName()).on(dataField.getTableName() + "." + dataField.getColumnName() + " = " + idField.getCompleteColumnName());
    }
//    private List<Map<String, Object>> querySubmoduleData(List<Map<String, Object>> props){
//        return props;
//    }

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
}

//