package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchExtraMetaForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		ReadingRuleContext preRequsite = alarmRule.getPreRequsite();
		
		int activeAlarmCount = getActiveAlarmCount(preRequsite);
		int alarmsCreatedThusWeek = getAlarmsCountCreatedThisWeek(preRequsite);
		Map<ResourceContext, Integer> resourceVsAlarmCount = getTop5CriticalAssets(preRequsite);
		
		context.put(FacilioConstants.ContextNames.ALARM_RULE_ACTIVE_ALARM, activeAlarmCount);
		context.put(FacilioConstants.ContextNames.ALARM_RULE_THIS_WEEK, alarmsCreatedThusWeek);
		context.put(FacilioConstants.ContextNames.ALARM_RULE_TOP_5_ASSETS, resourceVsAlarmCount);
		
		return false;
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
	
	private Map<ResourceContext, Integer> getTop5CriticalAssets(ReadingRuleContext rule) throws Exception {
		
		Map<ResourceContext,Integer> resourceVsAlarmCount = new HashMap<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<FacilioField> fetchFields = new ArrayList<>();
		FacilioField countField = FormulaContext.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("resource"));
		fetchFields.add(countField);
		fetchFields.add(fieldMap.get("resource"));
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.select(fetchFields)
				.moduleName(FacilioConstants.ContextNames.READING_ALARM)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(rule.getId()), NumberOperators.EQUALS))
				.groupBy(fieldMap.get("resource").getColumnName())
				.orderBy(countField.getColumnName() + " desc")
				.limit(5)
				;
		 List<Map<String, Object>> props = selectBuilder.getAsProps();
		 
		 for(Map<String, Object> prop :props) {
			 Long resourceId = (Long) prop.get("resource");
			 ResourceContext resource = ResourceAPI.getResource(resourceId);
			 Integer count = (Integer) prop.get(countField.getName());
			 
			 resourceVsAlarmCount.put(resource, count);
		 }
		 return resourceVsAlarmCount;
	}
}
