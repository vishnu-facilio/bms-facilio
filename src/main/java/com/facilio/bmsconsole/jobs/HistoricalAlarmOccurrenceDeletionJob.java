package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmOccurrenceDeletionJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmOccurrenceDeletionJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		
		long parentRuleResourceLoggerId = jc.getJobId();
		WorkflowRuleHistoricalLoggerContext parentRuleResourceLoggerContext = WorkflowRuleHistoricalLoggerUtil.getWorkflowRuleHistoricalLoggerById(parentRuleResourceLoggerId);

		Long actualStartTime = parentRuleResourceLoggerContext.getStartTime();
		Long actualEndTime = parentRuleResourceLoggerContext.getEndTime();
		Long ruleId = parentRuleResourceLoggerContext.getRuleId();
		Long resourceId = parentRuleResourceLoggerContext.getResourceId();
		if (ruleId == null || resourceId == null) {
			return;
		}
		
		DateRange modifiedDateRange = deleteEntireAlarmOccurrences(ruleId, resourceId, actualStartTime, actualEndTime);	
		long lesserStartTime = modifiedDateRange.getStartTime();
		long greaterEndTime = modifiedDateRange.getEndTime();
		
		JSONObject props = new JSONObject();
		props.put("ruleId", ruleId);
		props.put("resourceId", resourceId);
		props.put("startTime", lesserStartTime);
		props.put("endTime", greaterEndTime);
		BmsJobUtil.addJobProps(parentRuleResourceLoggerId, "HistoricalRuleAlarmProcessing", props);  //To reprocess the alarms with the modified daterange in which it was deleted
		
		parentRuleResourceLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.EVENT_GENERATING_STATE.getIntVal());
		parentRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(parentRuleResourceLoggerContext);	
		
		List<Long> ruleResourceGroupedLoggerIds = WorkflowRuleHistoricalLoggerUtil.getGroupedRuleResourceWorkflowRuleHistoricalLoggerIds(parentRuleResourceLoggerId);
		for(Long loggerId:ruleResourceGroupedLoggerIds)
		{
			FacilioTimer.scheduleOneTimeJobWithDelay(loggerId, "HistoricalRunForReadingRule", 30, "history"); //For events, splitted start and end time would be fetched from the loggers
		}	
	}
	
	
	public static DateRange deleteEntireAlarmOccurrences(long ruleId, long resourceId, long startTime, long endTime) throws Exception 
	{		
		List<AlarmOccurrenceContext> alarmOccurrenceList = NewAlarmAPI.getAllAlarmOccurrences(ruleId, startTime, endTime, resourceId);

		if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
		{
			AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
			BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
			BaseAlarmContext baseAlarmContext = NewAlarmAPI.getBaseAlarmById(baseAlarm.getId());
			
			if (alarmOccurrenceList.get(0).getCreatedTime() < startTime) 
			{
				AlarmOccurrenceContext initialEdgeAlarmOccurrence = alarmOccurrenceList.get(0);			
				clearAlarmOccurrenceIdForEdgeEvents(initialEdgeAlarmOccurrence, initialEdgeAlarmOccurrence.getCreatedTime(), startTime); 
				startTime = initialEdgeAlarmOccurrence.getCreatedTime();	
			}
			if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime) 
			{		
				BaseEventContext finalEvent = getFinalEventForAlarmOccurrence(lastAlarmOccurrence);
				if(finalEvent != null && finalEvent.getCreatedTime() > endTime) {
					clearAlarmOccurrenceIdForEdgeEvents(lastAlarmOccurrence, endTime, finalEvent.getCreatedTime()); 
					endTime = finalEvent.getCreatedTime();
				}
			}	
			
			deleteAllAlarmOccurences(baseAlarmContext, startTime, endTime);
			NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);	
			NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);		
		}
		
		return new DateRange(startTime, endTime);	
	}
	
	public static void clearAlarmOccurrenceIdForEdgeEvents(AlarmOccurrenceContext edgeAlarmOccurrence, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allEventFields);
		
		List<FacilioField> eventFields = new ArrayList<FacilioField>();
		eventFields.add(fieldMap.get("lastOccurrenceId"));
		eventFields.add(fieldMap.get("baseAlarm"));	
		
		UpdateRecordBuilder<BaseEventContext> updateBuilder = new UpdateRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +edgeAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.fields(eventFields);
		
		Map<String, Object> map = new HashMap<>();
		map.put("lastOccurrenceId", -99);
		map.put("baseAlarm", -99);
		updateBuilder.updateViaMap(map);
	}
	

	public static BaseEventContext getFinalEventForAlarmOccurrence(AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC").limit(1);
		
		return selectEventbuilder.fetchFirst();
	}
	
	public static void deleteAllAlarmOccurences(BaseAlarmContext baseAlarmContext, long startTime, long endTime) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarmContext.getId(), NumberOperators.EQUALS));

		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		builder.andCriteria(criteria);
		builder.delete();			
	}
		
}
