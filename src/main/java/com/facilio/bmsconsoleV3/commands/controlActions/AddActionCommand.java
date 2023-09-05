package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() == 0){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            ControlActionAPI.dropControlActionCommands(controlActionContext.getId());
            V3ControlActionContext dummyControlAction = new V3ControlActionContext();
            dummyControlAction.setId(controlActionContext.getId());
            ControlActionAPI.deleteActions(controlActionContext.getId());
            if(CollectionUtils.isEmpty(controlActionContext.getActionContextList())){
                continue;
            }
            List<V3ActionContext> actionContextList = controlActionContext.getActionContextList();
            for(V3ActionContext actionContext : actionContextList ){
                actionContext.setControlAction(dummyControlAction);
            }
            ControlActionAPI.createAction(actionContextList);
        }
        return false;
    }
}
