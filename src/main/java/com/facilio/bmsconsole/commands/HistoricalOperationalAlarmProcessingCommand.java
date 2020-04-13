package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HistoricalOperationalAlarmProcessingCommand extends FacilioCommand implements PostTransactionCommand{
    private static final Logger LOGGER = Logger.getLogger(HistoricalOperationalAlarmProcessingCommand.class.getName());
    private OperationAlarmHistoricalLogsContext parentResourceLoggerContext = null;
    private Long parentLoggerId = null;
    private String exceptionMessage = null;
    private StackTraceElement[] stack = null;
    private int retryCount = 0;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {
            parentLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_OPERATIONAL_ALARM_PROCESSING_JOB_ID);
            parentResourceLoggerContext = OperationAlarmApi.getOperationAlarmHistoricalLoggerById(parentLoggerId);
            Long resourceId = parentResourceLoggerContext.getResourceId();
            Long lesserStartTime = parentResourceLoggerContext.getSplitStartTime();
            Long greaterEndTime = parentResourceLoggerContext.getSplitEndTime();
            fetchAndProcessAllEventsBasedOnAlarmDeletionRange(resourceId, lesserStartTime, greaterEndTime, BaseAlarmContext.Type.OPERATION_ALARM);
            OperationAlarmApi.updateOperationAlarmHistoricalLogger(parentResourceLoggerContext);
        }

        catch (Exception processingException) {
            exceptionMessage = processingException.getMessage();
            stack = processingException.getStackTrace();
            LOGGER.severe("HISTORICAL OPERATIONAL ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentLoggerId+ " context --: "+ " Exception -- " + exceptionMessage + " StackTrace -- " + String.valueOf(stack));
            throw processingException;
        }
        return false;
    }

    private void fetchAndProcessAllEventsBasedOnAlarmDeletionRange(long resourceId, long lesserStartTime, long greaterEndTime, BaseAlarmContext.Type type) throws Exception
    {
        final int EVENTS_FETCH_LIMIT_COUNT = 5000;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = NewEventAPI.getEventModuleName(type);
        FacilioModule eventModule = modBean.getModule(moduleName);
        SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
                .select(modBean.getAllFields(eventModule.getName()))
                .module(eventModule)
                .beanClass(NewEventAPI.getEventClass(type))
                .andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN));

        HashMap<String, AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = new HashMap<String, AlarmOccurrenceContext>();
        List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
        SelectRecordsBuilder.BatchResult<BaseEventContext> batchSelect = selectEventbuilder.getInBatches("CREATED_TIME", EVENTS_FETCH_LIMIT_COUNT);

        while(batchSelect.hasNext())
        {
            if (baseEvents != null && !baseEvents.isEmpty())
            {
                FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
                addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
                addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
                addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, false);
                addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
                addEvent.execute();

                LOGGER.info("Events added in alarm processing job: "+parentLoggerId+"Operational Alarm resource ID: "  +resourceId+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);

                Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
                lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) addEvent.getContext().get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
            }
            baseEvents = batchSelect.get();
            setHistoricalPropsForBaseEvents(baseEvents);
        }

        //final batch of historical events to proceed with system autoclear
        if (baseEvents != null && !baseEvents.isEmpty())
        {
            FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
            addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
            addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
            addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, false);
            addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
            addEvent.execute();
            LOGGER.info("Operational Events added in final alarm processing job: "+parentLoggerId+ " for resource : "+resourceId+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);
        }

    }

    @Override
    public boolean postExecute() throws Exception {
        try {
            parentResourceLoggerContext.setStatus(OperationAlarmHistoricalLogsContext.Status.RESOLVED.getIntVal());
            parentResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
            OperationAlarmApi.updateOperationAlarmHistoricalLogger(parentResourceLoggerContext);
        }
        catch (Exception e) {
            LOGGER.severe("HISTORICAL OPERATIONAL ALARM PROCESSING JOB Post Execute Failed -- "+parentLoggerId+" Exception --  "+e);
        }
       return false;
    }

    public void onError() throws Exception {
        parentResourceLoggerContext.setStatus(OperationAlarmHistoricalLogsContext.Status.FAILED.getIntVal());
        parentResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
        OperationAlarmApi.updateOperationAlarmHistoricalLogger(parentResourceLoggerContext);
    }
    private void setHistoricalPropsForBaseEvents(List<BaseEventContext> baseEvents) throws Exception
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
                if (baseEvent instanceof OperationAlarmEventContext) {
                    OperationAlarmEventContext opEvent = (OperationAlarmEventContext) baseEvent;
                    opEvent.setCoverageType(opEvent.getCoverageType());
                    opEvent.setReadingFieldId(opEvent.getReadingFieldId());
                }
                baseEvent.getSeverity().setSeverity(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
                baseEvent.setSeverityString(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
            }
        }
    }
}

