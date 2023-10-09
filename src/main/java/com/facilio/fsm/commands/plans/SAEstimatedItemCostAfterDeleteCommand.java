package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.getAppointmentEstimatedCostMapForItems;
import static com.facilio.fsm.util.ServiceAppointmentUtil.updateEstimatedCost;

public class SAEstimatedItemCostAfterDeleteCommand  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = (List<ServiceOrderPlannedItemsContext>) context.get("deletedRecords");
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedItems)){
            Map<Long,Double> appointmentCostMap = getAppointmentEstimatedCostMapForItems(serviceOrderPlannedItems);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateEstimatedCost(appointmentCostMap,eventType);
            }
        }

        return false;
    }
}
