package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FTransactionManager;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.db.util.DBConf;
import com.facilio.tasker.FacilioTimer;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

@Log4j
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
            if (DBConf.getInstance().getCurrentOrgId() == 592) {
                try {
                    LOGGER.info("Current transaction while adding post transaction instant => " + FacilioTransactionManager.INSTANCE.getTransactionManager().getTransaction());
                    LOGGER.info("Adding post transaction instant jobs for TL " + jobName);
                }
                catch (Exception e) {

                }
            }
            FacilioTimer.scheduleInstantJob(jobName, (FacilioContext) context);
        }
        return false;
    }
}
