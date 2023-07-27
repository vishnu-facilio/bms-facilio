package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SetServiceAppointmentStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(records)) {

            for (ModuleBaseWithCustomFields record : records) {
                ServiceAppointmentContext appointment = (ServiceAppointmentContext) record;
                if(Optional.ofNullable(appointment.getScheduledStartTime()).orElse(0L) > 0
                    && Optional.ofNullable(appointment.getScheduledEndTime()).orElse(0L) > 0){
                    ServiceAppointmentTicketStatusContext dispatch = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.SCHEDULED);
                    if(dispatch != null){
                        appointment.setStatus(dispatch);
                    }
                } else {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Scheduled start and end time is mandatory to create an appointment");
                }
            }
        }
        return false;
    }
}
