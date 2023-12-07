package com.facilio.ims.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.ScheduleTriggerRecordRelationContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Log4j
public class AddOneTimeJobForScheduleTriggerHandler extends ImsHandler{

    public static final String KEY = "__one_time_job_for_scheduled_Trigger__";

    @Override
    public void processMessage(Message message) {
        try {
            String moduleName = (String) message.getContent().get("moduleName");
            HashMap<String,Object> record = (HashMap<String,Object>) message.getContent().get("record");
            List<Long> triggerIds = (List<Long>) message.getContent().get("triggerIds");
            String eventType = (String) message.getContent().get("eventType");

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(record == null || module == null){
                return;
            }

            List<TriggerFieldRelContext> triggers = TriggerUtil.getTriggersForEventTypeAndModule(triggerIds);

            if(CollectionUtils.isEmpty(triggers)){
                LOGGER.info("No triggers found for "+ module.getName() + " with id :" + record.get("id"));
                return;
            }

            if(eventType.equals(ScheduleTriggerRecordRelationContext.EventType.CREATE.getName())){
                addOneTimeJobForScheduledTriggerOnCreation(triggers,record);
            }else if(eventType.equals(ScheduleTriggerRecordRelationContext.EventType.PATCH.getName())){
                addOneTimeJobForScheduledTriggerOnUpdate(triggers,record,module.getModuleId(),triggerIds);
            }

        } catch (Exception e) {
            LOGGER.error("Error Occured while adding Job: " + e);
        }
    }

    public static void addOneTimeJobForScheduledTriggerOnCreation(List<TriggerFieldRelContext> triggers,HashMap<String,Object> recordMap) throws Exception {
        for (TriggerFieldRelContext trigger : triggers) {
            long fieldId = trigger.getFieldId();
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField dateField = moduleBean.getField(fieldId);
            Long dateFieldValue = (Long) recordMap.get(dateField.getName());

            if (dateFieldValue == null) {
                continue;
            }

            Long executionTime = TriggerUtil.getOneTimeTriggerExecutionTime(trigger, dateFieldValue);
            executionTime = trigger.validatedExecutionTime(executionTime);

            boolean isValidExecutionTime = executionTime > (System.currentTimeMillis() / 1000);
            if (isValidExecutionTime && ((dateFieldValue / 1000) < trigger.getTriggerEndTime() || trigger.getTriggerEndTime() == -1)) {
                Long recordId = ((Number) recordMap.get("id")).longValue();
                Long id = TriggerUtil.addScheduledTriggerRecordRelation(recordId, trigger, dateFieldValue, executionTime);
                if (id != null) {
                    LOGGER.info("Added one time job for Id " + id);
                }
            }
        }
    }

    public static void addOneTimeJobForScheduledTriggerOnUpdate(List<TriggerFieldRelContext> triggers,HashMap<String,Object> recordMap, Long moduleId, List<Long> triggerIds) throws Exception {
        List<HashMap<String, Object>> triggersToBeAdded = new ArrayList<>();
        List<ScheduleTriggerRecordRelationContext> triggersToBeDeleted = new ArrayList<>();
        Long recordId = ((Number) recordMap.get("id")).longValue();

        List<ScheduleTriggerRecordRelationContext> relationList = TriggerUtil.getScheduleTriggerRecordRel(
                Collections.singletonList(recordId), moduleId, triggerIds);

        HashMap<Long,ScheduleTriggerRecordRelationContext> relMap= new HashMap<>();
        for(ScheduleTriggerRecordRelationContext rel: relationList){
            relMap.put(rel.getTriggerId(),rel);
        }

        for (TriggerFieldRelContext trigger : triggers) {

            HashMap<String, Object> triggerMap = new HashMap<>();
            long fieldId = trigger.getFieldId();
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField dateField = moduleBean.getField(fieldId);
            Long dateFieldValue = (Long) recordMap.get(dateField.getName());
            Long executionTime = TriggerUtil.getOneTimeTriggerExecutionTime(trigger, dateFieldValue);
            executionTime = trigger.validatedExecutionTime(executionTime);

            triggerMap.put("executionTime", executionTime);
            triggerMap.put("dateFieldValue", dateFieldValue);
            triggerMap.put("id", trigger.getId());
            triggerMap.put("moduleId", trigger.getModuleId());

            ScheduleTriggerRecordRelationContext relRecord = relMap.get(trigger.getId());
            boolean isInValidExecution = executionTime != null && executionTime < (System.currentTimeMillis() / 1000);

            long triggerEndTime = trigger.getTriggerEndTime();
            boolean isDateFieldExecutionTimeValid = dateFieldValue != null && ((dateFieldValue / 1000) < triggerEndTime || triggerEndTime == -1);

            if (relRecord == null && isDateFieldExecutionTimeValid && !isInValidExecution) {
                triggersToBeAdded.add(triggerMap);
                continue;
            }

            if (relRecord != null) {
                boolean isRecordDateFieldInvalid = relRecord.getDateFieldValue() != null && (dateFieldValue == null || dateFieldValue < 0);

                if (isInValidExecution || isRecordDateFieldInvalid || !isDateFieldExecutionTimeValid) {
                    triggersToBeDeleted.add(relRecord);
                    continue;
                }

                if (!Objects.equals(relRecord.getDateFieldValue(), dateFieldValue)) {
                    triggersToBeAdded.add(triggerMap);
                    triggersToBeDeleted.add(relRecord);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(triggersToBeDeleted)) {
            int deletedRecord = TriggerUtil.deleteTriggerRecordRelationshipTable(triggersToBeDeleted);
            LOGGER.info("Deleted job count" + deletedRecord);
        }

        if (CollectionUtils.isNotEmpty(triggersToBeAdded)) {
            addOneTimeJobForUpdatedTriggerField(triggersToBeAdded, recordId);
        }
    }

    public static void addOneTimeJobForUpdatedTriggerField(List<HashMap<String,Object>> triggers,Long recordId) throws Exception {
        for(HashMap<String,Object> trigger : triggers) {
            Long dateFieldValue = (Long) trigger.get("dateFieldValue");
            Long executionTime = (Long) trigger.get("executionTime");

            TriggerFieldRelContext triggerRel = new TriggerFieldRelContext();
            triggerRel.setModuleId((Long) trigger.get("moduleId"));
            triggerRel.setId((Long) trigger.get("id"));

            Long id = TriggerUtil.addScheduledTriggerRecordRelation(recordId, triggerRel, dateFieldValue, executionTime);
            if (id != null) {
                LOGGER.info("Added one time job for Id " + id);
            }
        }
    }
}
