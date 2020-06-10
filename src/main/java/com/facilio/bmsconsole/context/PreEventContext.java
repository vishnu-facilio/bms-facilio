package com.facilio.bmsconsole.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.SaveAlarmAndEventsCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateRange;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import java.util.*;

public class PreEventContext extends BaseEventContext {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PreEventContext.class.getName());
    private List<ReadingEventContext> readingEvent;
    public void addReadingEvent(ReadingEventContext baseEvent) {
        if (readingEvent == null) {
            readingEvent = new ArrayList<>();
        }
        readingEvent.add(baseEvent);
    }

    @Override
    public String constructMessageKey() {
        if (getResource() != null && getRule() != null) {
            return rule.getId() + "_" + getResource().getId() + "_" + getEventType();
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new PreAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);
        PreAlarmContext preAlarm = (PreAlarmContext) baseAlarm;

        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        preAlarm.setReadingAlarmCategory(readingAlarmCategory);

        preAlarm.setRule(rule);
        preAlarm.setSubRule(subRule);
        preAlarm.setReadingFieldId(readingFieldId);
        return baseAlarm;
    }

    @Override
    public void setBaseAlarm(BaseAlarmContext baseAlarm) {
        super.setBaseAlarm(baseAlarm);
        if (readingEvent != null) {
            for (ReadingEventContext event: readingEvent) {
                baseAlarm.addAdditionalEvent(event);
            }
        }
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new PreAlarmOccurrenceContext();
        }

        PreAlarmOccurrenceContext preOccurrence = (PreAlarmOccurrenceContext) alarmOccurrence;
        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        preOccurrence.setReadingAlarmCategory(readingAlarmCategory);

        preOccurrence.setRule(rule);
        preOccurrence.setSubRule(subRule);
        preOccurrence.setReadingFieldId(readingFieldId);
        evaluatePreEvent(preOccurrence, context);
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    private ReadingAlarmCategoryContext readingAlarmCategory;
    public ReadingAlarmCategoryContext getReadingAlarmCategory() {
        return readingAlarmCategory;
    }
    public void setReadingAlarmCategory(ReadingAlarmCategoryContext readingAlarmCategory) {
        this.readingAlarmCategory = readingAlarmCategory;
    }

    private ReadingRuleContext rule;
    public ReadingRuleContext getRule() {
        return rule;
    }
    public void setRule(ReadingRuleContext rule) {
        this.rule = rule;
    }

    private ReadingRuleContext subRule;
    public ReadingRuleContext getSubRule() {
        return subRule;
    }
    public void setSubRule(ReadingRuleContext subRule) {
        this.subRule = subRule;
    }

    private long readingFieldId = -1;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }

    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.PRE_ALARM;
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setRuleId(long ruleId) {
        if (ruleId > 0) {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(ruleId);
            setRule(ruleContext);
        }
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setSubRuleId(long subRuleId) {
        if (subRuleId > 0) {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(subRuleId);
            setSubRule(ruleContext);
        }
    }

    private JSONObject obj;
    public JSONObject getObj() {
        return obj;
    }
    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    public Map<String, ReadingDataMeta> getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Map<String, ReadingDataMeta> previousValue) {
        this.previousValue = previousValue;
    }

    private Map<String, ReadingDataMeta> previousValue;

    private ReadingContext readingContext;
    public ReadingContext getReadingContext() {
        return readingContext;
    }
    public void setReadingContext(ReadingContext readingContext) {
        this.readingContext = readingContext;
    }

    private static final int OVER_PERIOD_BUFFER = 5 * 60 * 1000;
    public void evaluatePreEvent(PreAlarmOccurrenceContext preAlarmOccurrence, Context context) throws  Exception {
        ReadingRuleContext rule = getSubRule();
       //  String key = rule.getRuleGroupId()+ "_"+ getResource().getId() + "_" + BaseAlarmContext.Type.PRE_ALARM.getIndex();
        if (preAlarmOccurrence != null ) {
            if (getSeverity().getSeverity().equals("Clear")) {
                createClearReadingEvent(this, preAlarmOccurrence);
                preAlarmOccurrence.setPreEvents(null);
            }
            else {
                if (rule.getThresholdType() == ReadingRuleContext.ThresholdType.FLAPPING.getValue()) {
                    evaluateFlapping();
                } else if (rule.getOverPeriod() != -1 && rule.getOccurences() == -1) {
                    long periodDiff = getCreatedTime() - (long) preAlarmOccurrence.getLastOccurredTime();
                    if (periodDiff < ((rule.getOverPeriod() * 1000) + OVER_PERIOD_BUFFER)) {
                        createReadingEvent(this, preAlarmOccurrence);
                    }
                } else if (rule.getOverPeriod() != -1 && rule.getOccurences() != -1) {
                    List<BaseEventContext> event = NewAlarmAPI.getActiveEventforOccurrence(preAlarmOccurrence);
                    long periodDiff = getCreatedTime() - (long) preAlarmOccurrence.getCreatedTime();
                    if ((periodDiff < (rule.getOverPeriod() * 1000) + OVER_PERIOD_BUFFER)) {
                        if (preAlarmOccurrence.getNoOfEvents() + 1 == rule.getOccurences()) {
                            addReadingEventFromPreEvent(preAlarmOccurrence.getPreEvents(), (ReadingContext) readingContext);
                            createReadingEvent(this, preAlarmOccurrence);
                        } else if (preAlarmOccurrence.getNoOfEvents() + 1 >= rule.getOccurences()) {
                            createReadingEvent(this, preAlarmOccurrence);
                        }
                    }
                } else if (rule.isConsecutive() && rule.getOccurences() != -1) { // done
                    if (preAlarmOccurrence.getNoOfEvents() + 1 == rule.getOccurences()) {
                        addReadingEventFromPreEvent(preAlarmOccurrence.getPreEvents(), (ReadingContext) readingContext);
                        createReadingEvent(this, preAlarmOccurrence);
                    } else if (preAlarmOccurrence.getNoOfEvents() + 1 >= rule.getOccurences()) {
                        createReadingEvent(this, preAlarmOccurrence);
                    }
                }
                preAlarmOccurrence.addPreEvent(this);
            }
        }
    }
    public void evaluateFlapping() throws  Exception {
           LOGGER.log(Level.INFO, "flapping Rule" + rule + "readingContext" + readingContext);
            String key = rule.getId()+ "_"+ readingContext.getParentId() + "_" + BaseAlarmContext.Type.PRE_ALARM.getIndex();
            PreAlarmOccurrenceContext alarmOccurrence = (PreAlarmOccurrenceContext) NewAlarmAPI.getLatestAlarmOccurance(key);
            long flapCount = 0;
            if (alarmOccurrence != null) {
                List<BaseEventContext> events = NewAlarmAPI.getActiveEventforOccurrence(alarmOccurrence);
                flapCount = alarmOccurrence.getNoOfEvents();
                if (rule.getFlapInterval() > 0) {
                    for(BaseEventContext event : events) {
                        if (readingContext.getTtime() - (long) event.getCreatedTime() > rule.getFlapInterval()) {
                            flapCount--;
                        }
                    }
                }
                if (flapCount + 1 == rule.getFlapFrequency()) {
                    addReadingEventFromPreEvent(alarmOccurrence.getPreEvents(), (ReadingContext) readingContext);
                    createReadingEvent(this, alarmOccurrence);
                }
               else if (flapCount + 1 >= rule.getFlapFrequency()) {
                    createReadingEvent(this, alarmOccurrence);
               }
            }
    }


    public void constructAndAddPreClearEvent(Context context) throws Exception {
        Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
        if (isHistorical == null) {
            isHistorical = false;
        }
        Map<Long, ReadingRuleAlarmMeta> metaMap = null;
        if (isHistorical) {
        	
        	BaseEventContext previousBaseEventMeta = (BaseEventContext)context.get(EventConstants.EventContextNames.PREVIOUS_EVENT_META);
			if(previousBaseEventMeta != null && previousBaseEventMeta instanceof PreEventContext) 
			{
				PreEventContext previousEventMeta = (PreEventContext)previousBaseEventMeta;
	            if (previousEventMeta != null && previousEventMeta.getSeverityString() != null && !previousEventMeta.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
	                this.setMessage(previousEventMeta.getEventMessage());
	                context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(this));
	            }
			}
			else if(rule != null && readingContext != null){
		        LOGGER.log(Level.INFO, " PreviousEvent Meta not a Preevent for rule " + rule.getId() + "resource" + readingContext.getParentId());
			}
            
        }
        else {
            metaMap = fetchPreAlarmMeta(rule);
        }
        ReadingRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(getResource().getId()) : null;
        if (alarmMeta != null && !alarmMeta.isClear()) {
            alarmMeta.setClear(true);
            this.setMessage(alarmMeta.getSubject());
            context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(this));
            if (!isHistorical) {
                FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(false);
                FacilioContext addEventContext = addEvent.getContext();
                addEventContext.put(EventConstants.EventContextNames.EVENT_LIST, context.get(EventConstants.EventContextNames.EVENT_LIST));
                addEventContext.put(EventConstants.EventContextNames.EVENT_RULE_LIST, context.get(EventConstants.EventContextNames.EVENT_RULE_LIST));
                addEvent.execute();
            }
        }
    }

    private void createReadingEvent (PreEventContext preEvent, PreAlarmOccurrenceContext alarmOccurrenceContext) throws Exception {
        ReadingEventContext readingEvent = constructReadingEventFromPreEvent(this);
        readingEvent.setSeverity(AlarmAPI.getAlarmSeverity(getSeverityString()));
        alarmOccurrenceContext.setReadingEventCreated(true);
        addReadingEvent(readingEvent);
    }
    private void createClearReadingEvent (PreEventContext preContext, PreAlarmOccurrenceContext preAlarmOccurrence) throws Exception {
        if (preAlarmOccurrence.getReadingEventCreated() != null  && preAlarmOccurrence.getReadingEventCreated()) {
            ReadingEventContext clearEvent =  constructClearReadingEventFromPreEvent(this);
            clearEvent.setSeverity(AlarmAPI.getAlarmSeverity(getSeverityString()));
            addReadingEvent(clearEvent);
        }
    }

    private static Map<Long, ReadingRuleAlarmMeta> fetchPreAlarmMeta (ReadingRuleContext rule) throws Exception {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PRE_ALARM);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            Map<Long, ReadingRuleAlarmMeta> metaMap = new HashMap<>();

            List<PreAlarmContext> readingAlarms = new SelectRecordsBuilder<PreAlarmContext>()
                    .select(fields)
                    .beanClass(PreAlarmContext.class)
                    .moduleName(FacilioConstants.ContextNames.PRE_ALARM)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(rule.getId()), PickListOperators.IS))
                    .fetchSupplement((LookupField) fieldMap.get("severity"))
                    .get();

            if (CollectionUtils.isNotEmpty(readingAlarms)) {
                for (PreAlarmContext alarm : readingAlarms) {
                    ReadingRuleAlarmMeta alarmMeta = constructPreAlarmMeta(alarm.getId(), alarm.getResource(), rule, alarm.getSeverity().getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY), alarm.getSubject());
                    metaMap.put(alarmMeta.getResourceId(), alarmMeta);
                }
            }
            return metaMap;
    }
    private static ReadingRuleAlarmMeta constructPreAlarmMeta(long alarmId, ResourceContext resource, ReadingRuleContext rule, boolean isClear, String subject) {

        ReadingRuleAlarmMeta meta = new ReadingRuleAlarmMeta();
        meta.setOrgId(AccountUtil.getCurrentOrg().getId());
        meta.setAlarmId(alarmId);
        meta.setRuleGroupId(rule.getId());
        meta.setResourceId(resource.getId());
        meta.setResource(resource);
        meta.setReadingFieldId(rule.getReadingFieldId());
        meta.setClear(isClear);
        if(!StringUtils.isEmpty(subject)) {
            meta.setSubject(subject);
        }
        return meta;

    }


    private void addReadingEventFromPreEvent (List<PreEventContext> preEventContext, ReadingContext readingContext) throws Exception {

        for (PreEventContext preEvent : preEventContext) {
            ReadingEventContext readingEvent = constructReadingEventFromPreEvent(preEvent);
            addReadingEvent(readingEvent);
        }
    }

    private ReadingEventContext constructReadingEventFromPreEvent (PreEventContext preEvent) throws Exception {

            ReadingEventContext readingEvent = new ReadingEventContext();
            readingEvent.setMessage(preEvent.getMessage());
            readingEvent.setCreatedTime(preEvent.getCreatedTime());
            readingEvent.setResource(preEvent.getResource());
            readingEvent.setDescription(preEvent.getDescription());
            readingEvent.setSiteId(preEvent.getSiteId());
            readingEvent.setDescription(preEvent.getDescription());
            readingEvent.setReadingFieldId(preEvent.getReadingFieldId());
            AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(preEvent.getSeverity().getId());
            readingEvent.setSeverity(alarmSeverity);
            readingEvent.setRule(preEvent.getRule());
            if (preEvent.getAdditionalInfoJsonStr() != null) {
                readingEvent.setAdditionalInfoJsonStr(preEvent.getAdditionalInfoJsonStr());
            }
//            readingEvent.setAdditiona(preEvent.getAdditionalInfoJsonStr());
            readingEvent.setSubRule(preEvent.getSubRule());
            getFaultTypeFromReadingRule(readingEvent,preEvent);
            
            return  readingEvent;

    }

    public ReadingEventContext constructClearReadingEventFromPreEvent(PreEventContext preEventContext) throws Exception {
        ReadingEventContext event = new ReadingEventContext();
        event.setResource(preEventContext.getResource());
        event.setReadingFieldId(preEventContext.getReadingFieldId());
        event.setRule(preEventContext.getRule());
        event.setSubRule(preEventContext.getSubRule());
        event.setEventMessage(preEventContext.getMessage());
        event.setComment("System auto cleared Alarm because associated rule executed clear condition for the associated resource");
        event.setCreatedTime(preEventContext.getCreatedTime());
        event.setAutoClear(true);
        event.setSiteId(preEventContext.getSiteId());
        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
        getFaultTypeFromReadingRule(event,preEventContext);
        
        return event;
    }
    
    private void getFaultTypeFromReadingRule(ReadingEventContext readingEvent, PreEventContext preEvent) throws Exception {
    	 if(preEvent.getSubRule() != null) {
        	 if(preEvent.getSubRule().getFaultTypeEnum() == null) {
        		 if(preEvent.getSubRule().getId() != -1) {
            		 WorkflowRuleContext preEventRule = WorkflowRuleAPI.getWorkflowRule(preEvent.getSubRule().getId(),false,true);
            		 if(preEventRule != null && preEventRule instanceof ReadingRuleContext) {
            			ReadingRuleContext preEventReadingRule = (ReadingRuleContext) preEventRule;
            			if(preEventReadingRule != null && preEventReadingRule.getFaultTypeEnum() != null) {
                         	readingEvent.setFaultType(preEventReadingRule.getFaultTypeEnum());
            			}
            		 }
        		 }
        	 }
        	 else {
             	readingEvent.setFaultType(preEvent.getSubRule().getFaultTypeEnum()); 
        	 }
        }
    }
}
