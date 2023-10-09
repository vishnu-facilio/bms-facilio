package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderItemsContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.*;

public class SAItemCostAfterCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderItemsContext> serviceOrderItems = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderItems)){
            Map<Long,Double> appointmentCostMap = getAppointmentCostMapForItems(serviceOrderItems);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateActualCost(appointmentCostMap,eventType);
            }
        }
        return false;
    }
}
