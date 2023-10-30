package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateActionsOfControlActionTemplateCommand extends FacilioCommand {
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
            if(CollectionUtils.isEmpty(controlActionTemplateContext.getActionContextList())){
                throw new IllegalArgumentException("Action Can't be Empty");
            }
        }
        return false;
    }
}
