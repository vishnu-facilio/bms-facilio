package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.*;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.report.context.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportDrilldownParamsContext.DrilldownCriteria;
import com.facilio.report.util.ReportUtil;

public class ReportDrilldownCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ReportDrilldownParamsContext drillDownParamCtx = (ReportDrilldownParamsContext) context
				.get(FacilioConstants.ContextNames.REPORT_DRILLDOWN_PARAMS);

		if (drillDownParamCtx != null) {
			ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

			FacilioModule module = null;

			if (reportContext.getModuleId() > 0) {
				module = modBean.getModule(reportContext.getModuleId());
			} else if (StringUtils.isNotEmpty(moduleName)) {
				module = modBean.getModule(moduleName);
			}

			if (module == null) {
				throw new Exception("Module name should not be empty");
			}

			if (reportContext.getDataPoints() == null || reportContext.getDataPoints().isEmpty()) {
				return false;
			}
			String seriesAlias = drillDownParamCtx.getSeriesAlias();
			// multi series reports  get reduced to single series(whichever was clicked)

			List<ReportDataPointContext> drilledReportDataPoints = reportContext.getDataPoints().stream()
					.filter((ReportDataPointContext dp) -> {
						return dp.getAliases().get("actual").equals(seriesAlias);
					}).collect(Collectors.toList());

			ReportDataPointContext drilledReportDataPoint = drilledReportDataPoints.get(0);
			drilledReportDataPoint.setGroupByFields(null);//remove group by in report after drill down
			
			
			
			
			
			
			
			
			ReportFieldContext newDataPointXaxis = this.getNewXAxis(drillDownParamCtx.getxField(), module);
			
			drilledReportDataPoint.setxAxis(newDataPointXaxis);

			Criteria criteria = this.generateCriteria(drillDownParamCtx.getDrilldownCriteria());
			drillDownParamCtx.setCriteria(criteria);
			reportContext.setDataPoints(drilledReportDataPoints);
			//this.handleDateFieldDrillDown(reportContext,drillDownParamCtx.getxAggrEnum(),drillDownParamCtx.getDrilldownCriteria());
			reportContext.setxAggr(drillDownParamCtx.getxAggr());
			reportContext.setDrilldownParams(drillDownParamCtx);

		}
		return false;
	}
	
