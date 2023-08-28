package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class DispatchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        Long fieldAgentId = (Long) context.get(FacilioConstants.ServiceAppointment.FIELD_AGENT_ID);
        Long scheduledStartTime = (Long) context.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME);
        Long scheduledEndTime = (Long) context.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME);
        Boolean skipValidation = (Boolean) context.get(FacilioConstants.ServiceAppointment.SKIP_VALIDATION);
        ServiceAppointmentUtil.dispatchServiceAppointment(recordId, fieldAgentId,scheduledStartTime,scheduledEndTime,skipValidation);


        return false;
    }
}
