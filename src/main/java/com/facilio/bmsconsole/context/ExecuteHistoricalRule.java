package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.SingletonMap;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public abstract class ExecuteHistoricalRule {
	
	public abstract List<BaseEventContext> executeRuleAndGenerateEvents(JSONObject loggerInfo, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception;
	
	public List<String> getExecutionLoggerInfoProps() { //same will be applied as fieldName for occurrence and event-processing criteria
		List<String> defaultLoggerInfoPropList = new ArrayList<String>();
		defaultLoggerInfoPropList.add("rule");
		defaultLoggerInfoPropList.add("resource"); 
		return defaultLoggerInfoPropList;
	}
	
	public String fetchPrimaryLoggerKey() {
		return getExecutionLoggerInfoProps().get(0);	
	}
	
	public List<Long> getMatchedSecondaryParamIds(JSONObject loggerInfo, Boolean isInclude) throws Exception{
		if(loggerInfo.size() > 1) {
			throw new IllegalArgumentException("Secondary param is invalid to run through single-key based historical logger data.");
		}
		String primaryPropKeyName = fetchPrimaryLoggerKey();
		Long primaryProp = (Long)loggerInfo.get(primaryPropKeyName);
		return Collections.singletonList(primaryProp);
	}
	
	public Criteria getOccurrenceDeletionCriteria(JSONObject loggerInfo, Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		AlarmOccurrenceContext.Type alarmOccurrenceType = NewAlarmAPI.getOccurrenceTypeFromAlarmType(type);
		FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(alarmOccurrenceType));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		Criteria deletionCriteria = new Criteria();
		for(String loggerInfoPropKeyName :getExecutionLoggerInfoProps()) { //occurrence fieldName
			deletionCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(loggerInfoPropKeyName), String.valueOf(loggerInfo.get(loggerInfoPropKeyName)), NumberOperators.EQUALS));
		}
		return deletionCriteria;
	}
	
	public Criteria getEventsProcessingCriteria(JSONObject loggerInfo, Type type) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
		Criteria fetchCriteria = new Criteria();
		for(String loggerInfoPropKeyName :getExecutionLoggerInfoProps()) { //event fieldName
			fetchCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(loggerInfoPropKeyName), String.valueOf(loggerInfo.get(loggerInfoPropKeyName)), NumberOperators.EQUALS));
		}
		return fetchCriteria;
	}
	
	public String fetchSecondaryLoggerKey() {
		if(getExecutionLoggerInfoProps().size() > 1) {
			return getExecutionLoggerInfoProps().get(1);	
		}
		return null;	
	}
}
