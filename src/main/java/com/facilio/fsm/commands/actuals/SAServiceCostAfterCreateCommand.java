package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderServiceContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.*;

public class SAServiceCostAfterCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderServiceContext> serviceOrderServices = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderServices)){
            Map<Long,Double> appointmentCostMap = getAppointmentCostMapForServices(serviceOrderServices);
            if(appointmentCostMap!=null && !appointmentCostMap.isEmpty()){
                updateActualCost(appointmentCostMap,eventType);
            }
        }
        return false;
    }
}
