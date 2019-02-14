package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class FetchExtraMetaForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		ReadingRuleContext preRequsite = alarmRule.getPreRequsite();
		
		int activeAlarmCount = getActiveAlarmCount(preRequsite);
		int alarmsCreatedThusWeek = getAlarmsCountCreatedThisWeek(preRequsite);
		Map<ResourceContext, Integer> resourceVsAlarmCount = getAllCriticalAssets(preRequsite);
		Map<String, Double> wfSummaryRes = getWorkflowSummary(preRequsite);
		
		context.put(FacilioConstants.ContextNames.ALARM_RULE_ACTIVE_ALARM, activeAlarmCount);
		context.put(FacilioConstants.ContextNames.ALARM_RULE_THIS_WEEK, alarmsCreatedThusWeek);
		context.put(FacilioConstants.ContextNames.ALARM_RULE_TOP_5_ASSETS, resourceVsAlarmCount);
		context.put(FacilioConstants.ContextNames.ALARM_RULE_WO_SUMMARY, wfSummaryRes);
		
		return false;
	}
	private Map<String, Double> getWorkflowSummary(ReadingRuleContext rule) throws Exception {
		
		Map<String,Double> result = new HashMap<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
		
		FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
				.select(fields)
				.innerJoin(ticketModule.getTableName())
				.on(ticketModule.getTableName()+".ID = Alarms.WO_ID")
				.innerJoin(ticketStatusModule.getTableName())
				.on(ticketStatusModule.getTableName()+".ID = "+ticketModule.getTableName()+".STATUS_ID")
				.moduleName(FacilioConstants.ContextNames.READING_ALARM)
				.beanClass(ReadingAlarmContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("woId"), "", CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), "", DateOperators.CURRENT_WEEK))
				.andCustomWhere(ticketStatusModule.getTableName()+".STATUS_TYPE = ?", TicketStatusContext.StatusType.OPEN.getIntVal())
				;
		 int woCreatedThisWeek = selectBuilder.get().size();
		 
		 result.put("woCreatedThisWeek", Double.parseDouble(""+woCreatedThisWeek));
		 
		 selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
					.select(fields)
					.innerJoin(ticketModule.getTableName())
					.on(ticketModule.getTableName()+".ID = Alarms.WO_ID")
					.innerJoin(ticketStatusModule.getTableName())
					.on(ticketStatusModule.getTableName()+".ID = "+ticketModule.getTableName()+".STATUS_ID")
					.moduleName(FacilioConstants.ContextNames.READING_ALARM)
					.beanClass(ReadingAlarmContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("woId"), "", CommonOperators.IS_NOT_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), "", DateOperators.CURRENT_MONTH))
					.andCustomWhere(ticketStatusModule.getTableName()+".STATUS_TYPE = ?", TicketStatusContext.StatusType.OPEN.getIntVal())
					;
		 int woCreatedThisMonth = selectBuilder.get().size();
		 
		 result.put("woCreatedThisMonth", Double.parseDouble(""+woCreatedThisMonth));
		 
		 FacilioField field = new FacilioField();
		 field.setName("avgResponseTimeInMins");
		 field.setColumnName("avg((ACKNOWLEDGED_TIME - CREATED_TIME)/1000/60)");
		 
		 FacilioModule readingAlarmModule = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		 FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.ALARM);
		 
		 GenericSelectRecordBuilder selectBuilder1 = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(field))
					.table(readingAlarmModule.getTableName())
					.innerJoin(alarmModule.getTableName())
					.on("Alarms.id = Reading_Alarms.id") 
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("acknowledgedTime"), "", CommonOperators.IS_NOT_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), "", CommonOperators.IS_NOT_EMPTY))
					;
		 List<Map<String, Object>> props = selectBuilder1.get();
		 Double avgResponseTimeInMins = 0.0;
		 if(props != null && !props.isEmpty()) {
			 avgResponseTimeInMins = Double.parseDouble(props.get(0).get("avgResponseTimeInMins").toString());
		 }
		 result.put("avgResponseTimeInMins", avgResponseTimeInMins);
		 
		 field = new FacilioField();
		 field.setName("avgResolutionTimeInMins");
		 field.setColumnName("avg((CLEARED_TIME - CREATED_TIME)/1000/60)");
		 
		 
		 selectBuilder1 = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(field))
					.table(readingAlarmModule.getTableName())
					.innerJoin(alarmModule.getTableName())
					.on("Alarms.id = Reading_Alarms.id")
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("clearedTime"), "", CommonOperators.IS_NOT_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), "", CommonOperators.IS_NOT_EMPTY))
					;
		 props = selectBuilder1.get();
		 Double avgResolutionTimeInMins = 0.0;
		 if(props != null && !props.isEmpty()) {
			 avgResolutionTimeInMins = Double.parseDouble(props.get(0).get("avgResolutionTimeInMins").toString());
		 }
		 result.put("avgResolutionTimeInMins", avgResolutionTimeInMins);
		 
		 return result;
	}
	private int getActiveAlarmCount(ReadingRuleContext rule) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.READING_ALARM)
				.beanClass(ReadingAlarmContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("clearedTime"), "", CommonOperators.IS_EMPTY))
				;
		return selectBuilder.get().size();
	}
	
	private int getAlarmsCountCreatedThisWeek(ReadingRuleContext rule) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ReadingAlarmContext> selectBuilder = new SelectRecordsBuilder<ReadingAlarmContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.READING_ALARM)
				.beanClass(ReadingAlarmContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), "", DateOperators.CURRENT_WEEK))
				;
		return selectBuilder.get().size();
	}
	
	private Map<ResourceContext, Integer> getAllCriticalAssets(ReadingRuleContext rule) throws Exception {
		
		Map<ResourceContext,Integer> resourceVsAlarmCount = new HashMap<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule readingAlarmModule = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		 FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
		
		List<FacilioField> fetchFields = new ArrayList<>();
		FacilioField countField = FormulaContext.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("resource"));
		countField.setName("count");
		fetchFields.add(countField);
		fetchFields.add(fieldMap.get("resource"));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fetchFields)
				.table(readingAlarmModule.getTableName())
				.innerJoin(ticketModule.getTableName())
				.on(readingAlarmModule.getTableName()+".ID = "+ticketModule.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				.groupBy(fieldMap.get("resource").getColumnName())
				.orderBy(countField.getColumnName() + " desc")
				;
		 List<Map<String, Object>> props = selectBuilder.get();
		 
		 for(Map<String, Object> prop :props) {
			 Long resourceId = (Long)prop.get("resource");
			 ResourceContext resource = ResourceAPI.getResource(resourceId);
			 Integer count = Integer.parseInt(prop.get(countField.getName()).toString());
			 
			 resourceVsAlarmCount.put(resource, count);
		 }
		 return resourceVsAlarmCount;
	}
}
