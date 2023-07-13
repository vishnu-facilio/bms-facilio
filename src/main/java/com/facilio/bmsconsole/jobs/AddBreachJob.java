package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.AddOrUpdateSLABreachJobCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class AddBreachJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(AddBreachJob.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {


        long jobId = jobContext.getJobId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(jobId, ModuleFactory.getSLABreachJobExecution()));
        SLABreachJobExecution slaBreachJobExecution = SLAWorkflowAPI.getSLABreachJobExecution(criteria);

        if (slaBreachJobExecution == null){
            return;
        }

        long recordId = slaBreachJobExecution.getRecordId();
        FacilioModule module = modBean.getModule(slaBreachJobExecution.getModuleId());
        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getRecord(module, recordId);

        if (record == null){
            return;
        }

        long slaPolicyId = record.getSlaPolicyId();

        long entityId = slaBreachJobExecution.getSlaEntityId();
        SLAEntityContext currentSlaEntity = SLAWorkflowAPI.getSLAEntity(entityId);
        Criteria slaEntityCriteria = currentSlaEntity.getCriteria();
        boolean criteriaEvaluate = true;

        if (slaEntityCriteria != null) {
            criteriaEvaluate = slaEntityCriteria.computePredicate().evaluate(record);
        }
        if (!criteriaEvaluate) {
            return;
        }

        addActivityForBreachedRecords(module, currentSlaEntity, record);

        try {
            LOGGER.debug("Breach Job --------- \n" + "SLA breach job relation Entry " + slaBreachJobExecution);
        }catch (Exception e){

        }

        if (slaPolicyId > 0) {

            SLAPolicyContext slaPolicy = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(slaPolicyId);
            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = slaPolicy.getEscalations();

            Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap = null;
            if (CollectionUtils.isNotEmpty(escalations)) {
                escalationMap = new HashMap<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : escalations) {
                    escalationMap.put(escalation.getSlaEntityId(), escalation);
                }
            }

            FacilioField dueField = modBean.getField(currentSlaEntity.getDueFieldId());

            if (MapUtils.isNotEmpty(escalationMap)) {
                SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(currentSlaEntity.getId());
                if (slaPolicyEntityEscalationContext != null && CollectionUtils.isNotEmpty(slaPolicyEntityEscalationContext.getLevels())) {
                    slaPolicyEntityEscalationContext.setLevels(SLAWorkflowAPI.getEscalations(slaPolicy.getId(), slaPolicyEntityEscalationContext.getSlaEntityId()));
                    addSLAEscalationJobs(slaPolicyId, slaPolicyEntityEscalationContext.getLevels(), module, dueField, currentSlaEntity.getCriteria(), record, currentSlaEntity);
                }
            }

        }
    }

    public static void addSLAEscalationJobs(Long parentRuleId, List<SLAWorkflowEscalationContext> escalations,
                                            FacilioModule module, FacilioField dueField, Criteria criteria,
                                            ModuleBaseWithCustomFields moduleRecord, SLAEntityContext slaEntity) throws Exception {
        if (CollectionUtils.isNotEmpty(escalations)) {
            AddOrUpdateSLABreachJobCommand.deleteAllExistingSLASingleRecordJob(Collections.singletonList(moduleRecord), "SLAEntity_" + slaEntity.getId() + "_Escalation_", StringOperators.STARTS_WITH, module);

            int count = 0;

            Long value = (Long) FieldUtil.getValue(moduleRecord, dueField);

            if (value == null) {
                return;
            }

            for (SLAWorkflowEscalationContext escalation : escalations) {
                count++;

                WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
                workflowRuleContext.setName("SLAEntity_" + slaEntity.getId() + "_Escalation_" + count);
                workflowRuleContext.setRuleType(WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE);
                workflowRuleContext.setActivityType(EventType.SCHEDULED);
                workflowRuleContext.setModule(module);
                workflowRuleContext.setParentId(moduleRecord.getId());

                workflowRuleContext.setCriteria(criteria);
                workflowRuleContext.setDateFieldId(dueField.getFieldId());
                Long interval = escalation.getInterval();

                if (escalation.getTypeEnum() == WorkflowRuleContext.ScheduledRuleType.ON) {
                    interval = interval+20;
                }
                workflowRuleContext.setInterval(interval);
                workflowRuleContext.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);

                try {
                    LOGGER.debug("Escalation Job rule -------- " + workflowRuleContext);
                }catch (Exception e){

                }

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

    private void addActivityForBreachedRecords(FacilioModule module, SLAEntityContext entity, ModuleBaseWithCustomFields currentRecord) {
        try {
                ModuleBaseWithCustomFields record =  currentRecord;

                FacilioChain chain = TransactionChainFactory.getAddActivitiesCommand();
                FacilioContext activityContext = chain.getContext();

                if (module == null) {
                    throw new IllegalArgumentException("Invalid module");
                }

                activityContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                ActivityContext ac = new ActivityContext();
                ac.setTtime(DateTimeUtil.getCurrenTime());
                ac.setParentId(record.getId());
                ac.setType(WorkOrderActivityType.SLA_MEET);
                JSONObject infoJson = new JSONObject();
                infoJson.put("message", module.getDisplayName() + " overshot its " + entity.getName());
                ac.setInfo(infoJson);
                long orgId = AccountUtil.getCurrentOrg().getId();
                ac.setDoneBy(AccountUtil.getOrgBean().getSuperAdmin(orgId));

                activityContext.put(FacilioConstants.ContextNames.ACTIVITY_LIST, Collections.singletonList(ac));
                chain.execute();
        } catch (Exception ex) {
            LOGGER.error("Exception occurred ", ex);
        }

    }

}