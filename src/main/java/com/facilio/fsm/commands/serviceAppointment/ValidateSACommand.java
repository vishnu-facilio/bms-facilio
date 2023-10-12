package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ValidateSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                if(Optional.ofNullable(serviceAppointment.getScheduledStartTime()).orElse(0L) > 0 &&
                        Optional.ofNullable(serviceAppointment.getScheduledEndTime()).orElse(0L) > 0) {
                    if (serviceAppointment.getScheduledStartTime() >= serviceAppointment.getScheduledEndTime() ) {
                        throw new FSMException(FSMErrorCode.SA_SCHEDULED_TIME_MISMATCH);
                    }
                    else if(serviceAppointment.getScheduledStartTime()<serviceAppointment.getCurrentTime()){
                        throw new FSMException(FSMErrorCode.SA_SCHEDULED_TIME_INVALID);
                    }
                }else{
                    throw new FSMException(FSMErrorCode.SA_DETAILS_REQUIED);
                }
            }
            }
        return false;
    }
}
