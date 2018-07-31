package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
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
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;

public class FetchReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String xValues = (String) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
		
		if (report.getxCriteria() != null && xValues == null) {
			return false;
		}
		
		calculateBaseLineRange(report);
		List<List<ReportDataPointContext>> groupedDataPoints = groupDataPoints(report.getDataPoints());
		List<ReportDataContext> reportData = new ArrayList<>();
		for (List<ReportDataPointContext> dataPointList : groupedDataPoints) {
			ReportDataContext data = new ReportDataContext();
			data.setDataPoints(dataPointList);
			
			ReportDataPointContext dp = dataPointList.get(0); //Since order by, criteria are same for all dataPoints in a group, we can consider only one for the builder
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																					.module(dp.getxAxis().getField().getModule()) //Assuming X to be the base module
																					.andCriteria(dp.getCriteria())
																					;
			applyJoin(dp, selectBuilder);
			applyOrderBy(dp, selectBuilder);
			List<FacilioField> fields = new ArrayList<>();
			StringJoiner groupBy = new StringJoiner(",");
			FacilioField xAggrField = applyXAggregation(dp, groupBy, fields);
			setYFieldsAndGroupByFields(dataPointList, fields, xAggrField, groupBy, dp, selectBuilder);
			selectBuilder.select(fields);
			if (report.getxCriteria() != null) {
				selectBuilder.andCondition(getEqualsCondition(dp.getxAxis().getField(), xValues));
			}
			
			Map<String, List<Map<String, Object>>> props = new HashMap<>();
			props.put("actual", fetchReportData(report, dp, selectBuilder, null));
			
			if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
				for (ReportBaseLineContext reportBaseLine : report.getBaseLines()) {
					props.put(reportBaseLine.getBaseLine().getName(), fetchReportData(report, dp, selectBuilder, reportBaseLine));
				}
			}
			
			data.setProps(props);
			reportData.add(data);
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
		return false;
	}
	
	private void applyJoin(ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		if (!dp.getxAxis().getField().getModule().equals(dp.getyAxis().getField().getModule())) {
			selectBuilder.innerJoin(dp.getyAxis().getField().getModule().getTableName())
							.on(dp.getyAxis().getJoinOn());
			
			FacilioModule prevModule = dp.getyAxis().getField().getModule();
			FacilioModule extendedModule = prevModule.getExtendModule();
			while(extendedModule != null) {
				selectBuilder.innerJoin(extendedModule.getTableName())
						.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
				prevModule = extendedModule;
				extendedModule = extendedModule.getExtendModule();
			}
		}
	}
	
	private List<Map<String, Object>> fetchReportData(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext reportBaseLine) throws Exception {
		SelectRecordsBuilder<ModuleBaseWithCustomFields> newSelectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>(selectBuilder);
		applyDateCondition(report, dp, newSelectBuilder, reportBaseLine);
		return newSelectBuilder.getAsProps();
	}
	
	private void setYFieldsAndGroupByFields(List<ReportDataPointContext> dataPointList, List<FacilioField> fields, FacilioField xAggrField, StringJoiner groupBy, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) throws Exception {
		for (ReportDataPointContext dataPoint : dataPointList) {
			boolean isAggr = applyYAggregation(dataPoint, fields);
			if (isAggr && groupBy.length() == 0) {
				groupBy.add(xAggrField.getColumnName());
			}
		}
		
		if (dp.getGroupByFields() != null && !dp.getGroupByFields().isEmpty()) {
			for (FacilioField field : dp.getGroupByFields()) {
				fields.add(field);
				groupBy.add(field.getCompleteColumnName());
			}
		}
		
		if (groupBy.length() > 0) {
			selectBuilder.groupBy(groupBy.toString());
		}
	}
	
	private void applyDateCondition(ReportContext report, ReportDataPointContext dp, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, ReportBaseLineContext baseLine) {
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
	
	private List<List<ReportDataPointContext>> groupDataPoints(List<ReportDataPointContext> dataPoints) {
		List<List<ReportDataPointContext>> groupedList = new ArrayList<>();
		for (ReportDataPointContext dataPoint : dataPoints) {
			addToMatchedList(dataPoint, groupedList);
		}
		return groupedList;
	}
	
	private void addToMatchedList (ReportDataPointContext dataPoint, List<List<ReportDataPointContext>> groupedList) {
		for (List<ReportDataPointContext> dataPointList : groupedList) {
			ReportDataPointContext rdp = dataPointList.get(0);
			if (rdp.getxAxis().getField().equals(dataPoint.getxAxis().getField()) &&
					rdp.getyAxis().getField().getModule().equals(dataPoint.getyAxis().getField().getModule()) &&
					Objects.equals(rdp.getOrderBy(), dataPoint.getOrderBy())) {
				OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? OrderByFunction.ACCENDING : rdp.getOrderByFuncEnum();
				OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? OrderByFunction.ACCENDING : dataPoint.getOrderByFuncEnum();
				int rdpAggr = rdp.getxAxis().getAggrEnum() == null && rdp.getyAxis().getAggrEnum() == null ? 0 : 1;
				int dataPointAggr = dataPoint.getxAxis().getAggrEnum() == null && dataPoint.getyAxis().getAggrEnum() == null ? 0 : 1;
				if (rdpFunc == dataPointFunc && 
						Objects.equals(rdp.getCriteria(), dataPoint.getCriteria()) && 
						rdpAggr == dataPointAggr &&
						(rdpAggr == 0 || (rdp.getxAxis().getAggrEnum() == dataPoint.getxAxis().getAggrEnum())) &&
						Objects.equals(rdp.getGroupByFields(), dataPoint.getGroupByFields())) {
					dataPointList.add(dataPoint);
					return;
				}
			}
		}
		List<ReportDataPointContext> dataPointList = new ArrayList<>();
		dataPointList.add(dataPoint);
		groupedList.add(dataPointList);
	}
	
	private FacilioField applyXAggregation(ReportDataPointContext dp, StringJoiner groupBy, List<FacilioField> fields) throws Exception {
		FacilioField xAggrField = null;
		if (dp.getyAxis().getAggrEnum() != null && dp.getyAxis().getAggr() != 0) {
			if (dp.getxAxis().getAggrEnum() != null&& dp.getxAxis().getAggr() != 0 ) {
				xAggrField = dp.getxAxis().getAggrEnum().getSelectField(dp.getxAxis().getField());
			}
			else {
				xAggrField = dp.getxAxis().getField();
			}
			groupBy.add(xAggrField.getColumnName());
			if (dp.getxAxis().getAggrEnum() instanceof DateAggregateOperator) {
				fields.add(((DateAggregateOperator)dp.getxAxis().getAggrEnum()).getTimestampField(dp.getxAxis().getField()));
			}
			else {
				fields.add(xAggrField);
			}
		}
		else {
			if (dp.getyAxis().getAggrEnum() == null || dp.getxAxis().getAggr() == 0) {
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

	private void applyOrderBy (ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
		if (dataPoint.getOrderBy() != null && dataPoint.getOrderBy().isEmpty()) {
			StringBuilder orderBy = new StringBuilder(dataPoint.getOrderBy());
			if (dataPoint.getOrderByFuncEnum() != null) {
				orderBy.append(" ")
						.append(dataPoint.getOrderByFuncEnum().getStringValue());
			}
			selectBuilder.orderBy(orderBy.toString());
		}
	}
	
	private void calculateBaseLineRange (ReportContext report) {
		if (report.getDateOperatorEnum() != null) {
			DateRange actualRange = ((DateOperators) report.getDateOperatorEnum()).getRange(report.getDateValue());
			report.setDateRange(actualRange);
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
