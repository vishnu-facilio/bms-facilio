package com.facilio.bmsconsole.instant.jobs;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.AddOrUpdateSLABreachJobCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class AddSLAEscalationJob extends InstantJob {

    @Override
    public void execute(FacilioContext context) throws Exception {
        Long parentRuleId = (Long) context.get(FacilioConstants.ContextNames.PARENT_RULE_ID);
        List<SLAWorkflowEscalationContext> escalations = (List<SLAWorkflowEscalationContext>) context.get(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST);
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        FacilioField dueField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.MODULE_DATA);
        SLAEntityContext slaEntity = (SLAEntityContext) context.get(FacilioConstants.ContextNames.SLA_ENTITY);

        addSLAEscalationJobs(parentRuleId, escalations, module, dueField, criteria, moduleRecord, slaEntity);
    }

    public static void addSLAEscalationJobs(Long parentRuleId, List<SLAWorkflowEscalationContext> escalations,
                                            FacilioModule module, FacilioField dueField, Criteria criteria,
                                            ModuleBaseWithCustomFields moduleRecord, SLAEntityContext slaEntity) throws Exception {
        if (CollectionUtils.isNotEmpty(escalations)) {
            AddOrUpdateSLABreachJobCommand.deleteAllExistingSLASingleRecordJob(Collections.singletonList(moduleRecord), "_Escalation_", StringOperators.CONTAINS, module);
            if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion()) && AccountUtil.getCurrentOrg().getOrgId() == 583) {
                try {
                    LOGGER.info("SLA Escalation Job Entry - " + "\n SLA Entity - " + slaEntity.getName() + "\n Record Id - " + moduleRecord.getId() + "\n Module Name - " + module.getName());
                }catch (Exception e){

                }
            }
            int count = 0;
            for (SLAWorkflowEscalationContext escalation : escalations) {
                count++;

                Long value = (Long) FieldUtil.getValue(moduleRecord, dueField);
                if (value == null) {
                    // don't assign any escalations
                    if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion()) && AccountUtil.getCurrentOrg().getOrgId() == 583) {
                        try {
                            LOGGER.info("SLA Escalation Job Entry Field Value null - " + "\n SLA Entity - " + slaEntity.getName() + "\n Record Id - " + moduleRecord.getId() + "\n Module Name - " + module.getName());
                        }catch (Exception e){

                        }
                    }
                    continue;
                }

                value = value + (escalation.getInterval() * 1000);
                if (!FacilioUtil.isEmptyOrNull(value) && (value) < System.currentTimeMillis()) {
                    if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion()) && AccountUtil.getCurrentOrg().getOrgId() == 583) {
                        try {
                            LOGGER.info("SLA Escalation Job Entry Value < current time - " + "\n SLA Entity - " + slaEntity.getName() + "\n Record Id - " + moduleRecord.getId() + "\n Module Name - " + module.getName() + "\n Value - " + value);
                        }catch (Exception e){

                        }
                    }
                    continue;
                }

                WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
                workflowRuleContext.setName(slaEntity.getName() + "_Escalation_" + count);
                workflowRuleContext.setRuleType(WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE);
                workflowRuleContext.setActivityType(EventType.SCHEDULED);
                workflowRuleContext.setModule(module);
                workflowRuleContext.setParentId(moduleRecord.getId());

                workflowRuleContext.setCriteria(criteria);
                workflowRuleContext.setDateFieldId(dueField.getFieldId());

                workflowRuleContext.setInterval(escalation.getInterval());
                workflowRuleContext.setScheduleType(escalation.getTypeEnum());

                FacilioChain recordRuleChain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
                FacilioContext recordRuleContext = recordRuleChain.getContext();
                recordRuleContext.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
                List<ActionContext> actions = escalation.getActions();
                if (actions == null) {
                    actions = new ArrayList<>();
                }
                actions.add(getDefaultSLAEscalationTriggeredAction(count, slaEntity));
                recordRuleContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
                recordRuleChain.execute();

                GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getSLAEscalationWorkflowRuleRelModule().getTableName())
                        .fields(FieldFactory.getSLAEscalationWorkflowRuleRelFields());
                Map<String, Object> map = new HashMap<>();
                map.put("slaPolicyId", parentRuleId);
                map.put("workflowRuleId", workflowRuleContext.getId());
                insertRecordBuilder.insert(map);
            }
        }
    }

    private static ActionContext getDefaultSLAEscalationTriggeredAction(int count, SLAEntityContext slaEntity) {
        ActionContext actionContext = new ActionContext();
        actionContext.setActionType(ActionType.ACTIVITY_FOR_MODULE_RECORD);
        JSONObject json = new JSONObject();
        json.put("activityType", WorkOrderActivityType.SLA_ESCALATION_TRIGGERED.getValue());
        JSONObject infoJson = new JSONObject();
        infoJson.put("stage", count);
        infoJson.put("slaEntity", slaEntity.getName());
        infoJson.put("message", "Stage " + count + " escalation raised");
        json.put("info", infoJson);
        actionContext.setTemplateJson(json);
        actionContext.setStatus(true);
        return actionContext;
    }
}
