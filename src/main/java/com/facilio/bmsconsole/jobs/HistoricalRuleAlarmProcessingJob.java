package com.facilio.bmsconsole.jobs;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class HistoricalRuleAlarmProcessingJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRuleAlarmProcessingJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		
		long parentRuleResourceLoggerId = jc.getJobId();
		JSONObject props = BmsJobUtil.getJobProps(parentRuleResourceLoggerId, jc.getJobName());
		Long ruleId = (Long) props.get("ruleId");
		Long resourceId = (Long) props.get("resourceId");
		Long lesserStartTime = (Long) props.get("startTime");
		Long greaterEndTime = (Long) props.get("endTime");	
		
		WorkflowRuleHistoricalLoggerContext parentRuleResourceLoggerContext = WorkflowRuleHistoricalLoggerUtil.getWorkflowRuleHistoricalLoggerById(parentRuleResourceLoggerId);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(Type.READING_ALARM);
		FacilioModule eventModule = modBean.getModule(moduleName);
		
		SelectRecordsBuilder<ReadingEventContext> selectEventbuilder = new SelectRecordsBuilder<ReadingEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(eventModule)
				.beanClass(ReadingEventContext.class)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN))
				.orderBy("CREATED_TIME");
	
		List<ReadingEventContext> completeEvents = selectEventbuilder.get();
		
		if (!completeEvents.isEmpty())
		{
			FacilioContext context = new FacilioContext();
			context.put(EventConstants.EventContextNames.EVENT_LIST, completeEvents);
			FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain();
			addEvent.execute(context);
				
			Integer alarmOccurrenceCount = (Integer) context.get(FacilioConstants.ContextNames.ALARM_COUNT);
			if(alarmOccurrenceCount != null)
			{
				parentRuleResourceLoggerContext.setAlarmCount(alarmOccurrenceCount);
			}
		}
		
		parentRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		parentRuleResourceLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.RESOLVED.getIntVal());
		WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(parentRuleResourceLoggerContext);		
	}
}