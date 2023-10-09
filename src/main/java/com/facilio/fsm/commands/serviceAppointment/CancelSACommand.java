package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.activity.ServiceAppointmentActivityType;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class CancelSACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ServiceAppointmentUtil.cancelServiceAppointment(recordId);
        JSONObject info = new JSONObject();
        info.put("doneBy", AccountUtil.getCurrentUser().getName());
        CommonCommandUtil.addActivityToContext(recordId, -1, ServiceAppointmentActivityType.CANCELLED, info, (FacilioContext) context);
        return false;
    }
}
