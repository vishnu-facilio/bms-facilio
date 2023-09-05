package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PickCommandsToBeExecutedJob extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        Long actionTime = jobContext.getExecutionTime()*1000;
        Long controlActionId = jobContext.getJobId();
        V3ControlActionContext controlActionContext = ControlActionAPI.getControlAction(controlActionId);
        if(controlActionContext == null){
            throw new IllegalArgumentException("Control Action is Empty - #"+controlActionId);
        }
        List<V3CommandsContext> commandsContextList = ControlActionAPI.pickCommandsToBeExecuted(controlActionId,actionTime);
        if(CollectionUtils.isEmpty(commandsContextList)){
            return;
        }
        List<V3ActionContext> actionContextList = controlActionContext.getActionContextList();
        Map<Long,V3ActionContext> actionContextMap = actionContextList.stream().collect(Collectors.toMap(V3ActionContext::getId,Function.identity()));

       // if(validateControlActionApproval(controlActionContext)){
            for(V3CommandsContext commandsContext : commandsContextList){
                V3ActionContext actionContext = actionContextMap.get(commandsContext.getAction().getId());
                ControlActionAPI.setReadingValueForCommand(commandsContext,actionContext);
            }
            //Todo validate IotMessageAPI.setReadingValueForV3CommandContext()
            //Todo ask agent team abt the value of command (setValue())
            IoTMessageAPI.setReadingValueForV3CommandContext(commandsContextList);

//        }
//        else{
//            for(V3CommandsContext commandsContext : commandsContextList){
//                commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.FAILED.getVal());
//                commandsContext.setErrorMsg("Approval Pending");
//                ControlActionAPI.updateCommand(commandsContext);
//            }
//        }


    }
    public boolean validateControlActionApproval(V3ControlActionContext controlActionContext){
        if(controlActionContext.getApprovalStatus() == null && controlActionContext.getApprovalFlowId() < 0 ){
            return true;
        }
        return false;
    }
}
