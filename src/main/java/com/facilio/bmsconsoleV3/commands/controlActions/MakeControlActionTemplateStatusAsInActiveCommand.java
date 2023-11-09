package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class MakeControlActionTemplateStatusAsInActiveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionTemplateContext> controlActionTemplateContextList = (List<V3ControlActionTemplateContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionTemplateContextList)){
            return false;
        }
        for(V3ControlActionTemplateContext controlActionTemplateContext : controlActionTemplateContextList) {
            controlActionTemplateContext.setControlActionTemplateStatus(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getVal());
            if(controlActionTemplateContext.getControlActionExecutionType() == null || controlActionTemplateContext.getControlActionExecutionType() <= 0) {
                controlActionTemplateContext.setControlActionExecutionType(V3ControlActionContext.ControlActionExecutionType.ACTUAL.getVal());
            }
            controlActionTemplateContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal());
        }
        return false;
    }
}
