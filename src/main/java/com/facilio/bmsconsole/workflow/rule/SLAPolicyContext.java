package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SLAPolicyContext extends WorkflowRuleContext {

    private List<SLAPolicyEntityEscalationContext> escalations;
    public List<SLAPolicyEntityEscalationContext> getEscalations() {
        return escalations;
    }
    public void setEscalations(List<SLAPolicyEntityEscalationContext> escalations) {
        this.escalations = escalations;
    }

    private List<SLAWorkflowCommitmentRuleContext> commitments;
    public List<SLAWorkflowCommitmentRuleContext> getCommitments() {
        return commitments;
    }
    public void setCommitments(List<SLAWorkflowCommitmentRuleContext> commitments) {
        this.commitments = commitments;
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
            super.executeTrueActions(record, context, placeHolders);
        }
    }

    public static class SLAPolicyEntityEscalationContext implements Serializable {
        private long slaEntityId = -1;
        public long getSlaEntityId() {
            return slaEntityId;
        }
        public void setSlaEntityId(long slaEntityId) {
            this.slaEntityId = slaEntityId;
        }

        private List<SLAWorkflowEscalationContext> levels;
        public List<SLAWorkflowEscalationContext> getLevels() {
            return levels;
        }
        public void setLevels(List<SLAWorkflowEscalationContext> levels) {
            this.levels = levels;
        }
    }
}
