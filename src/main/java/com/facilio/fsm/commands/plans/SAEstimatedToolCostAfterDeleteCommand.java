package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.*;

public class SAEstimatedToolCostAfterDeleteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = (List<ServiceOrderPlannedToolsContext>) context.get("deletedRecords");
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools)){
            Map<Long,Double> appointmentCostMap = getAppointmentEstimatedCostMapForTools(serviceOrderPlannedTools);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateEstimatedCost(appointmentCostMap,eventType);
            }
        }
        return false;
    }
}
