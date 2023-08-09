package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetServiceAppointmentNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ServiceAppointmentContext> requests = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(requests)) {
            for (ServiceAppointmentContext serviceAppointmentContext : requests) {
                if (serviceAppointmentContext.getName() == null) {
                    ServiceOrderContext serviceOrder = serviceAppointmentContext.getServiceOrder();
                    if (serviceOrder != null && serviceOrder.getId() > 0) {
                        ServiceOrderContext serviceOrderContext = V3RecordAPI.getRecord(FacilioConstants.ServiceOrder.SERVICE_ORDER, serviceOrder.getId());
                        if (serviceOrderContext != null) {
                            serviceAppointmentContext.setName(serviceOrderContext.getName());
                        } else {
                            throw new Exception("Name Cannot be Empty");
                        }
                    } else {
                        throw new Exception("Name Cannot be Empty");
                    }
                }
            }
        }

        return false;
    }
}
