package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class AfterPMReminderJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PM_REMINDER_TYPE, ReminderType.AFTER);
			context.put(FacilioConstants.ContextNames.ID, jc.getJobId());
			
			Chain executePMReminderChain = FacilioChainFactory.getExecutePMReminderChain();
			executePMReminderChain.execute(context);
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
