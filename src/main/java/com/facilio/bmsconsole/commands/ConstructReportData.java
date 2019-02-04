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
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
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
		
		FacilioField xField = null;
		if (xAxisJSON.containsKey("field_id")) {
			xField = modBean.getField((Long) xAxisJSON.get("field_id"));
		}
		
		if (xAxisJSON.containsKey("aggr")) {
			Integer xAggr = ((Number) xAxisJSON.get("aggr")).intValue();
			AggregateOperator aggregateOperator = AggregateOperator.getAggregateOperator(xAggr);
			if (aggregateOperator instanceof DateAggregateOperator && isDateField(xField)) {
				reportContext.setxAggr(aggregateOperator);
				
				if (xAxisJSON.containsKey("operator") && xAxisJSON.containsKey("date_value")) {
					Integer operator = ((Number) xAxisJSON.get("operator")).intValue();
					reportContext.setDateOperator(operator);
					reportContext.setDateValue((String) xAxisJSON.get("date_value"));
					dataPointContext.setDateField(xField);
				}
				
			} else if (aggregateOperator instanceof SpaceAggregateOperator) {
				reportContext.setxAggr(aggregateOperator);
				if (xField instanceof LookupField) {
					// TODO check whether lookup module is resource module 
				} else {
					throw new Exception("x field should be resource field");
				}
			}
		}
		if (xField == null) {
			throw new Exception("field_id should be mandatory");
		}
		xAxis.setField(xField);
		dataPointContext.setxAxis(xAxis);
		
		JSONObject yAxisJSON = (JSONObject) context.get("y-axis");
		ReportYAxisContext yAxis = new ReportYAxisContext();
		
		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		FacilioField yField = null;
		if (yAxisJSON == null || !(yAxisJSON.containsKey("field_id"))) {
			yField = FieldFactory.getIdField(xField.getModule());
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
		
		if (context.containsKey("sort_fields")) {
			List<String> orderBy = new ArrayList<>();
			JSONArray orderByArray = (JSONArray) context.get("sort_fields");
			for (int i = 0; i < orderByArray.size(); i++) {
				Map object = (Map) orderByArray.get(i);
				FacilioField orderByField = modBean.getField((Long) object.get("field_id"));
				orderBy.add(orderByField.getCompleteColumnName());
			}
			dataPointContext.setOrderBy(orderBy);
			
			if (context.containsKey("sort_order")) {
				dataPointContext.setOrderByFunc(((Number) context.get("sort_order")).intValue());
			}
		}
		if (context.containsKey("limit")) {
			int limit = ((Number) context.get("limit")).intValue();
			dataPointContext.setLimit(limit);
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
		return field != null && (field.getDataType() == FieldType.DATE.getTypeAsInt() || field.getDataType() == FieldType.DATE_TIME.getTypeAsInt());
	}

}
