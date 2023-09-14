package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;

public class CompleteSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ServiceAppointmentUtil.completeServiceAppointment(recordId);
        CommonCommandUtil.addActivityToContext(recordId, -1, ServiceAppointmentActivityType.COMPLETE_WORK, null, (FacilioContext) context);
        return false;
    }
}
