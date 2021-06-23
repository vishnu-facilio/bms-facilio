package com.facilio.bmsconsole.commands;

import java.util.Arrays;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PMStatus;
import com.facilio.constants.FacilioConstants;

public class ResetContext extends FacilioCommand {

    private long pmId;
    ResetContext () {}

    ResetContext(long id) {
        this.pmId = id;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenance pm = new PreventiveMaintenance();
        pm.setStatus(PMStatus.ACTIVE);

        context.clear();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(pmId));
        context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
        return false;
    }
}
