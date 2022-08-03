package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class PMResourcePlannerBeforeSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordMap(context).get(Constants.getModuleName(context));

        for (ModuleBaseWithCustomFields moduleBaseWithCustomField : moduleBaseWithCustomFields) {
            PMResourcePlanner pmresourcePlanner = (PMResourcePlanner) moduleBaseWithCustomField;
            if (pmresourcePlanner.getAssignedTo() != null && pmresourcePlanner.getAssignedTo().getId() <= 0) {
                pmresourcePlanner.setAssignedTo(null);
            }
        }
        return false;
    }
}
