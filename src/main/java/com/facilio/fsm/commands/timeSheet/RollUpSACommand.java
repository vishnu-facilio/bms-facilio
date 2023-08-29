package com.facilio.fsm.commands.timeSheet;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class RollUpSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<TimeSheetContext> timeSheets = (List<TimeSheetContext>) recordMap.get(context.get("moduleName"));

        if (CollectionUtils.isNotEmpty(timeSheets)) {
            for (TimeSheetContext timeSheet : timeSheets) {
                if(timeSheet.getServiceAppointment()!=null) {
                    ServiceAppointmentContext appointment = timeSheet.getServiceAppointment();
                    Long appointmentId = appointment.getId();
                    Long agentId = timeSheet.getFieldAgent().getId();
                    long workDuration = ServiceAppointmentUtil.getAppointmentDuration(agentId, appointmentId);
                    appointment.setActualDuration(workDuration);
                    V3Util.processAndUpdateSingleRecord(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, appointmentId, FieldUtil.getAsJSON(appointment), null, null, null, null, null, null, null, null);
                }
                else{
                    throw new FSMException(FSMErrorCode.TIME_SHEET_SA_MANDATORY);
                }
            }
        }
        return false;
    }
}
