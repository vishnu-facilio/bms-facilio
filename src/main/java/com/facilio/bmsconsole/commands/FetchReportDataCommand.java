package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.logging.Logger;

import com.facilio.report.context.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.util.DemoHelperUtil;
import com.facilio.report.util.FilterUtil;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;

;

public class FetchReportDataCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(FetchReportDataCommand.class.getName());
	private FacilioModule baseModule;
	private ModuleBean modBean;
	private Boolean enableFutureData = false;
	private LinkedHashMap<String, String> moduleVsAlias = new LinkedHashMap<String, String>();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if (report.getDataPoints() == null || report.getDataPoints().isEmpty()) {
			return false;
		}
		
		Boolean handleBooleanFields = (Boolean) context.get(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN);
		if (handleBooleanFields == null) {
			handleBooleanFields = false;
		}
		
		calculateBaseLineRange(report);
		
		JSONObject dateField = (JSONObject) context.get("date-field");

		if(report!=null && dateField!=null && (Boolean)dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) != null) {
			JSONObject reportState;
			if(report.getReportState() != null && !report.getReportState().isEmpty()) {
				reportState = report.getReportState();
			}
			else {
				reportState = new JSONObject();
			}
			reportState.put(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA, (Boolean)dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA));
			report.setReportState(reportState);
		}
		
		
		
		if(report!=null && report.getReportState() != null && !report.getReportState().isEmpty()) {
			if(report.getReportState().containsKey(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA)
				&& (Boolean) report.getReportState().get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) == true) {
				enableFutureData = true;
			}
		}	

		List<ReportDataContext> reportData = new ArrayList<>();
		List<ReportDataPointContext> dataPoints = new ArrayList<>(report.getDataPoints());
		if(report.getReportTemplate() != null ) {
			Long reportParentId= report.getReportTemplate().getParentId();
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
			}
			else {
				if(reportParentId != null) {
				Map<String, Object> metaData = new HashMap<>();
				List<Long> parentIds = new ArrayList<>();
				parentIds.add(reportParentId);
				metaData.put("parentIds",parentIds);
				for(ReportDataPointContext dataPoint : dataPoints) {
					dataPoint.setMetaData(metaData);
					dataPoint.setName(dataPoint.getyAxis().getLabel());
				}
			}
		}
		}
		ReportDataPointContext sortPoint = getSortPoint(dataPoints);
		ReportDataContext sortedData = null;
		if (sortPoint != null) {
			sortedData = fetchDataForGroupedDPList(Collections.singletonList(sortPoint), report, false, null);
			reportData.add(sortedData);
		}
		List<List<ReportDataPointContext>> groupedDataPoints = groupDataPoints(dataPoints, handleBooleanFields);
		if (groupedDataPoints != null && !groupedDataPoints.isEmpty()) {
			for (int i = 0; i < groupedDataPoints.size(); i++) {
				List<ReportDataPointContext> dataPointList = groupedDataPoints.get(i);
				if(ReportContext.ReportType.READING_REPORT.getValue() == report.getType() && handleUserScope(dataPointList.get(0))){
					dataPoints.remove(dataPointList.get(0));
					report.setHasEdit(false);
				}else{
					ReportDataContext data = fetchDataForGroupedDPList(dataPointList, report, sortPoint != null, sortPoint == null ? null : sortedData.getxValues());
					reportData.add(data);
				}
			}
			if(dataPoints.isEmpty()){dataPoints.add(groupedDataPoints.get(0).get(0));}
			report.setDataPoints(dataPoints);
		}
		
		if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
			LOGGER.info("Report Data : "+reportData);
		}
		
		context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
		return false;
	}
	
	private ReportDataPointContext getSortPoint (List<ReportDataPointContext> dataPoints) {
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
	
	private ReportDataContext fetchDataForGroupedDPList (List<ReportDataPointContext> dataPointList, ReportContext report, boolean hasSortedDp, String xValues) throws Exception {
		ReportDataContext data = new ReportDataContext();
		data.setDataPoints(dataPointList);
		
		ReportDataPointContext dp = dataPointList.get(0); //Since order by, criteria are same for all dataPoints in a group, we can consider only one for the builder

		if (report.getTypeEnum() == ReportType.WORKORDER_REPORT) {
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
																				.setAggregation()
																				;
		Set<FacilioModule> addedModules = new HashSet<>();
		addedModules.add(baseModule);

		FacilioField marked = modBean.getField("marked", baseModule.getName());
		if (marked != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(marked, "false", BooleanOperators.IS));
		}

		joinModuleIfRequred(dp.getyAxis(), selectBuilder, addedModules);
		applyOrderByAndLimit(dp, selectBuilder);
		List<FacilioField> fields = new ArrayList<>();
		StringJoiner groupBy = new StringJoiner(",");
		FacilioField xAggrField = applyXAggregation(dp, report.getxAggrEnum(), groupBy, selectBuilder, fields, addedModules);
		setYFieldsAndGroupByFields(dataPointList, fields, xAggrField, groupBy, dp, selectBuilder, addedModules);
		List<FacilioField> cloneFields = new ArrayList<>();
		for(FacilioField field : fields) {
			if(field.getModule()!=null && (field.getModule().isCustom() && !baseModule.equals(field.getModule()))) {
				String alias = getAndSetModuleAlias(field.getModule().getName());
				FacilioField cloneField = field.clone();
				cloneField.setTableAlias(alias);
				cloneFields.add(field);
			}else {
				cloneFields.add(field);
			}
		}
		selectBuilder.select(cloneFields);

		boolean noMatch = hasSortedDp && (xValues == null || xValues.isEmpty());
		Map<String, List<Map<String, Object>>> props = new HashMap<>();
		List<Map<String, Object>> dataProps = noMatch ? Collections.EMPTY_LIST : fetchReportData(report, dp, selectBuilder, null, xAggrField, xValues);
		props.put(FacilioConstants.Reports.ACTUAL_DATA, dataProps);
		if (AccountUtil.getCurrentOrg().getId() == 75) {
			LOGGER.info("Date Props : "+dataProps);
		}
		if (dp.getLimit() != -1 && xValues == null) {
			data.setxValues(getXValues(dataProps, dp.getxAxis().getFieldName()));
			if (data.getxValues() == null || data.getxValues().isEmpty()) {
				noMatch = true;
			}
		}

		if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
			LOGGER.info("X Values : "+xValues);
			LOGGER.info("Data X Values : "+data.getxValues());
		}

		if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
			for (ReportBaseLineContext reportBaseLine : report.getBaseLines()) {
				props.put(reportBaseLine.getBaseLine().getName(), noMatch ? Collections.EMPTY_LIST : fetchReportData(report, dp, selectBuilder, reportBaseLine, xAggrField, data.getxValues() == null ? xValues : data.getxValues()));
				data.addBaseLine(reportBaseLine.getBaseLine().getName(), reportBaseLine);
			}
		}

		data.setProps(props);
		return data;
	}

	private String getXValues (List<Map<String, Object>> props, String key) {
		if (props != null && !props.isEmpty()) {
			StringJoiner xValues = new StringJoiner(",");
			for (Map<String, Object> prop : props) {
				Object val = prop.get(key);
				if (val != null) {
					xValues.add(val.toString());
				}
			}
			return xValues.toString();
		}
		return null;
	}

	private boolean applyFilters (ReportContext report, ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) throws Exception {
		if (report.getFilters() != null && !report.getFilters().isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (ReportFilterContext filter : report.getFilters()) {
				if (!filter.isDataFilter() && (filter.getFilterValue() == null || filter.getFilterValue().isEmpty())) {
					return true;
				}
				if (dataPoint.getTypeEnum()!= DataPointType.FIELD) {
					FacilioField filterField = modBean.getField(filter.getFilterFieldName(), dataPoint.getxAxis().getModuleName());
					if (filter.isDataFilter()) {
						selectBuilder.andCondition(CriteriaAPI.getCondition(filterField, filter.getFilterValue(), filter.getFilterOperatorEnum()));
					}
					else {
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

	private void applyJoin (String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		if(joinModule!=null && (joinModule.isCustom() && !baseModule.equals(joinModule))) {
			selectBuilder.innerJoin(joinModule.getTableName())
			.alias(getAndSetModuleAlias(joinModule.getName()))
			.on(on);
		}else {
			selectBuilder.innerJoin(joinModule.getTableName())
			.on(on);
		}

		FacilioModule prevModule = joinModule;
		FacilioModule extendedModule = prevModule.getExtendModule();
		while(extendedModule != null) {
			selectBuilder.innerJoin(extendedModule.getTableName())
							.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
	}

	private List<Map<String, Object>> fetchReportData(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext reportBaseLine, FacilioField xAggrField, String xValues) throws Exception {
		SelectRecordsBuilder<ModuleBaseWithCustomFields> newSelectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(selectBuilder);
		if (FacilioProperties.isProduction() && (AccountUtil.getCurrentOrg().getOrgId() == 210 || AccountUtil.getCurrentOrg().getOrgId() == 321l) && !enableFutureData) {
			DateRange dateRange = report.getDateRange();
			if (dateRange != null) {
				Long endTine = DemoHelperUtil.getEndTime(dp.getxAxis().getModule(), dateRange);
				dateRange.setEndTime(endTine);
			}
		}
		applyDateCondition(report, dp, newSelectBuilder, reportBaseLine);


		if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
			LOGGER.info("Fetch Data X Values : "+xValues);
		}

		if(report.getReportTemplate() != null ) {
			System.out.println("catergory Filter" + report.getReportTemplate().getCategoryFillter());
				Long templateType = report.getReportTemplate().getTemplateType();
				Map<String, ReportTemplateCategoryFilterContext> template = report.getReportTemplate().getCategoryTemplate();
				if (templateType != null && templateType == 2) {
					if (template.get(dp.getAliases().get("actual")) != null) {
						Long parentId = template.get(dp.getAliases().get("actual")).getParentId();
						ReportTemplateCategoryFilterContext filter = template.get(dp.getAliases().get("actual"));
						report.getReportTemplate().setParentId(parentId);
						Criteria templateCriteria = report.getReportTemplate().getCriteria(report,dp);
						if (templateCriteria != null) {
							newSelectBuilder.andCriteria(filter.getCriteria(dp));
						}
					}

				}
				else {
					Criteria templateCriteria = report.getReportTemplate().getCriteria(report,dp);
					if(templateCriteria != null) {
					newSelectBuilder.andCriteria(templateCriteria);
					}
					
				}
		}
		else {
			if (xValues == null) {
				if (dp.getAllCriteria() != null) {
					newSelectBuilder.andCriteria(dp.getAllCriteria());
				}
			}
			else {
				newSelectBuilder.andCondition(CriteriaAPI.getEqualsCondition(xAggrField, xValues));
			}
			boolean noMatch = applyFilters(report, dp, newSelectBuilder);
			if (noMatch) {
				return Collections.EMPTY_LIST;
			}
		}

		if (CollectionUtils.isNotEmpty(report.getUserFilters())) {
			for (ReportUserFilterContext userFilter: report.getUserFilters()) {
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

		if(report.getCriteria() != null) {
			newSelectBuilder.andCriteria(report.getCriteria());
		}
		
		if(dp.getDpCriteria() != null) {
			newSelectBuilder.andCriteria(dp.getDpCriteria());
		}
		
		JSONObject timeFilter = report.getTimeFilterJSON();
		if(timeFilter != null && !timeFilter.isEmpty()) {
			Criteria timeFilterCriteria = FilterUtil.getTimeFilterCriteria(dp.getxAxis().getModuleName(), timeFilter);
			if(timeFilterCriteria != null && !timeFilterCriteria.isEmpty()){
				newSelectBuilder.andCriteria(timeFilterCriteria);
			}
		}
		JSONObject dataFilter = report.getDataFilterJSON();
		if(dataFilter != null && !dataFilter.isEmpty()) {
			FilterUtil.setDataFilterCriteria(dp.getxAxis().getModuleName(), dataFilter,  report.getDateRange(), newSelectBuilder);
		}

		List<Map<String, Object>> props;

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
			}
			else {
				idField = FieldFactory.getIdField(lookupModule);
			}
			FacilioField countField = new FacilioField();
			countField.setColumnName("COALESCE(inn." + aggrFieldName + ", 0)");
			countField.setName(aggrFieldName);

			idField.setName(outerJoinField.getName());

			List<FacilioField> selectFields =  new ArrayList<>();
			selectFields.add(idField);
			selectFields.add(countField);
			if(CollectionUtils.isNotEmpty(dp.getGroupByFields())) {
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
					.andCondition(CriteriaAPI.getCondition(idField.getCompleteColumnName(), idField.getName(), StringUtils.join(dp.getxAxis().getSelectValuesOnly(), ","), NumberOperators.EQUALS))
					;
			newSelect.addWhereValue(Arrays.asList(newSelectBuilder.paramValues()), 0);
			if (CollectionUtils.isNotEmpty(dp.getOrderBy()) && (dp.getOrderByFuncEnum() != null && dp.getOrderByFuncEnum() != OrderByFunction.NONE)) {
				newSelect.orderBy(countField.getCompleteColumnName() + " " + dp.getOrderByFuncEnum().getStringValue());
			}

			props = newSelect.get();
			LOGGER.severe("SELECT BUILDER --- "+ newSelect);
		}
		else {
			props = newSelectBuilder.getAsProps();
		}

		 LOGGER.severe("SELECT BUILDER --- "+ newSelectBuilder);


		 if(!FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getId() == 75l) {
			 LOGGER.info("DATE FROM QUERY : "+props);
		}
		return props;
	}

	private boolean isAlreadyAdded(Set<FacilioModule> addedModules, FacilioModule module) {
		if (CollectionUtils.isEmpty(addedModules)) {
			return true;
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

//			Commenting out the following because we have already handled this while applying X aggregation
//			if (isAggr && groupBy.length() == 0) { //If only Y Aggr exists and there's no X Aggr, data is grouped by x Field by default
//				groupBy.add(xAggrField.getColumnName());
//			}
		}

		if (dp.getGroupByFields() != null && !dp.getGroupByFields().isEmpty()) {
			for (ReportGroupByField groupByField : dp.getGroupByFields()) {
				if (groupByField.getField() == null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					groupByField.setField(groupByField.getModule(), modBean.getField(groupByField.getFieldId()));
				}

				FacilioField gField = groupByField.getField().clone();

				if (groupByField.getAggrEnum() != null) {
					if (groupByField.getAggrEnum() instanceof SpaceAggregateOperator  && !groupByField.getAggrEnum().getStringValue().equalsIgnoreCase(baseModule.getName())) {
						gField = applySpaceAggregation(dp, groupByField.getAggrEnum(), selectBuilder, addedModules, groupByField.getField());
					} else {
						gField = groupByField.getAggrEnum().getSelectField(gField);
					}
				}
				fields.add(gField);

//				FacilioModule groupByModule = groupByField.getModule();
//				if (!isAlreadyAdded(addedModules, groupByModule)) {
//					applyJoin(groupByField.getJoinOn(), groupByModule, selectBuilder);
//					addedModules.add(groupByModule);
//				}
				handleJoin(groupByField, selectBuilder, addedModules);
				if(gField.getModule() != null && (gField.getModule().isCustom() && !baseModule.equals(gField.getModule()))) {
					gField.setTableAlias(getAndSetModuleAlias(gField.getModule().getName()));
				}
				groupBy.add(gField.getCompleteColumnName());
			}
		}

		if (groupBy.length() > 0) {

			for (ReportDataPointContext dataPoint : dataPointList) {
				//handling min/max if group by is present
				if (dataPoint.getyAxis().isFetchMinMax()) {
					FacilioField minField = NumberAggregateOperator.MIN.getSelectField(dataPoint.getyAxis().getField());
					minField.setName(dataPoint.getyAxis().getFieldName()+"_min");
					fields.add(minField);

					FacilioField maxField = NumberAggregateOperator.MAX.getSelectField(dataPoint.getyAxis().getField());
					maxField.setName(dataPoint.getyAxis().getFieldName()+"_max");
					fields.add(maxField);
				}
			}

			selectBuilder.groupBy(groupBy.toString());
		}
	}

	private void applyDateCondition(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext baseLine) throws Exception {
		if (report.getDateOperatorEnum() != null && dp.getTypeEnum()!= DataPointType.FIELD) {
			if (dp.getDateField() == null) {
				throw new IllegalArgumentException("Date Field for datapoint cannot be null when report has date filter");
			}
			if (report.getDateOperatorEnum().isValueNeeded() && (report.getDateValue() == null || report.getDateValue().isEmpty())) {
				throw new IllegalArgumentException("Date Filter value cannot be null for the Date Operator :  "+report.getDateOperatorEnum());
			}

			if (baseLine != null) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField().getField(), baseLine.getBaseLineRange().toString(), DateOperators.BETWEEN));
			}
			else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField().getField(), report.getDateRange().toString(), DateOperators.BETWEEN));
			}
		}
	}

	private List<List<ReportDataPointContext>> groupDataPoints(List<ReportDataPointContext> dataPoints, boolean handleBooleanField) throws Exception {
		if (dataPoints != null && !dataPoints.isEmpty()) {
			List<List<ReportDataPointContext>> groupedList = new ArrayList<>();
			for (ReportDataPointContext dataPoint : dataPoints) {
				if(dataPoint.getTypeEnum() == null) {
					dataPoint.setType(DataPointType.MODULE.getValue());
				}
				switch (dataPoint.getTypeEnum()) {
					case MODULE:
					case FIELD:
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

	private void addToMatchedList (ReportDataPointContext dataPoint, List<List<ReportDataPointContext>> groupedList) throws Exception {
		for (List<ReportDataPointContext> dataPointList : groupedList) {
			ReportDataPointContext rdp = dataPointList.get(0);
			if (rdp.getxAxis().getField().equals(dataPoint.getxAxis().getField()) &&									// xaxis should be same
					rdp.getyAxis().getModule().equals(dataPoint.getyAxis().getModule()) &&		// yaxis Module should be same
					Objects.equals(rdp.getOrderBy(), dataPoint.getOrderBy()) &&										// Order BY should be same
					rdp.isHandleEnum() == dataPoint.isHandleEnum()											// Both should be of same type
				) {
				OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? OrderByFunction.NONE : rdp.getOrderByFuncEnum();
				OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? OrderByFunction.NONE : dataPoint.getOrderByFuncEnum();
//				int rdpAggr = rdp.getxAxis().getAggrEnum() == null && rdp.getyAxis().getAggrEnum() == null ? 0 : 1;
//				int dataPointAggr = dataPoint.getxAxis().getAggrEnum() == null && dataPoint.getyAxis().getAggrEnum() == null ? 0 : 1;
				if (rdpFunc == dataPointFunc && 																		// order by function should be same
						Objects.equals(rdp.getCriteria(), dataPoint.getCriteria()) && 									// criteria should be same
//						rdpAggr == dataPointAggr &&																		// x and y aggregation (either both null or both should be not null)
//						(rdpAggr == 0 || (rdp.getxAxis().getAggrEnum() == dataPoint.getxAxis().getAggrEnum())) &&		// x aggregation should be same;
						Objects.equals(rdp.getGroupByFields(), dataPoint.getGroupByFields())) {							// group by field should be same;
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
			selectBuilder.innerJoin(resourceModule.getTableName())
			.on(resourceModule.getTableName()+".ID = " + field.getCompleteColumnName());
			addedModules.add(resourceModule);
		}
		
		selectBuilder.innerJoin(baseSpaceModule.getTableName())
		.on(resourceModule.getTableName()+".SPACE_ID = "+baseSpaceModule.getTableName()+".ID");		
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
				selectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName()+".SPACE_TYPE", "SPACE_TYPE", String.valueOf(SpaceType.valueOf(aggr.toString()).getIntVal()), NumberOperators.EQUALS));
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

		selectBuilder.andCustomWhere(spaceField.getCompleteColumnName() + " in (" + builder.constructSelectStatement() + ")");
		spaceField.setName(field.getName());
		spaceField.setDataType(FieldType.NUMBER);
		return spaceField;
	}

	private String getJoinOn(LookupField lookupField) {
		FacilioField idField = null;
		if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
			idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
		}
		else {
			idField = FieldFactory.getIdField(lookupField.getLookupModule());
		}
		if(idField.getModule()!=null && (idField.getModule().isCustom() && !baseModule.equals(idField.getModule()))) {
			String alias = getAndSetModuleAlias(idField.getModule().getName());
			idField = idField.clone();
			idField.setTableAlias(alias);
		}
		return lookupField.getCompleteColumnName()+" = "+idField.getCompleteColumnName();
	}

	private Map<String, LookupField> getLookupFields(List<FacilioField> fields) {
		Map<String, LookupField> lookupFields = new HashMap<>();
		for (FacilioField field : fields) {
			if (field.getDataTypeEnum() == FieldType.LOOKUP) {
				LookupField lookupField = (LookupField) field;
				if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
					lookupFields.put(lookupField.getSpecialType(), lookupField);
				}
				else {
					lookupFields.put(lookupField.getLookupModule().getName(), lookupField);
				}
			}
		}
		return lookupFields;
	}

	private void handleLookupJoin(Map<String, LookupField> lookupFields, FacilioModule module, SelectRecordsBuilder builder, Set<FacilioModule> addedModules) {
		Stack<FacilioModule> stack = null;
		FacilioModule prevModule = null;
		while (module != null) {
			if (lookupFields.containsKey(module.getName())) {
				LookupField lookupFieldClone = lookupFields.get(module.getName()).clone();
				String joinOn = getJoinOn(lookupFieldClone);
				
				applyJoin(joinOn, module, builder);
				prevModule = module;
				break;
			}
			if (stack == null) {
				stack = new Stack<>();
			}
			stack.push(module);
			module = module.getExtendModule();
		}

		while (prevModule != null && CollectionUtils.isNotEmpty(stack)) {
			FacilioModule pop = stack.pop();
			builder.innerJoin(pop.getTableName())
			.on(prevModule.getTableName()+".ID = "+pop.getTableName()+".ID");
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
					xAggrField = applySpaceAggregation(dp, xAggr, selectBuilder, addedModules, dp.getxAxis().getField());
				}
				else {
					xAggrField = xAggr.getSelectField(dp.getxAxis().getField());
				}
			}
			else {
				xAggrField = dp.getxAxis().getField();
			}
			if(xAggrField.getModule()!=null && (xAggrField.getModule().isCustom() && !baseModule.equals(xAggrField.getModule()))) {
				xAggrField.setTableAlias(getAndSetModuleAlias(xAggrField.getModule().getName()));
			}
			groupBy.add(xAggrField.getCompleteColumnName());

			if (xAggr instanceof DateAggregateOperator) {
				fields.add(((DateAggregateOperator)xAggr).getTimestampField(dp.getxAxis().getField()));
			}
			else {
				fields.add(xAggrField);
			}
		}
		else {
			if (xAggr == null || xAggr == CommonAggregateOperator.ACTUAL || dp.isHandleEnum()) { //Return x field as aggr field as there's no X aggregation
				xAggrField = dp.getxAxis().getField();
				fields.add(xAggrField);
			}
			else {
				throw new IllegalArgumentException("You can't apply X Aggr when Y Aggr is empty");
			}
		}
		handleJoin(dp.getxAxis(), selectBuilder, addedModules);
		return xAggrField;
	}

	private void handleJoin(ReportFieldContext reportField, SelectRecordsBuilder selectBuilder, Set<FacilioModule> addedModules) throws Exception {
		if (!reportField.getModule().equals(baseModule) && !isAlreadyAdded(addedModules, reportField.getModule())) {		// inter-module support
			List<FacilioField> allFields = modBean.getAllFields(baseModule.getName()); // for now base module is enough
			Map<String, LookupField> lookupFields = getLookupFields(allFields);
			handleLookupJoin(lookupFields, reportField.getModule(), selectBuilder, addedModules);
		} else {
			joinModuleIfRequred(reportField, selectBuilder, addedModules);
		}
	}

	private boolean applyYAggregation (ReportDataPointContext dataPoint, List<FacilioField> fields) throws Exception {
		if (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == 0) {
			fields.add(dataPoint.getyAxis().getField());
			return false;
		}
		else {
			FacilioField aggrField = dataPoint.getyAxis().getAggrEnum().getSelectField(dataPoint.getyAxis().getField());
			aggrField.setName(ReportUtil.getAggrFieldName(aggrField, dataPoint.getyAxis().getAggrEnum()));
			fields.add(aggrField);
			return true;
		}
	}

	private void applyOrderByAndLimit (ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		if (dataPoint.getOrderBy() != null && !dataPoint.getOrderBy().isEmpty()) {

			if (dataPoint.getOrderByFuncEnum() == null || dataPoint.getOrderByFuncEnum() == OrderByFunction.NONE) {
				throw new IllegalArgumentException("Order By function cannot be empty when order by is not empty");
			}

			StringJoiner orderBy = new StringJoiner(",");
			for (String order : dataPoint.getOrderBy()) { //MySQL requires direction be specified for each column
				orderBy.add(order+" "+dataPoint.getOrderByFuncEnum().getStringValue());
			}
			selectBuilder.orderBy(orderBy.toString());

			if (dataPoint.getLimit() != -1) {
				selectBuilder.limit(dataPoint.getLimit());
			}
		}
		else if (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) {

			String orderBy = null;
			if(dataPoint.getyAxis().getAggr() < 1) {
				orderBy = dataPoint.getxAxis().getField().getCompleteColumnName();
			}
			else {
				orderBy = "MIN(" + dataPoint.getxAxis().getField().getCompleteColumnName() + ")";
			}
			selectBuilder.orderBy(orderBy);
		}
	}
	
	private void calculateBaseLineRange (ReportContext report) {
		if (report.getDateOperatorEnum() != null) {
			DateRange actualRange = null;
			if(report.getDateRange() == null) {
				actualRange = report.getDateOperatorEnum().getRange(report.getDateValue());
				report.setDateRange(actualRange);
			}
			else {
				actualRange = report.getDateRange();
			}
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
	
	private boolean handleUserScope(ReportDataPointContext dataPoint) throws Exception{
		Collection<Long> parentIds = dataPoint.getMetaData() != null ? (Collection<Long>) dataPoint.getMetaData().get("parentIds") : null;
		if(parentIds != null && !(dataPoint.getModuleName() != null && dataPoint.getModuleName().equals("mvproject"))){
			ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			
			FacilioField idField = FieldFactory.getIdField(module);
			
			SelectRecordsBuilder<ResourceContext> builder = new SelectRecordsBuilder<ResourceContext>()
					.select(Collections.singletonList(idField))
					.module(module)
					.beanClass(ResourceContext.class)
					.andCondition(CriteriaAPI.getIdCondition(parentIds, module));
			
			Criteria spaceCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ContextNames.ASSET);
			if(spaceCriteria != null) {
				Condition condition = new Condition();
				condition.setColumnName("SITE_ID");
				condition.setFieldName("siteId");
				condition.setOperator(NumberOperators.EQUALS);
				condition.setValue(StringUtils.join(parentIds, ","));
				
				spaceCriteria.addOrCondition(condition);
				builder.andCriteria(spaceCriteria);
			}else{
				return false;
			}
			
			List<Map<String, Object>> assetList = builder.getAsProps();
			return assetList.isEmpty();
		}else{
			return false;
		}
	}
	
	private String getAndSetModuleAlias(String moduleName){
		String alias = "";
		if(moduleVsAlias.containsKey(moduleName)) {
			alias = moduleVsAlias.get(moduleName);
		}else {
			if(moduleVsAlias.values().size() > 0) {
				alias = ReportUtil.getAlias((String) moduleVsAlias.values().toArray()[moduleVsAlias.values().size()-1]);
			}
			else {
				alias =  ReportUtil.getAlias("");
			}
			moduleVsAlias.put(moduleName, alias);
		}
		return alias; 
	}
}

//