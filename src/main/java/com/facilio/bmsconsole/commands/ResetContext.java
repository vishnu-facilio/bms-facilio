package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Arrays;

public class ResetContext implements Command {

    private long pmId;
    ResetContext () {}

    ResetContext(long id) {
        this.pmId = id;
    }
    @Override
    public boolean execute(Context context) throws Exception {
        PreventiveMaintenance pm = new PreventiveMaintenance();
        pm.setStatus(true);

        context.clear();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(pmId));
        context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
        return false;
    }
}
