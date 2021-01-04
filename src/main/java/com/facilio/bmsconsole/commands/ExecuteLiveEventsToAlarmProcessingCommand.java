package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.PreEventContext;
import com.facilio.bmsconsole.context.BaseEventContext.EventProcessingStatus;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ExecuteLiveEventsToAlarmProcessingCommand extends FacilioCommand{
	
	private static final Logger LOGGER = Logger.getLogger(ExecuteLiveEventsToAlarmProcessingCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {		
			long currentTime = DateTimeUtil.getCurrenTime();
			final int EVENTS_FETCH_LIMIT_COUNT = 5000; 
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
			SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
					.select(modBean.getAllFields(eventModule.getName()))
					.module(eventModule)
					.beanClass(BaseEventContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), ""+currentTime, NumberOperators.LESS_THAN_EQUAL))	
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("eventProcessingStatus"), ""+EventProcessingStatus.UNPROCESSED.getIndex(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("isLiveEvent"), ""+true, BooleanOperators.IS));
			
			HashMap<String, AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = new HashMap<String, AlarmOccurrenceContext>();
			List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
			SelectRecordsBuilder.BatchResult<BaseEventContext> batchSelect = selectEventbuilder.getInBatches("CREATED_TIME", EVENTS_FETCH_LIMIT_COUNT);
			
			while(batchSelect.hasNext()) 
			{
				if (baseEvents != null && !baseEvents.isEmpty())
				{
					FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(false);
					addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
					addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, false);
					addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, false);
					addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
					addEvent.execute();
					
					LOGGER.info("Events added in event to alarm processing job Size  -- "+baseEvents.size()+ " baseEvents -- "+baseEvents);				
					lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) addEvent.getContext().get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
				}	
				baseEvents = batchSelect.get();
				baseEvents = NewEventAPI.getExtendedEvent(baseEvents);
				setSeverityPropsForBaseEvents(baseEvents);
			}
			
			//final batch of historical events to proceed with system autoclear
			if (baseEvents != null && !baseEvents.isEmpty())
			{
				baseEvents = NewEventAPI.getExtendedEvent(baseEvents);
				setSeverityPropsForBaseEvents(baseEvents);
				
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(false);
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
				addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, false);
				addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, true);
				addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
				addEvent.execute();
				
				LOGGER.info("Events added in final events to alarm processing job: Size  -- "+baseEvents.size()+ " baseEvents -- "+baseEvents);				
			}	
		}
		catch(Exception e){
			LOGGER.severe("Exception during ExecuteLiveEventsToAlarmProcessingCommand context --: " +context+ " Exception -- " + e);
			throw e;			
		}
		return false;
	}
	
	private void setSeverityPropsForBaseEvents(List<BaseEventContext> baseEvents) throws Exception
	{
		if(baseEvents != null && !baseEvents.isEmpty())
		{
			List<Long> baseEventSeverityIds = new ArrayList<Long>();
			for(BaseEventContext baseEvent :baseEvents) {
				baseEventSeverityIds.add(baseEvent.getSeverity().getId());
			}
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(baseEventSeverityIds);
			
			for(BaseEventContext baseEvent :baseEvents)
			{
				baseEvent.setSeverity(alarmSeverityMap.get(baseEvent.getSeverity().getId()));
				baseEvent.getSeverity().setSeverity(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
				baseEvent.setSeverityString(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
			}				
		}
	}

}
