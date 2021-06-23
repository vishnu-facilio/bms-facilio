package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;

public class CreateTaskSectionTriggerRelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PreventiveMaintenance> pms;
        pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
        if (pms == null) {
            return false;
        }
        for(PreventiveMaintenance pm: pms) {
            PreventiveMaintenanceAPI.addTaskSectionTrigger(pm, pm.getWoTemplate().getSectionTemplates());
        }
        return false;
    }
}
