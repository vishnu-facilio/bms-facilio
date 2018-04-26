package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class EnergyPerformanceIndicatiorAPI {
	
	public static long getStartTimeForHistoricalCalculation(EnergyPerformanceIndicatorContext enpi) {
		switch (enpi.getFrequencyEnum()) {
			case DAILY:
				return DateTimeUtil.getDayStartTime(-90, true);
			case WEEKLY:
			case MONTHLY:
				return DateTimeUtil.getMonthStartTime(-12, true);
			case HALF_YEARLY:
			case QUARTERTLY:
				return DateTimeUtil.getYearStartTime(-1, true);
			default:
				return -1;
		}
	}
	
	public static long addEnPI (EnergyPerformanceIndicatorContext enpi) throws Exception {
		updateChildIds(enpi);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getEnPIModule().getTableName())
														.fields(FieldFactory.getEnPIFields())
														;
		
		long id = insertBuilder.insert(FieldUtil.getAsProperties(enpi));
		if (id == -1) {
			throw new RuntimeException("Unable to add EnPI");
		}
		else {
			enpi.setId(id);
//			FacilioTimer.scheduleCalendarJob(id, "ENPICalculatior", System.currentTimeMillis(), enpi.getSchedule(), "facilio");
		}
		
		return enpi.getId();
	}
	
	private static void updateChildIds(EnergyPerformanceIndicatorContext enpi) throws Exception {
		long workflowId = WorkflowUtil.addWorkflow(enpi.getWorkflow());
		enpi.setWorkflowId(workflowId);
		enpi.setOrgId(AccountUtil.getCurrentOrg().getId());
		enpi.setReadingFieldId(enpi.getReadingField().getId());
	}
	
	public static EnergyPerformanceIndicatorContext getENPI(long enpiId) throws Exception {
		FacilioModule module = ModuleFactory.getEnPIModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getEnPIFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(enpiId, module))
														;
		
		List<EnergyPerformanceIndicatorContext> enpiList = getENPIFromProps(selectBuilder.get());
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static ReadingContext calculateENPI(EnergyPerformanceIndicatorContext enpi, long startTime, long endTime) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		
		double resultVal = (double) WorkflowUtil.getWorkflowExpressionResult(enpi.getWorkflow().getWorkflowString(), params);
		
		ReadingContext reading = new ReadingContext();
		reading.setParentId(enpi.getSpaceId());
		reading.addReading(enpi.getReadingField().getName(), resultVal);
		reading.setTtime(endTime);
		
		return reading;
	}
	
	public static List<EnergyPerformanceIndicatorContext> getAllENPIs() throws Exception {
		FacilioModule module = ModuleFactory.getEnPIModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getEnPIFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		
		return getENPIFromProps(selectBuilder.get());
		
	}
	
	public static List<EnergyPerformanceIndicatorContext> getEnPIsOfType(List<Integer> types) throws Exception {
		FacilioModule module = ModuleFactory.getEnPIModule();
		List<FacilioField> fields = FieldFactory.getEnPIFields();
		FacilioField frequenctField = FieldFactory.getAsMap(fields).get("frequency");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(frequenctField, StringUtils.join(types, ","), NumberOperators.EQUALS))
														;
		
		return getENPIFromProps(selectBuilder.get());
		
	}
	
	public static void recalculateHistoricalData(EnergyPerformanceIndicatorContext enpi, FacilioField enpiField) throws Exception {
		ModuleCRUDBean crudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		crudBean.deleteAllData(enpiField.getModule().getName());
		
		FacilioTimer.deleteJob(enpi.getId(), "HistoricalENPICalculator");
		FacilioTimer.scheduleOneTimeJob(enpi.getId(), "HistoricalENPICalculator", 30, "priority");
	}
	
	private static List<EnergyPerformanceIndicatorContext> getENPIFromProps (List<Map<String, Object>> props) throws Exception {
		if( props != null && !props.isEmpty()) {
			List<EnergyPerformanceIndicatorContext> enpiList = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map<String, Object> prop : props) {
				EnergyPerformanceIndicatorContext enpi = FieldUtil.getAsBeanFromMap(prop, EnergyPerformanceIndicatorContext.class);
				enpi.setReadingField(modBean.getField(enpi.getReadingFieldId()));
				enpi.setWorkflow(WorkflowUtil.getWorkflowContext(enpi.getWorkflowId(),true));
				
				List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowFields(enpi.getWorkflowId());
				if (workflowFields != null && !workflowFields.isEmpty()) {
					List<FacilioField> dependentFields = new ArrayList<>();
					for (WorkflowFieldContext workflowField : workflowFields) {
						dependentFields.add(modBean.getField(workflowField.getFieldId()));
					}
					enpi.setDependentFields(dependentFields);
				}
				
				enpiList.add(enpi);
			}
			return enpiList;
		}
		return null;
	}

}
