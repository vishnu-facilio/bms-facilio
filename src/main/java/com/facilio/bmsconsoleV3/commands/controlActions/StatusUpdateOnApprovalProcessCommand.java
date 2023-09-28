package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalStateTransitionRuleContext;
import com.facilio.bmsconsoleV3.actions.V3ControlActionAction;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StatusUpdateOnApprovalProcessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            Long approvalTransitionId = (Long) context.get(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID);
            if(approvalTransitionId == null || approvalTransitionId < 0){
                continue;
            }
            if(controlActionContext.getControlActionStatus() == V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal()){
                continue;
            }
            List<PeopleContext> firstLevelApproverList = ControlActionAPI.getApprovalList(controlActionContext.getId(),FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
            List<PeopleContext> secondLevelApproverList = ControlActionAPI.getApprovalList(controlActionContext.getId(),FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
            List<V3CommandsContext> commandsContextList = ControlActionAPI.getCommandsOfControlAction(controlActionContext.getId());
            ApprovalStateTransitionRuleContext approvalTransition = (ApprovalStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(approvalTransitionId);
            if(CollectionUtils.isNotEmpty(firstLevelApproverList) && controlActionContext.getControlActionStatus() == V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getVal()){
                if(approvalTransition.getName().equals("Approved")){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.FIRST_LEVEL_APPROVED.getVal());
                    ControlActionAPI.updateControlAction(controlActionContext);
                    ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.FIRST_LEVEL_APPROVED.getValue(),controlActionContext.getId());
                }
                if(approvalTransition.getName().equals("Rejected")){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.REJECTED.getVal());
                    updateCommandStatus(commandsContextList, V3CommandsContext.ControlActionCommandStatus.CANCELED);
                }
                if(CollectionUtils.isNotEmpty(secondLevelApproverList) && controlActionContext.getControlActionSourceType() == V3ControlActionContext.ControlActionStatus.FIRST_LEVEL_APPROVED.getVal()){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.WAITING_FOR_SECOND_LEVEL_APPROVAL.getVal());
                    ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.WAITING_FOR_SECOND_LEVEL_APPROVAL.getValue(),controlActionContext.getId());
                }
            }
            else if(CollectionUtils.isEmpty(firstLevelApproverList)){
                controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getVal());
                ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getValue(),controlActionContext.getId());
                updateCommandStatus(commandsContextList, V3CommandsContext.ControlActionCommandStatus.SCHEDULED);
                controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
                ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getValue(),controlActionContext.getId());
                controlActionContext.setScheduleActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
            }
            else if(controlActionContext.getControlActionStatus() == V3ControlActionContext.ControlActionStatus.FIRST_LEVEL_APPROVED.getVal() && CollectionUtils.isEmpty(secondLevelApproverList)){
                controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getVal());
                updateCommandStatus(commandsContextList, V3CommandsContext.ControlActionCommandStatus.SCHEDULED);
            }
            else if(CollectionUtils.isNotEmpty(secondLevelApproverList) && controlActionContext.getControlActionStatus() == V3ControlActionContext.ControlActionStatus.WAITING_FOR_SECOND_LEVEL_APPROVAL.getVal()){
                if(approvalTransition.getName().equals("Approved")){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.SECOND_LEVEL_APPROVED.getVal());
                    ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.SECOND_LEVEL_APPROVED.getValue(),controlActionContext.getId());
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getVal());
                    ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getValue(),controlActionContext.getId());
                    updateCommandStatus(commandsContextList, V3CommandsContext.ControlActionCommandStatus.SCHEDULED);
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
                    controlActionContext.setScheduleActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
                }
                if(approvalTransition.getName().equals("Rejected")){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.REJECTED.getVal());
                    updateCommandStatus(commandsContextList, V3CommandsContext.ControlActionCommandStatus.CANCELED);
                }
            }
        }
        return false;
    }
    public static void updateCommandStatus(List<V3CommandsContext> commandsContextList, V3CommandsContext.ControlActionCommandStatus commandStatus) throws Exception{
        if(CollectionUtils.isEmpty(commandsContextList)){
            return;
        }
        for(V3CommandsContext commandsContext : commandsContextList) {
            commandsContext.setControlActionCommandStatus(commandStatus.getVal());
            if (commandsContext.getControlActionCommandStatus() == V3CommandsContext.ControlActionCommandStatus.POINT_NOT_COMMISSIONED.getVal() && commandStatus == V3CommandsContext.ControlActionCommandStatus.CANCELED) {
                ControlActionAPI.updateCommand(commandsContext);
                ControlActionAPI.addCommandActivity(commandStatus.getValue(), commandsContext.getId());
            }
        }
    }

}
