package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class FetchReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Map<String, List<Map<String, Object>>> reportData = new HashMap<>();
		
		for (ReportDataPointContext dataPoint : report.getDataPoints()) {
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																					.module(dataPoint.getxAxisField().getModule()) //Assuming both x and y will be from same module in a datapoint
																					.andCriteria(dataPoint.getCriteria())
																					;
			applyAggregation(dataPoint, selectBuilder);
			applyOrderBy(dataPoint, selectBuilder);
			reportData.put(dataPoint.getName(), selectBuilder.getAsProps());
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
		return false;
	}
	
	private void applyAggregation (ReportDataPointContext dataPoint, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		if (dataPoint.getxAxisAggrEnum() == null && dataPoint.getyAxisAggrEnum() == null) { 
			fields.add(dataPoint.getxAxisField());
			fields.add(dataPoint.getyAxisField());
		}
		else {
			FacilioField xAggrField, yAggrField;
			if (dataPoint.getxAxisAggrEnum() != null) {
				xAggrField = dataPoint.getxAxisAggrEnum().getSelectField(dataPoint.getxAxisField());
			}
			else {
				xAggrField = dataPoint.getxAxisField();
			}
			
			if (dataPoint.getyAxisAggrEnum() != null) {
				yAggrField = dataPoint.getyAxisAggrEnum().getSelectField(dataPoint.getyAxisField());
			}
			else {
				yAggrField = dataPoint.getyAxisField();
			}
			
			fields.add(xAggrField);
			fields.add(yAggrField);
			selectBuilder.groupBy(xAggrField.getColumnName());
		}
		selectBuilder.select(fields);
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
