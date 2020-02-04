package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class SLAPolicyContext extends WorkflowRuleContext {

    public SLAPolicyContext() {
        setRuleType(RuleType.SLA_POLICY_RULE);
    }

    @JsonInclude
    @Override
    public int getRuleType() {
        return super.getRuleType();
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        if (record instanceof ModuleBaseWithCustomFields) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField slaPolicyField = modBean.getField("slaPolicyId", getModuleName());

            ModuleBaseWithCustomFields r = (ModuleBaseWithCustomFields) record;
            r.setSlaPolicyId(getId());
            UpdateRecordBuilder<ModuleBaseWithCustomFields> builder = new UpdateRecordBuilder<>()
                    .module(getModule())
                    .fields(Collections.singletonList(slaPolicyField))
                    .andCondition(CriteriaAPI.getIdCondition(r.getId(), getModule()));
            builder.update(r);

//            Criteria criteria = new Criteria();
//            criteria.addAndCondition(CriteriaAPI.getCondition("SLA_POLICY_RULE_ID", "slaPoliceRuleId", String.valueOf(getId()), NumberOperators.EQUALS));
//
//            List<WorkflowRuleContext> slaCommitments = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(getModule(), Arrays.asList(SLA), criteria, RuleType.SLA_WORKFLOW_RULE);
//            if (CollectionUtils.isNotEmpty(slaCommitments)) {
//                List<Object> records = Arrays.asList(r);
//                Iterator<Object> it = records.iterator();
//                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(getModule().getName(), r, placeHolders);
//                WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(slaCommitments, getModule(), r, null, it, recordPlaceHolders, (FacilioContext) context, true, Arrays.asList(SLA), null);
//            }
        }
    }
}
