package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ApprovalStateTransitionRuleContext extends AbstractStateTransitionRuleContext {

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        FacilioStatus status = TicketAPI.getStatus(getToStateId());
        if (status.getParentModuleId() == -1 && "Rejected".equals(status.getDisplayName())) {
            // if it is rejected status, check the evaluateStateFlow for approve transition
            List<Map<String, Long>> stateIds = new ArrayList<>();
            Map<String, Long> map = new HashMap<>();
            map.put("stateFlowId", getStateFlowId());
            map.put("fromStateId", getFromStateId());
            stateIds.add(map);
            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getStateTransitions(stateIds);
            if (CollectionUtils.isNotEmpty(allStateTransitionList) && allStateTransitionList.size() == 2) {
                Optional<WorkflowRuleContext> first = allStateTransitionList.stream().filter(transition -> transition.getId() != getId()).findFirst();
                if (first.isPresent()) {
                    boolean b = ((ApprovalStateTransitionRuleContext) first.get())
                            .evaluateStateFlow(moduleRecord.getApprovalFlowId(), moduleRecord.getApprovalStatus(), moduleName, record, placeHolders, context);
                    if (!b) {
                        return false;
                    }
                }
            }
        }
        return evaluateStateFlow(moduleRecord.getApprovalFlowId(), moduleRecord.getApprovalStatus(),
                moduleName, record, placeHolders, context);
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        super.executeTrueActions(record, context, placeHolders);


        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        // add activities
        FacilioStatus facilioStatus = TicketAPI.getStatus(getToStateId());
        ActivityType activityType = CommonActivityType.APPROVAL;
        JSONObject info = new JSONObject();
        info.put("status", facilioStatus.getStatus());
        info.put("name", getName());
        info.put("fromStateId", getFromStateId());
        info.put("toStateId", getToStateId());
        info.put("user", AccountUtil.getCurrentUser().getId());
        info.put("ruleId", getStateFlowId());
        CommonCommandUtil.addActivityToContext(moduleRecord.getId(), moduleRecord.getCurrentTime(), activityType, info, (FacilioContext) context);
    }

    @Override
    protected void executeTrue(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleId());

        FacilioStatus facilioStatus = TicketAPI.getStatus(getToStateId());
        List<FacilioField> fields = new ArrayList<>();

        if (facilioStatus.getType() == FacilioStatus.StatusType.EXIT) {
            // remove the approval
            fields.add(modBean.getField("approvalFlowId", module.getName()));
            moduleRecord.setApprovalFlowId(-99);
            facilioStatus.setId(-99);
            moduleRecord.setApprovalStatus(facilioStatus);
        }
        else {
            moduleRecord.setApprovalStatus(facilioStatus);
        }
        fields.add(modBean.getField("approvalStatus", module.getName()));

        // temp handling
        ModuleBaseWithCustomFields baseModuleRecord = new ModuleBaseWithCustomFields();
        baseModuleRecord.setId(moduleRecord.getId());
        baseModuleRecord.setApprovalStatus(moduleRecord.getApprovalStatus());
        baseModuleRecord.setApprovalFlowId(moduleRecord.getApprovalFlowId());
        baseModuleRecord.setModuleId(moduleRecord.getModuleId());
        baseModuleRecord.setFormId(moduleRecord.getFormId());
        baseModuleRecord.setOrgId(moduleRecord.getOrgId());


        UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(moduleRecord.getId(), module));
        updateBuilder.update(baseModuleRecord);
    }

    @Override
    public Boolean isShouldExecuteFromPermalink() {
        return true;
    }
}
