package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class SLAWorkflowCommitmentRuleContext extends WorkflowRuleContext {

    private List<SLAEntityDuration> slaEntities;
    public List<SLAEntityDuration> getSlaEntities() {
        return slaEntities;
    }
    public void setSlaEntities(List<SLAEntityDuration> slaEntities) {
        this.slaEntities = slaEntities;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        if (moduleRecord.getSlaPolicyId() != getParentRuleId()) {
            return false;
        }

        return super.evaluateMisc(moduleName, record, placeHolders, context);
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (CollectionUtils.isEmpty(slaEntities)) {
            slaEntities = SLAWorkflowAPI.getSLAEntitiesForCommitment(getId());
        }

        if (CollectionUtils.isNotEmpty(slaEntities)) {
            SLAPolicyContext slaPolicy = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(getParentRuleId());
            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = slaPolicy.getEscalations();
            Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap = null;
            if (CollectionUtils.isNotEmpty(escalations)) {
                escalationMap = new HashMap<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : escalations) {
                    escalationMap.put(escalation.getSlaEntityId(), escalation);
                }
            }

            FacilioModule module = modBean.getModule(getModuleId());
            for (SLAEntityDuration slaEntityDuration : slaEntities) {
                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(slaEntityDuration.getSlaEntityId());

                FacilioField baseField = modBean.getField(slaEntity.getBaseFieldId());
                FacilioField dueField = modBean.getField(slaEntity.getDueFieldId());

                Long timeValue = (Long) FieldUtil.getValue(moduleRecord, baseField);
                if (timeValue == null) {
                    continue;
                }
                timeValue += slaEntityDuration.getAddDuration() * 1000;

                Long oldTime = (Long) FieldUtil.getValue(moduleRecord, dueField);
                addSLATriggeredActivity(context, moduleRecord, slaPolicy, oldTime, timeValue);
                FieldUtil.setValue(moduleRecord, dueField, timeValue);

                UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(Collections.singletonList(dueField))
                        .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
                update.update(moduleRecord);

                if (MapUtils.isNotEmpty(escalationMap)) {
                    SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(slaEntityDuration.getSlaEntityId());
                    if (slaPolicyEntityEscalationContext != null && CollectionUtils.isNotEmpty(slaPolicyEntityEscalationContext.getLevels())) {
                        slaPolicyEntityEscalationContext.setLevels(SLAWorkflowAPI.getEscalations(slaPolicy.getId(), slaPolicyEntityEscalationContext.getSlaEntityId()));
                        addEscalationJobs(slaPolicyEntityEscalationContext.getLevels(), module, dueField, slaEntity.getCriteria(), moduleRecord);
                    }
                }
            }
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    private void addEscalationJobs(List<SLAWorkflowEscalationContext> escalations, FacilioModule module, FacilioField dueField, Criteria criteria, ModuleBaseWithCustomFields moduleRecord) throws Exception {
        if (CollectionUtils.isNotEmpty(escalations)) {
            SLAWorkflowAPI.getActions(escalations);
            int count = 0;
            for (SLAWorkflowEscalationContext escalation : escalations) {
                count++;

                WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
                workflowRuleContext.setName(getName() + "_Escalation_" + count);
                workflowRuleContext.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
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
                actions.add(getDefaultSLAEscalationTriggeredAction(count));
                recordRuleContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
                recordRuleChain.execute();
            }
        }
    }

    private void addSLATriggeredActivity(Context context, ModuleBaseWithCustomFields record, SLAPolicyContext slaPolicy, Long oldDate, Long newDate) throws Exception {
        JSONObject infoJson = new JSONObject();
        infoJson.put("name", slaPolicy.getName());
        infoJson.put("oldDate", oldDate);
        infoJson.put("newDate", newDate);

        CommonCommandUtil.addActivityToContext(record.getId(), System.currentTimeMillis(), WorkOrderActivityType.SLA_ACTIVATED, infoJson,
                (FacilioContext) context);
    }

    private ActionContext getDefaultSLAEscalationTriggeredAction(int count) {
        ActionContext actionContext = new ActionContext();
        actionContext.setActionType(ActionType.ACTIVITY_FOR_MODULE_RECORD);
        JSONObject json = new JSONObject();
        json.put("activityType", WorkOrderActivityType.SLA_ESCALATION_TRIGGERED.getValue());
        JSONObject infoJson = new JSONObject();
        infoJson.put("stage", count);
        infoJson.put("message", "Stage " + count + " escalation raised");
        json.put("info", infoJson);
        actionContext.setTemplateJson(json);
        actionContext.setStatus(true);
        return actionContext;
    }

    public static class SLAEntityDuration {
        private long slaEntityId = -1;
        public long getSlaEntityId() {
            return slaEntityId;
        }
        public void setSlaEntityId(long slaEntityId) {
            this.slaEntityId = slaEntityId;
        }

        private long slaCommitmentId = -1;
        public long getSlaCommitmentId() {
            return slaCommitmentId;
        }
        public void setSlaCommitmentId(long slaCommitmentId) {
            this.slaCommitmentId = slaCommitmentId;
        }

        private long addDuration = -1;
        public long getAddDuration() {
            return addDuration;
        }
        public void setAddDuration(long addDuration) {
            this.addDuration = addDuration;
        }
    }
}
