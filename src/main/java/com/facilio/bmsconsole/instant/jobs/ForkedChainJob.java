package com.facilio.bmsconsole.instant.jobs;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory.FacilioChain;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.InstantJob;

public class ForkedChainJob extends InstantJob {

	private static final Logger LOGGER = LogManager.getLogger(ForkedChainJob.class.getName());
	
	@Override
	public void execute(FacilioContext context) {
		// TODO Auto-generated method stub
		try {
			FacilioChain chain = new FacilioChain(false);
			List<Command> commands = (List<Command>) context.remove(FacilioConstants.Job.FORKED_COMMANDS);
			for (Command command : commands) {
				chain.addCommand(command);
			}
			CommonCommandUtil.addCleanUpCommand(chain);
			chain.execute(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred during execution of ForkedJob", e);
			CommonCommandUtil.emailException("ForkedJob", "Error occurred during execution of ForkedJob", e);
		}
	}

}
