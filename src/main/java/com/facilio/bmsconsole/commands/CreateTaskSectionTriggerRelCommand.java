package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class CreateTaskSectionTriggerRelCommand implements Command {
    @Override
    public boolean execute(Context context) throws Exception {
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
