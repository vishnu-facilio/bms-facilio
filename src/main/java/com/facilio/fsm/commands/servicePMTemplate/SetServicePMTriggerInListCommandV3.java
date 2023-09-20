package com.facilio.fsm.commands.servicePMTemplate;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.getTriggerForTemplate;

public class SetServicePMTriggerInListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePMTemplateContext> servicePMTemplates = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePMTemplates)){
            for(ServicePMTemplateContext servicePMTemplate: servicePMTemplates){
                ServicePMTriggerContext trigger =  getTriggerForTemplate(servicePMTemplate.getId());
                servicePMTemplate.setServicePMTrigger(trigger);
            }
        }
        return false;
    }
}
