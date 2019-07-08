package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;

public class ActivateDeactivateDigestConfigCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.DIGEST_CONFIG_ID);
		Map<String, Object> config = DigestConfigAPI.getDigestConfig(configId);
		if(config != null) {
			Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
		    updateConfigStatus(status, configId);
		    JobStore.setInActiveStatusForJob((Long)config.get("scheduledActionId"), FacilioConstants.Job.DIGEST_JOB_NAME, status);
		}
		
	    return false;
	}
	
	private void updateConfigStatus(boolean status, long id) throws Exception{
		Map<String, Object> configProps = new HashMap<>();
		configProps.put("isActive", status);
		DigestConfigAPI.updateConfig(configProps, Collections.singletonList(id));
	}

}
