package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderServiceContext;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.fsm.util.ServiceAppointmentUtil.updateActualCostDuringUpdate;
import static com.facilio.fsm.util.ServiceAppointmentUtil.updateEstimatedCostDuringUpdate;

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
                    ServiceAppointmentContext serviceAppointment = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,serviceOrderService.getServiceAppointment().getId());
                    List<UpdateChangeSet> changes =changeSet.get(serviceOrderService.getId());
                    updateActualCostDuringUpdate(changes,serviceAppointment,moduleName);
                }
            }
        }
        return false;
    }
}
