package com.facilio.wmsv2.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleRecordRelationshipContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class AddOneTimeJobForScheduledRuleHandler extends BaseHandler{

    private static final Logger LOGGER = LogManager.getLogger(AddOneTimeJobForScheduledRuleHandler.class.getName());

    public void processOutgoingMessage(Message message) {

        try {
            String moduleName = (String) message.getContent().get("moduleName");
            HashMap<String,Object> record = (HashMap<String,Object>) message.getContent().get("record");
            List<Long> ruleIds = (List<Long>) message.getContent().get("ruleIds");
            String eventType = (String) message.getContent().get("eventType");

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(record == null || module == null){
                return;
            }

            List<? extends WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(ruleIds,true,true);

            if(CollectionUtils.isEmpty(rules)){
                LOGGER.info("No rules found for "+ module.getName() + " with id :" + record.get("id"));
                return;
            }

            if(eventType.equals(WorkflowRuleRecordRelationshipContext.EventType.CREATE.getName())){
                addOneTimeJobForScheduledRuleOnCreation(rules,record);
            }else if(eventType.equals(WorkflowRuleRecordRelationshipContext.EventType.PATCH.getName())){
                addOneTimeJobForScheduledRuleOnUpdate(rules,record,module.getModuleId(),ruleIds);
            }

        } catch (Exception e) {
            LOGGER.error("Error Occured while adding Job: " + e);
        }
    }

    public static void addOneTimeJobForScheduledRuleOnCreation(List<? extends WorkflowRuleContext> rules,HashMap<String,Object> recordMap) throws Exception {
        for (WorkflowRuleContext rule : rules) {
            Long dateFieldValue = (Long) recordMap.get(rule.getDateField().getName());

            if (dateFieldValue == null) {
                continue;
            }

            Long executionTime = WorkflowRuleAPI.getOneTimeRuleExecutionTime(rule, dateFieldValue);
            executionTime = rule.validatedExecutionTime(executionTime);

            boolean isValidExecutionTime = executionTime > (System.currentTimeMillis() / 1000);
            if (isValidExecutionTime && ((dateFieldValue / 1000) < rule.getRuleEndTime() || rule.getRuleEndTime() == -1)) {
                Long recordId = ((Number) recordMap.get("id")).longValue();
                Long id = WorkflowRuleAPI.addScheduledRuleRecordRelation(recordId, rule, dateFieldValue, executionTime);
                if (id != null) {
                    LOGGER.info("Added one time job for Id " + id);
                }
            }
        }
    }

    public static void addOneTimeJobForScheduledRuleOnUpdate(List<? extends WorkflowRuleContext> rules,HashMap<String,Object> recordMap, Long moduleId, List<Long> ruleIds) throws Exception {
        List<HashMap<String, Object>> rulesToBeAdded = new ArrayList<>();
        List<WorkflowRuleRecordRelationshipContext> rulesToBeDeleted = new ArrayList<>();
        Long recordId = ((Number) recordMap.get("id")).longValue();

        List<WorkflowRuleRecordRelationshipContext> relationList = WorkflowRuleAPI.getRuleFromRuleAndRecordRelationshipTable(
                Collections.singletonList(recordId), moduleId, ruleIds);

        HashMap<Long,WorkflowRuleRecordRelationshipContext> relMap= new HashMap<>();
        for(WorkflowRuleRecordRelationshipContext rel: relationList){
            relMap.put(rel.getRuleId(),rel);
        }

        for (WorkflowRuleContext rule : rules) {
            HashMap<String, Object> ruleMap = new HashMap<>();
            Long dateFieldValue = (Long) recordMap.get(rule.getDateField().getName());
            Long executionTime = WorkflowRuleAPI.getOneTimeRuleExecutionTime(rule, dateFieldValue);
            executionTime = rule.validatedExecutionTime(executionTime);

            ruleMap.put("executionTime", executionTime);
            ruleMap.put("dateFieldValue", dateFieldValue);
            ruleMap.put("id", rule.getId());
            ruleMap.put("moduleId", rule.getModuleId());

            WorkflowRuleRecordRelationshipContext relRecord = relMap.get(rule.getId());
            boolean isInValidExecution = executionTime != null && executionTime < (System.currentTimeMillis() / 1000);

            long ruleEndTime = rule.getRuleEndTime();
            boolean isDateFieldExecutionTimeValid = dateFieldValue != null && ((dateFieldValue / 1000) < ruleEndTime || ruleEndTime == -1);

            if (relRecord == null && isDateFieldExecutionTimeValid && !isInValidExecution) {
                rulesToBeAdded.add(ruleMap);
                continue;
            }

            if (relRecord != null) {
                boolean isRecordDateFieldInvalid = relRecord.getDateFieldValue() != null && (dateFieldValue == null || dateFieldValue < 0);

                if (isInValidExecution || isRecordDateFieldInvalid || !isDateFieldExecutionTimeValid) {
                    rulesToBeDeleted.add(relRecord);
                    continue;
                }

                if (!relRecord.getDateFieldValue().equals(dateFieldValue)) {
                    rulesToBeAdded.add(ruleMap);
                    rulesToBeDeleted.add(relRecord);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(rulesToBeDeleted)) {
            int deletedRecord = WorkflowRuleAPI.deleteRuleFromRuleAndRecordRelationshipTable(rulesToBeDeleted);
            LOGGER.info("Deleted job count" + deletedRecord);
        }

        if (CollectionUtils.isNotEmpty(rulesToBeAdded)) {
            addOneTimeJobForUpdatedRuleField(rulesToBeAdded, recordId);
        }
    }

    public static void addOneTimeJobForUpdatedRuleField(List<HashMap<String,Object>> rules,Long recordId) throws Exception {
        for(HashMap<String,Object> rule : rules) {
            Long dateFieldValue = (Long) rule.get("dateFieldValue");
            Long executionTime = (Long) rule.get("executionTime");

            WorkflowRuleContext ruleContext = new WorkflowRuleContext();
            ruleContext.setModuleId((Long) rule.get("moduleId"));
            ruleContext.setId((Long) rule.get("id"));

            Long id = WorkflowRuleAPI.addScheduledRuleRecordRelation(recordId, ruleContext, dateFieldValue, executionTime);
            if (id != null) {
                LOGGER.info("Added one time job for Id " + id);
            }
        }
    }
}
