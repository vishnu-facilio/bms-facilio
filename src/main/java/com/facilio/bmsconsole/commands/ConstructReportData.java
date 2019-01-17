package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
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
		ReportContext reportContext = new ReportContext();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		reportContext.setType(ReportType.READING_REPORT);
		reportContext.setxAggr(0);
		reportContext.setxAlias("X");
		
		JSONObject xAxisJSON = (JSONObject) context.get("x-axis");
		
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		ReportFieldContext xAxis = new ReportFieldContext();
		FacilioField xField = modBean.getField((Long) xAxisJSON.get("field_id"));
		if (xAxisJSON.containsKey("aggr") && (xField.getDataType() == FieldType.DATE.getTypeAsInt() || xField.getDataType() == FieldType.DATE_TIME.getTypeAsInt())) {
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
		
		int yAggr = CommonAggregateOperator.COUNT.ordinal();
		FacilioField yField = null;
		if (yAxisJSON == null) {
			yField = FieldFactory.getIdField(xField.getModule());
			yField.setColumnName(yField.getModule().getTableName() + ".ID");
		} else {
			yAggr = ((Number) yAxisJSON.get("aggr")).intValue();
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
				FacilioField field = modBean.getField((long) groupByJSON.get("field_id"));
				groupByField.setField(field);
				groupByField.setAlias(field.getName());
				groupByFields.add(groupByField);
			}
			dataPointContext.setGroupByFields(groupByFields);
		}
		
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", "Y");
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		
		reportContext.addDataPoint(dataPointContext);
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}

}
