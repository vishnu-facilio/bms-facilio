package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SLAWorkflowCommitmentRuleContext extends WorkflowRuleContext {

    private List<SLAEntityDuration> slaEntities;
    public List<SLAEntityDuration> getSlaEntities() {
        return slaEntities;
    }
    public void setSlaEntities(List<SLAEntityDuration> slaEntities) {
        this.slaEntities = slaEntities;
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

            for (SLAEntityDuration slaEntityDuration : slaEntities) {
                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(slaEntityDuration.getSlaEntityId());

                FacilioField baseField = modBean.getField(slaEntity.getBaseFieldId());
                FacilioField dueField = modBean.getField(slaEntity.getDueFieldId());
                FacilioField compareField = modBean.getField(slaEntity.getCompareFieldId());

                Long timeValue;
                if (baseField.isDefault()) {
                    timeValue = (Long) PropertyUtils.getProperty(moduleRecord, baseField.getName());
                } else {
                    timeValue = (Long) moduleRecord.getDatum(baseField.getName());
                }
                if (timeValue == null) {
                    timeValue = DateTimeUtil.getCurrenTime();
                }
                timeValue += slaEntityDuration.getAddDuration() * 1000;

                if (dueField.isDefault()) {
                    PropertyUtils.setProperty(moduleRecord, dueField.getName(), timeValue);
                } else {
                    moduleRecord.setDatum(dueField.getName(), timeValue);
                }

                FacilioModule module = modBean.getModule(getModuleId());
                UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(Collections.singletonList(dueField))
                        .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
                update.update(moduleRecord);

                if (MapUtils.isNotEmpty(escalationMap)) {
                    SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(slaEntityDuration.getSlaEntityId());
                    if (CollectionUtils.isNotEmpty(slaPolicyEntityEscalationContext.getEscalations())) {
                        slaPolicyEntityEscalationContext.setEscalations(SLAWorkflowAPI.getEscalations(slaPolicy.getId(), slaPolicyEntityEscalationContext.getSlaEntityId()));
                        addEscalationJobs(slaPolicyEntityEscalationContext.getEscalations(), module, dueField, compareField, moduleRecord);
                    }
                }
            }
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    private void addEscalationJobs(List<SLAWorkflowEscalationContext> escalations, FacilioModule module, FacilioField dueField, FacilioField compareField, ModuleBaseWithCustomFields moduleRecord) throws Exception {
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

                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(compareField, CommonOperators.IS_EMPTY));
                workflowRuleContext.setCriteria(criteria);
                workflowRuleContext.setDateFieldId(dueField.getFieldId());

                workflowRuleContext.setInterval(escalation.getInterval());
                workflowRuleContext.setScheduleType(escalation.getTypeEnum());

                FacilioChain recordRuleChain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
                FacilioContext recordRuleContext = recordRuleChain.getContext();
                recordRuleContext.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
                recordRuleContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, escalation.getActions());
                recordRuleChain.execute();
            }
        }
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
