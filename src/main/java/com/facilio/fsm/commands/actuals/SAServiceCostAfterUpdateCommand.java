package com.facilio.fsm.commands.actuals;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderServiceContext;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.fsm.util.ServiceAppointmentUtil;

public class SAServiceCostAfterUpdateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderServiceContext> serviceOrderServices = recordMap.get(moduleName);
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        if(CollectionUtils.isNotEmpty(serviceOrderServices) && changeSet!=null){
            for(ServiceOrderServiceContext serviceOrderService : serviceOrderServices){
                if(serviceOrderService.getServiceAppointment()!=null){
                    FacilioContext appointmentContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, Collections.singletonList(serviceOrderService.getServiceAppointment().getId()));
                    ServiceAppointmentContext serviceAppointment  = (ServiceAppointmentContext) Constants.getRecordList(appointmentContext).get(0);
                    List<UpdateChangeSet> changes =changeSet.get(serviceOrderService.getId());
                    ServiceAppointmentUtil.updateActualCostDuringUpdate(changes,serviceAppointment,moduleName);
                }
            }
        }
        return false;
    }
}
