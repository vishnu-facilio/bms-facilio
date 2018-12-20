package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.NumberAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.context.ReportFilterContext;
import com.facilio.report.context.ReportGroupByField;

public class FetchReportDataCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(FetchReportDataCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
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
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																				.module(dp.getxAxis().getField().getModule()) //Assuming X to be the base module
																				;
		Set<FacilioModule> addedModules = new HashSet<>();
		addedModules.add(dp.getxAxis().getField().getModule());
		
		joinYModuleIfRequred(dp, selectBuilder, addedModules);
//		applyOrderByAndLimit(dp, selectBuilder); // TODO handle orderBy and Limit
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
	
	private void joinYModuleIfRequred(ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
		if (!dp.getxAxis().getField().getModule().equals(dp.getyAxis().getField().getModule())) {
			applyJoin(dp.getyAxis().getJoinOn(), dp.getyAxis().getField().getModule(), selectBuilder);
			addedModules.add(dp.getyAxis().getField().getModule());
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
			if (dp.getCriteria() != null) {
				newSelectBuilder.andCriteria(dp.getCriteria());
			}
			
			boolean noMatch = applyFilters(report, dp, newSelectBuilder);
			if (noMatch) {
				return Collections.EMPTY_LIST;
			}
		}
		else {
			newSelectBuilder.andCondition(getEqualsCondition(xAggrField, xValues));
		}
		
		List<Map<String, Object>> props = newSelectBuilder.getAsProps();
		
		LOGGER.severe("SELECT BUILDER --- "+ newSelectBuilder);
//		LOGGER.info("DATE FROM QUERY : "+props);
		return props;
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
				fields.add(groupByField.getField());
				
				FacilioModule groupByModule = groupByField.getField().getModule();
				if (!addedModules.contains(groupByModule)) {
					
					applyJoin(groupByField.getJoinOn(), groupByModule, selectBuilder);
					addedModules.add(groupByModule);
				}
				groupBy.add(groupByField.getField().getCompleteColumnName());
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
				selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField(), baseLine.getBaseLineRange().toString(), DateOperators.BETWEEN));
			}
			else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(dp.getDateField(), report.getDateRange().toString(), DateOperators.BETWEEN));
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
				return CriteriaAPI.getCondition(field, value, EnumOperators.IS);
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
					rdp.getyAxis().getField().getModule().equals(dataPoint.getyAxis().getField().getModule()) &&		// yaxis Module should be same
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
	
	private FacilioField applySpaceAggregation(ReportDataPointContext dp, AggregateOperator xAggr, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE));
		
		selectBuilder.innerJoin(resourceModule.getTableName())
					.on(resourceModule.getTableName()+".ID = "+dp.getxAxis().getField().getCompleteColumnName())
					.innerJoin(baseSpaceModule.getTableName())
					.on(resourceModule.getTableName()+".SPACE_ID = "+baseSpaceModule.getTableName()+".ID");
		addedModules.add(resourceModule);
		addedModules.add(baseSpaceModule);
		
		FacilioField spaceField = null;
		switch ((SpaceAggregateOperator)xAggr) {
			case SITE:
				spaceField = fieldMap.get("siteId").clone();
				break;
			case BUILDING:
				spaceField = fieldMap.get("buildingId").clone();
				break;
			case FLOOR:
				spaceField = fieldMap.get("floorId").clone();
				break;
			default:
				throw new RuntimeException("Cannot be here!!");
		}
		spaceField.setName(dp.getxAxis().getFieldName());
		return spaceField;
	}
	
	private FacilioField applyXAggregation(ReportDataPointContext dp, AggregateOperator xAggr, StringJoiner groupBy, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, List<FacilioField> fields, Set<FacilioModule> addedModules) throws Exception {
		FacilioField xAggrField = null;
		if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0) {
			if (xAggr != null) {
				if (xAggr instanceof SpaceAggregateOperator) {
					xAggrField = applySpaceAggregation(dp, xAggr, selectBuilder, addedModules);
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
		return xAggrField;
	}
	
	private boolean applyYAggregation (ReportDataPointContext dataPoint, List<FacilioField> fields) throws Exception {
		if (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == 0) { 
			fields.add(dataPoint.getyAxis().getField());
			return false;
		}
		else {
			fields.add(dataPoint.getyAxis().getAggrEnum().getSelectField(dataPoint.getyAxis().getField()));
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
			selectBuilder.orderBy(dataPoint.getxAxis().getField().getCompleteColumnName());
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

