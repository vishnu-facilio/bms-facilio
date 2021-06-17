package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class PostPMReminderJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(PostPMReminderJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);
			context.put(FacilioConstants.ContextNames.ID, jc.getJobId());
			
			FacilioChain executePMReminderChain = FacilioChainFactory.getExecutePMReminderChain();
			executePMReminderChain.execute(context);
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
