package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckRecordLockCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<Long> recordIds = Constants.getRecordIds(context);

        List<ServiceAppointmentContext> serviceAppointments = new ArrayList<>();
        if(recordMap != null) {
            serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        }
        else if(CollectionUtils.isNotEmpty(recordIds)){
            FacilioContext listContext = V3Util.getSummary(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,recordIds);
            serviceAppointments = Constants.getRecordList(listContext);
        }
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                ServiceAppointmentTicketStatusContext status = V3RecordAPI.getRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_TICKET_STATUS, serviceAppointment.getStatus().getId());
                if (eventType == EventType.EDIT) {
                    if (status.isRecordLocked()) {
                        throw new FSMException(FSMErrorCode.SA_RECORD_LOCKED);
                    }
                } else if (eventType == EventType.DELETE) {
                    if (status.isDeleteLocked()) {
                        throw new FSMException(FSMErrorCode.SA_RECORD_LOCKED);
                    }
                }
            }
        }
        return false;
    }
}

