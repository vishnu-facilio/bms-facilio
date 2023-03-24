package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class GetShiftStatesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);
        FacilioStatus activeState = TicketAPI.getStatus(shiftMod, "active");
        context.put(FacilioConstants.Shift.ACTIVE_STATE, activeState);
        return false;
    }
}
