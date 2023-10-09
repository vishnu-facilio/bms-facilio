package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.*;

public class SAEstimatedServiceCostAfterDeleteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = (List<ServiceOrderPlannedServicesContext>) context.get("deletedRecords");
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedServices)){
            Map<Long,Double> appointmentCostMap = getAppointmentEstimatedCostMapForServices(serviceOrderPlannedServices);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateEstimatedCost(appointmentCostMap,eventType);
            }
        }

        return false;
    }
}
