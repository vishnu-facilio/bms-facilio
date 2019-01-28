package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.ReportYAxisContext;

public class ConstructReportData implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportContext == null) {
			reportContext = new ReportContext();
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		reportContext.setType(ReportType.READING_REPORT);
		reportContext.setxAggr(0);
		reportContext.setxAlias("X");
		
		JSONObject xAxisJSON = (JSONObject) context.get("x-axis");
		
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		ReportFieldContext xAxis = new ReportFieldContext();
		
		if (!xAxisJSON.containsKey("field_id")) {
			throw new Exception("field_id should be mandatory");
		}
		
		FacilioField xField = modBean.getField((Long) xAxisJSON.get("field_id"));
		if (xAxisJSON.containsKey("aggr") && isDateField(xField)) {
			Integer xAggr = ((Number) xAxisJSON.get("aggr")).intValue();
			AggregateOperator aggregateOperator = AggregateOperator.getAggregateOperator(xAggr);
			if (aggregateOperator instanceof DateAggregateOperator) {
				reportContext.setxAggr(aggregateOperator);
			}
		}
		xAxis.setField(xField);
		dataPointContext.setxAxis(xAxis);
		
		JSONObject yAxisJSON = (JSONObject) context.get("y-axis");
		ReportYAxisContext yAxis = new ReportYAxisContext();
		
		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		FacilioField yField = null;
		if (yAxisJSON == null || !(yAxisJSON.containsKey("field_id"))) {
			yField = FieldFactory.getIdField(xField.getModule());
//			yField.setColumnName(yField.getModule().getTableName() + ".ID");
		} else {
			if (yAxisJSON.containsKey("aggr")) {
				yAggr = AggregateOperator.getAggregateOperator(((Number) yAxisJSON.get("aggr")).intValue());
			}
			yField = modBean.getField((Long) yAxisJSON.get("field_id"));
		}
		yAxis.setAggr(yAggr);
		yAxis.setField(yField);
		dataPointContext.setyAxis(yAxis);
		
		JSONArray groupByJSONArray = (JSONArray) context.get("group-by");
		if (groupByJSONArray != null) {
			List<ReportGroupByField> groupByFields = new ArrayList<>();
			for (int i = 0; i < groupByJSONArray.size(); i++) {
				Map groupByJSON = (Map) groupByJSONArray.get(i);
				ReportGroupByField groupByField = new ReportGroupByField();
				
				if (!groupByJSON.containsKey("field_id")) {
					throw new Exception("Field ID should be mandatory");
				}
				
				FacilioField field = modBean.getField((long) groupByJSON.get("field_id"));
				groupByField.setField(field);
				groupByField.setAlias(field.getName());
				groupByFields.add(groupByField);
			}
			dataPointContext.setGroupByFields(groupByFields);
		}
		
		if (context.containsKey("criteria")) {
			Criteria criteria = (Criteria) context.get("criteria");
			dataPointContext.setCriteria(criteria);
		}
		
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", "Y");
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		
		reportContext.addDataPoint(dataPointContext);
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}

	private boolean isDateField(FacilioField field) {
		return (field.getDataType() == FieldType.DATE.getTypeAsInt() || field.getDataType() == FieldType.DATE_TIME.getTypeAsInt());
	}

}
