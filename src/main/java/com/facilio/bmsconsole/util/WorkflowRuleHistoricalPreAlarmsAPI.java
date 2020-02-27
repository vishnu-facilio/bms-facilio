package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.PreAlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class WorkflowRuleHistoricalPreAlarmsAPI {
	
	public static List<PreAlarmOccurrenceContext> getAllPreAlarmOccurrences(long ruleId, long startTime, long endTime, long resourceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
	
		SelectRecordsBuilder<PreAlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<PreAlarmOccurrenceContext>()
				.beanClass(PreAlarmOccurrenceContext.class).moduleName(FacilioConstants.ContextNames.PRE_ALARM_OCCURRENCE)
				.select(allFields)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", "" + ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resource", "" + resourceId, NumberOperators.EQUALS));
//				.andCondition(CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType",, NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + endTime, NumberOperators.LESS_THAN_EQUAL));
		
		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + startTime, NumberOperators.GREATER_THAN_EQUAL));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);
		
		selectbuilder.andCriteria(criteria);
		selectbuilder.orderBy("CREATED_TIME");
		
		List<PreAlarmOccurrenceContext> preAlarmOccurrenceList = selectbuilder.get();
		
		return preAlarmOccurrenceList;
	}
	
	public static void clearAlarmOccurrenceIdForEdgePreAlarms(AlarmOccurrenceContext edgeAlarmOccurrence, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule preAlarmsModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT); // change to preAlarms moduleName
		List<FacilioField> allPreAlarmsFields = modBean.getAllFields(preAlarmsModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allPreAlarmsFields);
		
		List<FacilioField> preAlarmFields = new ArrayList<FacilioField>();
		preAlarmFields.add(fieldMap.get("baseAlarm"));	
		preAlarmFields.add(fieldMap.get("alarmOccurrence"));
		
//		UpdateRecordBuilder<PreAlarmsEventContext> updateBuilder = new UpdateRecordBuilder<PreAlarmsEventContext>() // change to preAlarmsContext
//				.module(preAlarmsModule)
//				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +edgeAlarmOccurrence.getId(), NumberOperators.EQUALS))
//				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
//				.fields(preAlarmFields);
		
		BaseAlarmContext baseAlarmNull = new BaseAlarmContext();
		baseAlarmNull.setId(-99);
		
		AlarmOccurrenceContext alarmOccurrenceContextNull = new AlarmOccurrenceContext();
		alarmOccurrenceContextNull.setId(-99);
		
		Map<String, Object> map = new HashMap<>();
		map.put("baseAlarm", FieldUtil.getAsProperties(baseAlarmNull));
		map.put("alarmOccurrence", FieldUtil.getAsProperties(alarmOccurrenceContextNull));
//		updateBuilder.updateViaMap(map);
	}

}
