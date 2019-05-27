package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.AggregateOperator.CommonAggregateOperator;
import com.facilio.modules.AggregateOperator.DateAggregateOperator;
import com.facilio.modules.AggregateOperator.NumberAggregateOperator;
import com.facilio.modules.AggregateOperator.SpaceAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

;

public class FetchReportDataCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(FetchReportDataCommand.class.getName());
	private FacilioModule baseModule;
	private ModuleBean modBean;
	
	@Override
	public boolean execute(Context context) throws Exception {
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
		List<ReportDataContext> reportData = new ArrayList<>();
		List<ReportDataPointContext> dataPoints = new ArrayList<>(report.getDataPoints());
		ReportDataPointContext sortPoint = getSortPoint(dataPoints);
		ReportDataContext sortedData = null;
		if (sortPoint != null) {
			sortedData = fetchDataForGroupedDPList(Collections.singletonList(sortPoint), report, false, null);
			reportData.add(sortedData);
		}
		List<List<ReportDataPointContext>> groupedDataPoints = groupDataPoints(dataPoints, handleBooleanFields);
		if (groupedDataPoints != null && !groupedDataPoints.isEmpty()) {
			for (List<ReportDataPointContext> dataPointList : groupedDataPoints) {
				ReportDataContext data = fetchDataForGroupedDPList(dataPointList, report, sortPoint != null, sortPoint == null ? null : sortedData.getxValues());
				reportData.add(data);
			}
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
				
				itr.remove();
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
		
//		joinModuleIfRequred(dp.getxAxis(), selectBuilder, addedModules);
		joinModuleIfRequred(dp.getyAxis(), selectBuilder, addedModules);
		applyOrderByAndLimit(dp, selectBuilder);
		List<FacilioField> fields = new ArrayList<>();
		StringJoiner groupBy = new StringJoiner(",");
		FacilioField xAggrField = applyXAggregation(dp, report.getxAggrEnum(), groupBy, selectBuilder, fields, addedModules);
		setYFieldsAndGroupByFields(dataPointList, fields, xAggrField, groupBy, dp, selectBuilder, addedModules);
		selectBuilder.select(fields);
		
		boolean noMatch = hasSortedDp && (xValues == null || xValues.isEmpty());
		Map<String, List<Map<String, Object>>> props = new HashMap<>();
		List<Map<String, Object>> dataProps = noMatch ? Collections.EMPTY_LIST : fetchReportData(report, dp, selectBuilder, null, xAggrField, xValues);
		props.put(FacilioConstants.Reports.ACTUAL_DATA, dataProps);
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
				FacilioField filterField = modBean.getField(filter.getFilterFieldName(), dataPoint.getxAxis().getModuleName());
				if (filter.isDataFilter()) {
					selectBuilder.andCondition(CriteriaAPI.getCondition(filterField, filter.getFilterValue(), filter.getFilterOperatorEnum()));
				}
				else {
					selectBuilder.andCondition(getEqualsCondition(filterField, filter.getFilterValue()));
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
		
		if (!baseModule.isParentOrChildModule(module)) {
			applyJoin(axis.getJoinOn(), module, selectBuilder);
			addedModules.add(module);
		}
	}
	
	private void applyJoin (String on, FacilioModule joinModule, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		selectBuilder.innerJoin(joinModule.getTableName())
									.on(on);

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
		applyDateCondition(report, dp, newSelectBuilder, reportBaseLine);
		
		
		if (AccountUtil.getCurrentOrg().getId() == 75 || AccountUtil.getCurrentOrg().getId() == 168) {
			LOGGER.info("Fetch Data X Values : "+xValues);
		}
		
		if (xValues == null) {
			if (dp.getAllCriteria() != null) {
				newSelectBuilder.andCriteria(dp.getAllCriteria());
			}
			
			boolean noMatch = applyFilters(report, dp, newSelectBuilder);
			if (noMatch) {
				return Collections.EMPTY_LIST;
			}
		}
		else {
			newSelectBuilder.andCondition(getEqualsCondition(xAggrField, xValues));
		}
		
		if (CollectionUtils.isNotEmpty(report.getUserFilters())) {
			for (ReportUserFilterContext userFilter: report.getUserFilters()) {
				Criteria criteria = userFilter.getCriteria();
				if (criteria != null) {
					newSelectBuilder.andCriteria(criteria);
				}
			}
		}
		
		if(report.getCriteria() != null) {
			newSelectBuilder.andCriteria(report.getCriteria());
		}
		
		List<Map<String, Object>> props = newSelectBuilder.getAsProps();
		
		 LOGGER.severe("SELECT BUILDER --- "+ newSelectBuilder);
//		LOGGER.info("DATE FROM QUERY : "+props);
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
				
				FacilioField gField = groupByField.getField();
				
				if (groupByField.getAggrEnum() != null) {
					if (groupByField.getAggrEnum() instanceof SpaceAggregateOperator) {
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
		if (report.getDateOperatorEnum() != null) {
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
	
	private Condition getEqualsCondition(FacilioField field, String value) {
		switch (field.getDataTypeEnum()) {
			case STRING:
				return CriteriaAPI.getCondition(field, value, StringOperators.IS);
			case BOOLEAN:
				return CriteriaAPI.getCondition(field, value, BooleanOperators.IS);
			case NUMBER:
			case DECIMAL:
			case DATE_TIME:
			case DATE:
				return CriteriaAPI.getCondition(field, value, NumberOperators.EQUALS);
			case LOOKUP:
				return CriteriaAPI.getCondition(field, value, PickListOperators.IS);
			case ENUM:
				return CriteriaAPI.getCondition(field, value, EnumOperators.VALUE_IS);
			default:
				return null;
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
				if (xAggr instanceof SpaceAggregateOperator) {
					xAggrField = applySpaceAggregation(dp, xAggr, selectBuilder, addedModules, dp.getxAxis().getField());
				}
				else {
					xAggrField = xAggr.getSelectField(dp.getxAxis().getField());
				}
			}
			else {
				xAggrField = dp.getxAxis().getField();
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
		if (!reportField.getModule().equals(baseModule)) {		// inter-module support
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
				actualRange = ((DateOperators) report.getDateOperatorEnum()).getRange(report.getDateValue());
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
}