//	private void handleDateFieldDrillDown(ReportContext report,AggregateOperator drillDownAggregation,List<DrilldownCriteria> drillDownCriteriaList)
//	{
//		//setting dateRange to last drilldown criteria's range .
//		//daterange ingore by client in modulenewreport and overridden with UI datepicker.dateobject. need to change
//		if(ReportUtil.isDateAggregateOperator(drillDownAggregation)) {
//			//if there is an X aggregation then PREVIOUS Criteria is always a TIME_STAMP value.
//			
//			String betweenDates=drillDownCriteriaList.get(drillDownCriteriaList.size()-1).getDimensionValues();
//			String[] timeStampArray=betweenDates.split(",");
//			
//			report.setDateValue(betweenDates);
//			report.setDateRange(new DateRange(Long.parseLong(timeStampArray[0]), Long.parseLong(timeStampArray[1])));
//			
//		}
//	}
	
	private ReportFieldContext getNewXAxis(Map<String, Object> xAxisJSON, FacilioModule module) throws Exception {
		ReportFieldContext xAxis = new ReportFieldContext();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField xAxisField = null;
		if (xAxisJSON.containsKey("field_id")) {
			Object fieldId = xAxisJSON.get("field_id");
			xAxisField = ReportUtil.getField(modBean, fieldId, module);
		}
		FacilioModule xAxisModule = null;
		if (xAxisJSON.containsKey("module_id")) {
			xAxisModule = modBean.getModule((Long) xAxisJSON.get("module_id"));
		}
		if (xAxisJSON.containsKey("lookupFieldId")) {
			xAxis.setLookupFieldId((Long) xAxisJSON.get("lookupFieldId"));
		}

		xAxis.setField(xAxisModule, xAxisField);
		return xAxis;
	}

	private Criteria generateCriteria(List<ReportDrilldownParamsContext.DrilldownCriteria> drillldownCriteriaList)
			throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Criteria criteria = new Criteria();

		for (DrilldownCriteria drilldownCriteria : drillldownCriteriaList) {

			Map<String, Object> xField = drilldownCriteria.getxField();

			long fieldId = (long) xField.get("field_id");
			long moduleId = (long) xField.get("module_id");
			long xAggrOperator = (long) xField.get("xAggr");
			AggregateOperator xAggr = AggregateOperator.getAggregateOperator((int)xAggrOperator);
			String dimensionValues = drilldownCriteria.getDimensionValues();

			FacilioModule module = modBean.getModule(moduleId);
			
			
			FacilioField xAxisField =null;
			if (fieldId == -1) {// clicked X dimension is a formula field

				String fieldName = drilldownCriteria.getFormulaFieldName();
				int operatorId = drilldownCriteria.getFormulaFieldOperator();
				String moduleName = drilldownCriteria.getFormulaModuleName();
				
				FacilioField field = modBean.getField(fieldName, moduleName);
				Operator operator = Operator.getOperator(operatorId);
				Condition condition = CriteriaAPI.getCondition(field, dimensionValues, operator);
				criteria.addAndCondition(condition);

			} else {
				xAxisField = modBean.getField(fieldId);
				if(xAxisField instanceof MultiEnumField || xAxisField instanceof MultiLookupField)
				{
					Condition condition = new Condition();
					condition.setField(xAxisField);
					condition.setOperatorId(90);
					condition.setValue(dimensionValues);
					condition.validateValue();
					criteria.addAndCondition(condition);
				}
				else {
					Condition condition = CriteriaAPI.getCondition(xAxisField, dimensionValues,
							getOperator(xAxisField, xAggr));
					criteria.addAndCondition(condition);
				}

			}
			
			
			

			Map<String, Object> groupBy = drilldownCriteria.getGroupBy();
			if (groupBy != null) {
				long groupByFieldId = (long) groupBy.get("field_id");
//				long groupByModuleId = (long) groupBy.get("module_id");
				long groupByXAggrOperator = (long) groupBy.get("xAggr");
				AggregateOperator groupByXAggr = AggregateOperator.getAggregateOperator((int)groupByXAggrOperator);

				String groupByValues = drilldownCriteria.getGroupByValues();

//				FacilioModule groupBymodule = modBean.getModule(groupByModuleId);
				FacilioField groupByField = modBean.getField(groupByFieldId);
				Condition groupByCondition = null;
				if(groupByFieldId != -1){
					groupByCondition = CriteriaAPI.getCondition(groupByField, groupByValues,
							getOperator(groupByField, groupByXAggr));
					criteria.addAndCondition(groupByCondition);
				}
				else  if(groupBy.containsKey("colName") && groupBy.get("colName") != null)
				{
					ReportFactory.ReportFacilioField groupbyfield = (ReportFactory.ReportFacilioField)ReportFactory.getReportField((String)groupBy.get("colName"));
					if(groupbyfield.getName().equals("overdue_open") || groupbyfield.getName().equals("overdue_closed") ) {
						if(groupByValues.equals("Ontime") && groupbyfield.getName().equals("overdue_open")){
							groupByCondition = groupbyfield.getConditions().get("On Schedule");
						}
						else{
							groupByCondition = groupbyfield.getConditions().get(groupByValues);

						}
						groupByCondition.setColumnName("Tickets.DUE_DATE");
						criteria.addAndCondition(groupByCondition);
					}
					else{
						Map<String, Condition> condition_map = replaceSpecialFieldVals((String) groupBy.get("colName"), groupByValues, groupbyfield);
						groupByCondition = !condition_map.isEmpty() && condition_map.containsKey(groupByValues) ? condition_map.get(groupByValues) : null;
						criteria.addAndCondition(groupByCondition);
					}
				}
			}

		}

		return criteria;
	}

	private Map<String, Condition> replaceSpecialFieldVals(String colName, String sel_value, ReportFactory.ReportFacilioField reportField)throws Exception
	{
		Map<String, Condition> conditions = new HashMap<>();
		switch (colName)
		{
			case ReportFactory.WorkOrder.OPENVSCLOSE_COL:
				FacilioStatus.StatusType statusType = FacilioStatus.StatusType.OPEN.getStringVal().equals(sel_value) ? FacilioStatus.StatusType.OPEN :
						FacilioStatus.StatusType.CLOSED.getStringVal().equals(sel_value) ? FacilioStatus.StatusType.CLOSED : FacilioStatus.StatusType.SKIPPED;
				List<FacilioStatus> list = TicketAPI.getStatusOfStatusType(statusType);
				Map<String, Object> status_data = new HashMap<>();
				if (CollectionUtils.isNotEmpty(list)) {
					List<Long> statusIds = new ArrayList<>();
					for (FacilioStatus status : list) {
						statusIds.add(status.getId());
					}
					status_data.put("status_id", StringUtils.join(statusIds, ","));
				}
				String openarguments="", closedarguments="", skippedarguments="";
				if(statusType == FacilioStatus.StatusType.OPEN){
					openarguments = String.valueOf(status_data.get("status_id"));
				}else if(statusType == FacilioStatus.StatusType.CLOSED){
					closedarguments = String.valueOf(status_data.get("status_id"));
				}else {
					skippedarguments = String.valueOf(status_data.get("status_id"));
				}
				Map<String, Condition> genericConditions = reportField.getGenericConditions();
				for (String key : genericConditions.keySet())
				{
					Condition condition = genericConditions.get(key);
					Map<String, Object> conditionProperty = FieldUtil.getAsProperties(condition);
					Condition c = FieldUtil.getAsBeanFromMap(conditionProperty, Condition.class);
					String value = c.getValue();
					value = value.replace("?", closedarguments);
					value = value.replace("*", openarguments);
					value = value.replaceAll("@", skippedarguments);
					c.setColumnName("Tickets.MODULE_STATE");
					c.setValue(value);
					conditions.put(key, c);
				}

				String columnName = reportField.getGenericColumnName();
				columnName = columnName.replace("?", closedarguments);
				columnName = columnName.replace("*", openarguments);
				columnName = columnName.replace("@", skippedarguments);
				reportField.setColumnName(columnName);
				break;
		}
		return conditions;
	}

	private Operator getOperator(FacilioField field, AggregateOperator xAggr) {
		Operator operator = null;
		FieldType dataType = field.getDataTypeEnum();

		switch (dataType) {
			case ENUM:
			case SYSTEM_ENUM:
				operator = PickListOperators.IS;
				break;
			case STRING_SYSTEM_ENUM:
				operator = StringOperators.IS;
				break;
			case BOOLEAN:
				operator=BooleanOperators.IS;
				break;
			case LOOKUP:
				LookupField lookupField = (LookupField) field;
				String lookupModuleName = lookupField.getLookupModule().getName();
				if (lookupModuleName.equals("basespace")
						|| (lookupModuleName.equals("resource") && ReportUtil.isSpaceAggregation(xAggr))) {
					operator = BuildingOperator.BUILDING_IS;
				} else {
					operator = PickListOperators.IS;
				}
				break;
			case DATE:
			case DATE_TIME:
				operator = DateOperators.BETWEEN;
				break;
		}
		return operator;
	}

}
