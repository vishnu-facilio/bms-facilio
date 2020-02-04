package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SLAWorkflowCommitmentRuleContext extends WorkflowRuleContext {

    public SLAWorkflowCommitmentRuleContext() {
        setRuleType(RuleType.SLA_WORKFLOW_RULE);
    }

    @Override
    @JsonInclude
    public int getRuleType() {
        return super.getRuleType();
    }

//    private long slaPoliceRuleId = -1;
//    public long getSlaPoliceRuleId() {
//        return slaPoliceRuleId;
//    }
//    public void setSlaPoliceRuleId(long slaPoliceRuleId) {
//        this.slaPoliceRuleId = slaPoliceRuleId;
//    }

    private long baseFieldId = -1;
    public long getBaseFieldId() {
        return baseFieldId;
    }
    public void setBaseFieldId(long baseFieldId) {
        this.baseFieldId = baseFieldId;
    }

    private long dueFieldId = -1;
    public long getDueFieldId() {
        return dueFieldId;
    }
    public void setDueFieldId(long dueFieldId) {
        this.dueFieldId = dueFieldId;
    }

    private long compareFieldId = -1;
    public long getCompareFieldId() {
        return compareFieldId;
    }
    public void setCompareFieldId(long compareFieldId) {
        this.compareFieldId = compareFieldId;
    }

    private long addDuration = -1;
    public long getAddDuration() {
        return addDuration;
    }
    public void setAddDuration(long addDuration) {
        this.addDuration = addDuration;
    }

    private List<SLAWorkflowEscalationContext> escalations;
    public List<SLAWorkflowEscalationContext> getEscalations() {
        return escalations;
    }
    public void setEscalations(List<SLAWorkflowEscalationContext> escalations) {
        this.escalations = escalations;
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField baseField = modBean.getField(baseFieldId);
        FacilioField dueField = modBean.getField(dueFieldId);
        FacilioField compareField = modBean.getField(compareFieldId);

        Long timeValue;
        if (baseField.isDefault()) {
            timeValue = (Long) PropertyUtils.getProperty(moduleRecord, baseField.getName());
        }
        else {
            timeValue = (Long) moduleRecord.getDatum(baseField.getName());
        }
        if (timeValue == null) {
            timeValue = DateTimeUtil.getCurrenTime();
        }
        timeValue += getAddDuration() * 1000;

        if (dueField.isDefault()) {
            PropertyUtils.setProperty(moduleRecord, dueField.getName(), timeValue);
        }
        else {
            moduleRecord.setDatum(dueField.getName(), timeValue);
        }

        FacilioModule module = modBean.getModule(getModuleId());
        UpdateRecordBuilder<ModuleBaseWithCustomFields> update = new UpdateRecordBuilder<>()
                .module(module)
                .fields(Collections.singletonList(dueField))
                .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
        update.update(moduleRecord);

        if (CollectionUtils.isEmpty(getEscalations())) {
            setEscalations(SLAWorkflowAPI.getEscalations(getId()));
        }

        addEscalationJobs(module, dueField, compareField, moduleRecord);

        super.executeTrueActions(record, context, placeHolders);
    }

    private void addEscalationJobs(FacilioModule module, FacilioField dueField, FacilioField compareField, ModuleBaseWithCustomFields moduleRecord) throws Exception {
        if (CollectionUtils.isNotEmpty(getEscalations())) {
            SLAWorkflowAPI.getActions(getEscalations());
            int count = 0;
            for (SLAWorkflowEscalationContext escalation : getEscalations()) {
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
}
