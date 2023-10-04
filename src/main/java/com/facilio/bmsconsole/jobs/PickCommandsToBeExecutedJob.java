package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CalendarActivityType;
import com.facilio.bmsconsole.activity.CommandActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.context.calendar.V3EventContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PickCommandsToBeExecutedJob extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        Long actionTime = jobContext.getExecutionTime()*1000;
        Long controlActionId = jobContext.getJobId();
        String jobName = jobContext.getJobName();
        V3ControlActionContext controlActionContext = ControlActionAPI.getControlAction(controlActionId);
        if(controlActionContext == null){
            throw new IllegalArgumentException("Control Action is Empty - #"+controlActionId);
        }
        if(jobName.equals("PickCommandsToBeExecutedScheduledAction")){
            controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_IN_PROGRESS.getVal());
            controlActionContext.setScheduleActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_IN_PROGRESS.getVal());
            V3Util.updateBulkRecords(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME, FacilioUtil.getAsMap(FieldUtil.getAsJSON(controlActionContext)), Collections.singletonList(controlActionContext.getId()),false);
        }
        if(jobName.equals("PickCommandsToBeExecutedRevertAction")){
            controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_IN_PROGRESS.getVal());
            controlActionContext.setRevertActionStatus(V3ControlActionContext.ControlActionStatus.REVERT_ACTION_IN_PROGRESS.getVal());
            V3Util.updateBulkRecords(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME, FacilioUtil.getAsMap(FieldUtil.getAsJSON(controlActionContext)), Collections.singletonList(controlActionContext.getId()),false);
        }
        List<V3CommandsContext> commandsContextList = ControlActionAPI.pickCommandsToBeExecuted(controlActionId,actionTime);
        if(CollectionUtils.isEmpty(commandsContextList)){
            return;
        }
        List<V3ActionContext> actionContextList = controlActionContext.getActionContextList();
        Map<Long,V3ActionContext> actionContextMap = actionContextList.stream().collect(Collectors.toMap(V3ActionContext::getId,Function.identity()));

       if(validateControlActionApproval(controlActionContext)) {
           for (V3CommandsContext commandsContext : commandsContextList) {
               V3ActionContext actionContext = actionContextMap.get(commandsContext.getAction().getId());
               ControlActionAPI.setReadingValueForCommand(commandsContext, actionContext);
               ControlActionAPI.updateCommand(commandsContext);
               if(commandsContext.getControlActionCommandStatus() == V3CommandsContext.ControlActionCommandStatus.POINT_NOT_COMMISSIONED.getVal()){
                   commandsContextList.remove(commandsContext);
               }
               else {
                   ControlActionAPI.addCommandActivity(V3CommandsContext.ControlActionCommandStatus.IN_PROGRESS.getValue(), commandsContext.getId());
               }
               if (controlActionContext.getControlActionExecutionType() == V3ControlActionContext.ControlActionExecutionType.SANDBOX.getVal()) {
                   validateExecutionForSandBox(commandsContext);
               }
           }

           //Todo validate IotMessageAPI.setReadingValueForV3CommandContext()
           //Todo ask agent team abt the value of command (setValue())
           if (controlActionContext.getControlActionExecutionType() == V3ControlActionContext.ControlActionExecutionType.ACTUAL.getVal()) {
               IoTMessageAPI.setReadingValueForV3CommandContext(commandsContextList);
           }
       }
        else{
            for(V3CommandsContext commandsContext : commandsContextList){
                commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.FAILED.getVal());
                commandsContext.setErrorMsg("Approval Pending");
                ControlActionAPI.updateCommand(commandsContext);
                ControlActionAPI.addCommandActivity(V3CommandsContext.ControlActionCommandStatus.FAILED.getValue(),commandsContext.getId());
            }
        }


    }
    public boolean validateControlActionApproval(V3ControlActionContext controlActionContext){
        if(controlActionContext.getApprovalStatus() == null && controlActionContext.getApprovalFlowId() < 0 ){
            return true;
        }
        return false;
    }
    public void validateExecutionForSandBox(V3CommandsContext commandsContext) throws Exception{
        commandsContext.setAfterValue(commandsContext.getSetValue());
        commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.SUCCESS.getVal());
        ControlActionAPI.addCommandActivity(V3CommandsContext.ControlActionCommandStatus.SCHEDULED.getValue(),commandsContext.getId());
    }
}
