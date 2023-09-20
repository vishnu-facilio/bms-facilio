package com.facilio.fsm.commands.servicePMTemplate;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getTriggerForTemplate;

public class SetServicePMTriggerCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        ServicePMTemplateContext servicePMTemplate = (ServicePMTemplateContext) CommandUtil.getModuleData(context, FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE, id);
        if(servicePMTemplate!=null){
            ServicePMTriggerContext trigger =  getTriggerForTemplate(id);
            servicePMTemplate.setServicePMTrigger(trigger);
        }
        return false;
    }
}
