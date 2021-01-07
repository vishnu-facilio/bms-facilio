package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddInstantJobCommand extends FacilioCommand implements PostTransactionCommand {

    private Context context;
    private String jobName;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        jobName = (String) context.get(FacilioConstants.ContextNames.INSTANT_JOB_NAME);
        if (StringUtils.isNotEmpty(jobName)) {
            context.remove(FacilioConstants.ContextNames.INSTANT_JOB_NAME);
            this.context = context;
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (context != null) {
            FacilioTimer.scheduleInstantJob(jobName, (FacilioContext) context);
        }
        return false;
    }
}
