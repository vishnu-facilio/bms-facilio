package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class WorkOrderNotesDestructureCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        String parentModuleName = (String) context.get("oldParentName");
        if( moduleName != null && !moduleName.isEmpty()){
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        }
        if( parentModuleName != null && !parentModuleName.isEmpty()){
            context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
        }
        return false;
    }
}
