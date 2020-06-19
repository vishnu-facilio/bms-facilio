package com.facilio.v3.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StateFlowListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<? extends ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(records)) {
            Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(records);
            if (MapUtils.isNotEmpty(stateFlows)) {
                for(ModuleBaseWithCustomFields record: records) {
                    FacilioStatus currentState = record.getApprovalStatus();
                    if (currentState == null) {
                        currentState = record.getModuleState();
                    }
                    if (currentState == null) {
                        continue;
                    }

                    long stateFlowId = -1;
                    if (record.getApprovalFlowId() != null) {
                        stateFlowId = record.getApprovalFlowId();
                    } else if (record.getStateFlowId() != null) {
                        stateFlowId = record.getStateFlowId();
                    }

                    String key = stateFlowId + "_" + currentState.getId();
                    if(stateFlows.containsKey(key)) {
                        ArrayList<WorkflowRuleContext> list = new ArrayList<>(stateFlows.get(key));
                        removeUnwantedTranstions(list);
                        List<WorkflowRuleContext> evaluateStateFlowAndExecuteActions = StateFlowRulesAPI.evaluateStateFlowAndExecuteActions(list, moduleName, record, context);
                        if (CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions)) {
                            record.setEvaluatedTransitionIds(evaluateStateFlowAndExecuteActions.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                        }
                        record.setCanCurrentUserApprove(CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions));
                    }
                }
            }
            context.put(Constants.STATE_FLOWS, stateFlows);
        }
        return false;
    }

    private static void removeUnwantedTranstions(List<WorkflowRuleContext> states) {
        if (CollectionUtils.isEmpty(states)) {
            return;
        }

        Iterator<WorkflowRuleContext> iterator = states.iterator();
        while (iterator.hasNext()) {
            AbstractStateTransitionRuleContext transition = (AbstractStateTransitionRuleContext) iterator.next();
            if (transition.getTypeEnum() != AbstractStateTransitionRuleContext.TransitionType.NORMAL) {
                iterator.remove();
                continue;
            }

            if(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                if (!transition.isShowInTenantPortal()) {
                    iterator.remove();
                    continue;
                }
            }
            else if(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
                if (!transition.isShowInVendorPortal()) {
                    iterator.remove();
                    continue;
                }
            }
        }
    }

}
