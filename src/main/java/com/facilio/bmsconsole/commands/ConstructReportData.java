package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext.ReportType;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructReportData implements Command {

	private int moduleType = -1;
	private FacilioModule module;

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		moduleType = (int) context.get(FacilioConstants.Reports.MODULE_TYPE);
		if (reportContext == null) {
			reportContext = new ReportContext();
			reportContext.setType(ReportType.WORKORDER_REPORT);
		}
		reportContext.setModuleType(moduleType);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = null;
		if (reportContext.getModuleId() > 0) {
			module = modBean.getModule(reportContext.getModuleId());
		} else if (StringUtils.isNotEmpty(moduleName)) {
			module = modBean.getModule(moduleName);
		}
		
		if (module == null) {
			throw new Exception("Module name should not be empty");
		}
		
		context.put(FacilioConstants.ContextNames.MODULE, module);
		
		reportContext.setxAggr(0);
		reportContext.setxAlias("X");
		reportContext.setModuleId(module.getModuleId());
		
		List<ReportUserFilterContext> userFilters = (List<ReportUserFilterContext>) context.get("user-filters");
		if (CollectionUtils.isNotEmpty(userFilters)) {
			for (ReportUserFilterContext userFilterContext : userFilters) {
				userFilterContext.setField(modBean.getField(userFilterContext.getFieldId()));
			}
		}
		reportContext.setUserFilters(userFilters);
		
		JSONObject xAxisJSON = (JSONObject) context.get("x-axis");
		JSONObject dateFieldJSON = (JSONObject) context.get("date-field");
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
			addDataPointContext(modBean, reportContext, xAxisJSON, dateFieldJSON, null, groupByJSONArray, criteria, sortFields, sortOrder, limit);
		} else {
			for (int i = 0; i < yAxisJSON.size(); i++) {
				addDataPointContext(modBean, reportContext, xAxisJSON, dateFieldJSON, (Map) yAxisJSON.get(i), groupByJSONArray, criteria, sortFields, sortOrder, limit);
			}
		}
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}
	
	private void addDataPointContext(ModuleBean modBean, ReportContext reportContext, JSONObject xAxisJSON, JSONObject dateField, Map yMap, JSONArray groupByJSONArray, Criteria criteria, JSONArray sortFields, Integer sortOrder, Integer limit) throws Exception {
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		
		ReportFieldContext xAxis = new ReportFieldContext();
		
		FacilioField xField = null;
		if (xAxisJSON.containsKey("field_id")) {
			Object fieldId = xAxisJSON.get("field_id");
			xField = getField(modBean, fieldId);
		}
 		
		if (xAxisJSON.containsKey("aggr")) {
			Integer xAggr = ((Number) xAxisJSON.get("aggr")).intValue();
			AggregateOperator aggregateOperator = AggregateOperator.getAggregateOperator(xAggr);
			if (aggregateOperator instanceof DateAggregateOperator && isDateField(xField)) {
				reportContext.setxAggr(aggregateOperator);				
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
			throw new Exception("x field should be mandatory");
		}
		FacilioModule xAxisModule = null;
		if (xAxisJSON.containsKey("module_id")) {
			xAxisModule = modBean.getModule((Long) xAxisJSON.get("module_id"));
		}
		xAxis.setField(xAxisModule, xField);
		dataPointContext.setxAxis(xAxis);
		
		
		if (dateField != null && dateField.containsKey("operator") && dateField.containsKey("date_value")) {
			Integer operator = ((Number) dateField.get("operator")).intValue();
			reportContext.setDateOperator(operator);
			reportContext.setDateValue((String) dateField.get("date_value"));
			ReportFieldContext reportFieldContext = new ReportFieldContext();
			FacilioField field = modBean.getField((Long) dateField.get("field_id"));
			long moduleId = -1;
			if (dateField.containsKey("module_id")) {
				moduleId = (long) dateField.get("module_id");
			}
			reportFieldContext.setField(modBean.getModule(moduleId), field);
			dataPointContext.setDateField(reportFieldContext);
		}
		
		ReportYAxisContext yAxis = new ReportYAxisContext();
		FacilioField yField = null;

		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		FacilioModule yAxisModule = null;
		if (yMap == null) {
			yField = FieldFactory.getIdField(module);
			yAxisModule = module;
		} else {
			if (yMap == null || !(yMap.containsKey("field_id"))) {
				yField = FieldFactory.getIdField(xField.getModule());
				yAxisModule = module;
			} else {
				if (yMap.containsKey("aggr")) {
					yAggr = AggregateOperator.getAggregateOperator(((Number) yMap.get("aggr")).intValue());
				}
				Object fieldId = yMap.get("field_id");
				yField = getField(modBean, fieldId);
				if (yMap.containsKey("module_id")) {
					yAxisModule = modBean.getModule((Long) yMap.get("module_id"));
				}
			}
		}

		yAxis.setAggr(yAggr);
		yAxis.setField(yAxisModule, yField);
		dataPointContext.setyAxis(yAxis);
		
		if (groupByJSONArray != null) {
			List<ReportGroupByField> groupByFields = new ArrayList<>();
			for (int i = 0; i < groupByJSONArray.size(); i++) {
				Map groupByJSON = (Map) groupByJSONArray.get(i);
				ReportGroupByField groupByField = new ReportGroupByField();
				
				if (!groupByJSON.containsKey("field_id")) {
					throw new Exception("Field ID should be mandatory");
				}
				
				Object fieldId = groupByJSON.get("field_id");
				FacilioField field = getField(modBean, fieldId);
				
				FacilioModule groupByModule = null;
				if (groupByJSON.containsKey("module_id")) {
					groupByModule = modBean.getModule((Long) groupByJSON.get("module_id"));
				}
				groupByField.setField(groupByModule, field);
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
				Object fieldId = object.get("field_id");
				if (fieldId instanceof String && ((String) fieldId).equals("y-field")) {
					orderBy.add(yAggr.getSelectField(yField).getCompleteColumnName());
				} else if (fieldId instanceof Long) {
					FacilioField orderByField = modBean.getField((Long) fieldId);
					orderBy.add(orderByField.getCompleteColumnName());
				}
			}
			dataPointContext.setOrderBy(orderBy);
			
			if (sortOrder != null) {
				dataPointContext.setOrderByFunc(sortOrder);
			}
		}
		if (limit != null && limit > 0) {
			dataPointContext.setLimit(limit);
		}
		
		if (module.getName().equals("workorder")) {
			SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
					.beanClass(TicketStatusContext.class)
					.module(modBean.getModule("ticketstatus"))
					.select(modBean.getAllFields("ticketstatus"))
					.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", "3", NumberOperators.EQUALS));
			List<TicketStatusContext> list = builder.get();
			if (CollectionUtils.isNotEmpty(list)) {
				long id = list.get(0).getId();
				Criteria c = new Criteria();
				Operator operator = NumberOperators.NOT_EQUALS;
				if (moduleType == 2) {
					operator = NumberOperators.EQUALS;
				}
				c.addAndCondition(CriteriaAPI.getCondition("STATUS_ID", "status", String.valueOf(id), operator));
				dataPointContext.setOtherCriteria(c);	
			}
		}
				
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", yField.getDisplayName());
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		
		reportContext.addDataPoint(dataPointContext);
	}
	
	private FacilioField getField(ModuleBean modBean, Object fieldId) throws Exception {
		FacilioField xField = null;
		if (fieldId instanceof Long) {
			xField = modBean.getField((Long) fieldId);
		} else if (fieldId instanceof String) {
			xField = modBean.getField((String) fieldId, module.getName());
			if (xField == null) {
				xField = ReportFactory.getReportField((String) fieldId);
			}
		}
		return xField;
	}

	private boolean isDateField(FacilioField field) {
		return field != null && (field.getDataType() == FieldType.DATE.getTypeAsInt() || field.getDataType() == FieldType.DATE_TIME.getTypeAsInt());
	}

}
