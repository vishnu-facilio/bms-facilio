package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetControlActionInitialStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            if(controlActionContext.getControlActionSourceType() == V3ControlActionContext.ControlActionSourceTypeEnum.MANUAL.getVal() || controlActionContext.getControlActionSourceType() == null) {
                controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal());
            }
            if(controlActionContext.getControlActionSourceType() == V3ControlActionContext.ControlActionSourceTypeEnum.CONTROL_ACTION_TEMPLATE.getVal()){
                controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.PUBLISHED.getVal());
            }
            if(controlActionContext.getControlActionExecutionType() == null || controlActionContext.getControlActionExecutionType() <= 0){
                controlActionContext.setControlActionExecutionType(V3ControlActionContext.ControlActionExecutionType.ACTUAL.getVal());
            }
        }
        return false;
    }
}
