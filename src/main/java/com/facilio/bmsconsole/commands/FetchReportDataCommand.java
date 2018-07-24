package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.google.common.base.Objects;

public class FetchReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String xValues = (String) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
		
		if (report.getxCriteria() != null && xValues == null) {
			return false;
		}
		
		Condition dateFilter = (Condition) context.get(FacilioConstants.ContextNames.DATE_FILTER);
		List<List<ReportDataPointContext>> groupedDataPoints = groupDataPoints(report.getDataPoints());
		List<Pair<List<ReportDataPointContext>, List<Map<String, Object>>>> reportData = new ArrayList<>();
		for (List<ReportDataPointContext> dataPointList : groupedDataPoints) {
			ReportDataPointContext dp = dataPointList.get(0); //Since order by, criteria are same for all dataPoints in a group, we can consider only one for the builder
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																					.module(dp.getxAxisField().getModule()) //Assuming both x and y will be from same module in a datapoint
																					.andCriteria(dp.getCriteria())
																					;
			applyOrderBy(dp, selectBuilder);
			if (dateFilter != null) {
				selectBuilder.andCondition(dateFilter);
			}
			List<FacilioField> fields = new ArrayList<>();
			if (dp.getyAxisAggrEnum() != null) {
				FacilioField xAggrField;
				if (dp.getxAxisAggrEnum() != null) {
					xAggrField = dp.getxAxisAggrEnum().getSelectField(dp.getxAxisField());
				}
				else {
					xAggrField = dp.getxAxisField();
				}
				selectBuilder.groupBy(xAggrField.getColumnName());
				fields.add(xAggrField);
			}
			else {
				fields.add(dp.getxAxisField());
			}
			
			for (ReportDataPointContext dataPoint : dataPointList) {
				applyAggregation(dataPoint, fields);
			}
			selectBuilder.select(fields);
			selectBuilder.andCriteria(dp.getCriteria());
			
			if (report.getxCriteria() != null) {
				selectBuilder.andCondition(getEqualsCondition(dp.getxAxisField(), xValues));
			}
			
			reportData.add(Pair.of(dataPointList, selectBuilder.getAsProps()));
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
		return false;
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
			if (rdp.getxAxisField().equals(dataPoint.getxAxisField()) &&
					Objects.equal(rdp.getOrderBy(), dataPoint.getOrderBy())) {
				OrderByFunction rdpFunc = rdp.getOrderByFuncEnum() == null ? OrderByFunction.ACCENDING : rdp.getOrderByFuncEnum();
				OrderByFunction dataPointFunc = dataPoint.getOrderByFuncEnum() == null ? OrderByFunction.ACCENDING : dataPoint.getOrderByFuncEnum();
				int rdpAggr = rdp.getxAxisAggrEnum() == null && rdp.getyAxisAggrEnum() == null ? 0 : 1;
				int dataPointAggr = dataPoint.getxAxisAggrEnum() == null && dataPoint.getyAxisAggrEnum() == null ? 0 : 1;
				if (rdpFunc == dataPointFunc && 
						Objects.equal(rdp.getCriteria(), dataPoint.getCriteria()) && 
						rdpAggr == dataPointAggr &&
						(rdpAggr == 0 || (rdp.getxAxisAggrEnum() == dataPoint.getxAxisAggrEnum() && rdp.getxAxisField().equals(dataPoint.getxAxisField()) ))) {
					dataPointList.add(dataPoint);
					return;
				}
			}
		}
		List<ReportDataPointContext> dataPointList = new ArrayList<>();
		dataPointList.add(dataPoint);
		groupedList.add(dataPointList);
	}
	
	private void applyAggregation (ReportDataPointContext dataPoint, List<FacilioField> fields) throws Exception {
		if (dataPoint.getyAxisAggrEnum() == null) { 
			fields.add(dataPoint.getxAxisField());
		}
		else {
			FacilioField yAggrField;
			if (dataPoint.getyAxisAggrEnum() != null) {
				yAggrField = dataPoint.getyAxisAggrEnum().getSelectField(dataPoint.getyAxisField());
			}
			else {
				yAggrField = dataPoint.getyAxisField();
			}
			fields.add(yAggrField);
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
}
