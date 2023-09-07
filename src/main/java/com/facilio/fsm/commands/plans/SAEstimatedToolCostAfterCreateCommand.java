package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.getAppointmentEstimatedCostMapForTools;
import static com.facilio.fsm.util.ServiceAppointmentUtil.updateEstimatedCost;

public class SAEstimatedToolCostAfterCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools)){
            Map<Long,Double> appointmentCostMap = getAppointmentEstimatedCostMapForTools(serviceOrderPlannedTools);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateEstimatedCost(appointmentCostMap,eventType);
            }
        }
        return false;
    }
}
