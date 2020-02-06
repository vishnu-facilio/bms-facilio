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
        }
    }

    public static class SLAPolicyEntityEscalationContext {
        private long slaEntityId = -1;
        public long getSlaEntityId() {
            return slaEntityId;
        }
        public void setSlaEntityId(long slaEntityId) {
            this.slaEntityId = slaEntityId;
        }

        private List<SLAWorkflowEscalationContext> escalations;
        public List<SLAWorkflowEscalationContext> getEscalations() {
            return escalations;
        }
        public void setEscalations(List<SLAWorkflowEscalationContext> escalations) {
            this.escalations = escalations;
        }
    }
}
