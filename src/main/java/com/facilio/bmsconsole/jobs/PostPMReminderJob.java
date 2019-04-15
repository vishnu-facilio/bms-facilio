package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PostPMReminderJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(PostPMReminderJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO)) {
				return;
			}
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);
			context.put(FacilioConstants.ContextNames.ID, jc.getJobId());
			
			Chain executePMReminderChain = FacilioChainFactory.getExecutePMReminderChain();
			executePMReminderChain.execute(context);
			
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
