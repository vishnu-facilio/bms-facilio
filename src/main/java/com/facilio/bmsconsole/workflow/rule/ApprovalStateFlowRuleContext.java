package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApprovalStateFlowRuleContext extends AbstractStateFlowRuleContext {

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        // Skip if the existing approval flow is same as this
        if (moduleRecord.getApprovalFlowId() == getId()) {
            return;
        }

        moduleRecord.setApprovalFlowId(getId());
        moduleRecord.setApprovalStatus(TicketAPI.getStatus(getDefaultStateId()));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = getModule();

        List<FacilioField> fields = new ArrayList<>();
        fields.add(modBean.getField("approvalFlowId", module.getName()));
        fields.add(modBean.getField("approvalStatus", module.getName()));
        UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
        updateBuilder.update(moduleRecord);
        super.executeTrueActions(record, context, placeHolders);
    }
}
