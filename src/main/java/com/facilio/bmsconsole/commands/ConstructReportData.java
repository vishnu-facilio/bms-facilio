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
		
		
		reportContext.setType(ReportType.READING_REPORT);
		reportContext.setxAggr(0);
		reportContext.setxAlias("X");
		
		JSONObject xAxisJSON = (JSONObject) context.get("x-axis");
		JSONArray yAxisJSON = (JSONArray) context.get("y-axis");
		JSONArray groupByJSONArray = (JSONArray) context.get("group-by");
		Criteria criteria = (Criteria) context.get("criteria");
		JSONArray sortFields = (JSONArray) context.get("sort_fields");
		Integer sortOrder = null;
		if (context.containsKey("sort_order")) {
			sortOrder = ((Number) context.get("sort_order")).intValue();
		}
		Integer limit = null;
		if (context.containsKey("limit")) {
			limit = ((Number) context.get("limit")).intValue();
		}
		
		if (yAxisJSON == null || yAxisJSON.size() == 0) {
			addDataPointContext(reportContext, xAxisJSON, null, groupByJSONArray, criteria, sortFields, sortOrder, limit);
		} else {
			for (int i = 0; i < yAxisJSON.size(); i++) {
				addDataPointContext(reportContext, xAxisJSON, (Map) yAxisJSON.get(i), groupByJSONArray, criteria, sortFields, sortOrder, limit);
			}
		}
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}
	
	private void addDataPointContext(ReportContext reportContext, JSONObject xAxisJSON, Map yMap, JSONArray groupByJSONArray, Criteria criteria, JSONArray sortFields, Integer sortOrder, Integer limit) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
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
		
		ReportYAxisContext yAxis = new ReportYAxisContext();
		FacilioField yField = null;

		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		if (yMap == null) {
			yField = FieldFactory.getIdField(xField.getModule());
		} else {
			if (yMap == null || !(yMap.containsKey("field_id"))) {
				yField = FieldFactory.getIdField(xField.getModule());
			} else {
				if (yMap.containsKey("aggr")) {
					yAggr = AggregateOperator.getAggregateOperator(((Number) yMap.get("aggr")).intValue());
				}
				yField = modBean.getField((Long) yMap.get("field_id"));
			}
		}

		yAxis.setAggr(yAggr);
		yAxis.setField(yField);
		dataPointContext.setyAxis(yAxis);
		
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

				if (groupByJSON.containsKey("aggr")) {
					groupByField.setAggr(AggregateOperator.getAggregateOperator(((Number) groupByJSON.get("aggr")).intValue()));
				}
				
				groupByFields.add(groupByField);
			}
			dataPointContext.setGroupByFields(groupByFields);
		}
		
		if (criteria != null) {
			dataPointContext.setCriteria(criteria);
		}
		
		if (sortFields != null) {
			List<String> orderBy = new ArrayList<>();
			for (int i = 0; i < sortFields.size(); i++) {
				Map object = (Map) sortFields.get(i);
				FacilioField orderByField = modBean.getField((Long) object.get("field_id"));
				orderBy.add(orderByField.getCompleteColumnName());
			}
			dataPointContext.setOrderBy(orderBy);
			
			if (sortOrder != null) {
				dataPointContext.setOrderByFunc(sortOrder);
			}
		}
		if (limit != null) {
			dataPointContext.setLimit(limit);
		}
		
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", yField.getDisplayName());
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		
		reportContext.addDataPoint(dataPointContext);
	}

	private boolean isDateField(FacilioField field) {
		return field != null && (field.getDataType() == FieldType.DATE.getTypeAsInt() || field.getDataType() == FieldType.DATE_TIME.getTypeAsInt());
	}

}
