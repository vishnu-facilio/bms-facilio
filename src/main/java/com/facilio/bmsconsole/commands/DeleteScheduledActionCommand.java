package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteScheduledActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long id = (Long)context.get(FacilioConstants.ContextNames.ID);

		ScheduledActionAPI.deleteScheduledAction(id,FacilioConstants.Job.DIGEST_JOB_NAME);
		
		return false;
	}

}
