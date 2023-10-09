package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.getAppointmentEstimatedCostMapForItems;
import static com.facilio.fsm.util.ServiceAppointmentUtil.updateEstimatedCost;

public class SAEstimatedItemCostAfterCreateCommand  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedItems)){
            Map<Long,Double> appointmentCostMap = getAppointmentEstimatedCostMapForItems(serviceOrderPlannedItems);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateEstimatedCost(appointmentCostMap,eventType);
            }
          }
        return false;
    }
}
