package com.facilio.bmsconsole.instant.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.InstantJob;
import com.facilio.wms.endpoints.PubSubManager;

public class PubSubInstantJob extends InstantJob {
	
	private static final Logger LOGGER = LogManager.getLogger(PubSubInstantJob.class.getName());
	
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		try {
			String topicName = (String) context.get(FacilioConstants.ContextNames.PUBSUB_TOPIC);
			
			if ("readingChange".equalsIgnoreCase(topicName)) {
				String readingKey = (String) context.get(FacilioConstants.ContextNames.READING_KEY);
				
				PubSubManager.getInstance().publishReadingChange(AccountUtil.getCurrentOrg().getId(), readingKey);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during execution of PubSubInstantJob", e);
		}
	}
}
