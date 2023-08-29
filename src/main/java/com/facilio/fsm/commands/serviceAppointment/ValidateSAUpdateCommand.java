package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateSAUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        HashMap<String,Object> oldRecordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long,Object> oldServiceAppointments = (Map<Long,Object>) oldRecordMap.get(context.get("moduleName"));
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for(ServiceAppointmentContext serviceAppointment: serviceAppointments){
                ServiceAppointmentContext oldSA = (ServiceAppointmentContext) oldServiceAppointments.get(serviceAppointment.getId());
                ServiceAppointmentTicketStatusContext appointmentStatus = serviceAppointment.getStatus();
                if(serviceAppointment.getServiceOrder().getId()!=oldSA.getServiceOrder().getId()){
                    throw new FSMException(FSMErrorCode.SA_FIELD_UPDATE_PREVENT);
                }
                if(appointmentStatus!= null && appointmentStatus.getTypeCode() != 1) {

                    if (serviceAppointment.getFieldAgent().getId() != oldSA.getFieldAgent().getId()) {
                        throw new FSMException(FSMErrorCode.SA_FIELD_UPDATE_PREVENT);
                    }
                }

            }


        }

        return false;
    }
}
