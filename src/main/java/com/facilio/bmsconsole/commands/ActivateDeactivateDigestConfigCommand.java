package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivateDeactivateDigestConfigCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.DIGEST_CONFIG_ID);
		Map<String, Object> config = DigestConfigAPI.getDigestConfig(configId);
		if(config != null) {
			Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
		    updateConfigStatus(status, configId);
		    if (status) {
				FacilioTimer.activateJob((Long)config.get("scheduledActionId"), FacilioConstants.Job.DIGEST_JOB_NAME);
			}
		    else {
				FacilioTimer.inActivateJob((Long)config.get("scheduledActionId"), FacilioConstants.Job.DIGEST_JOB_NAME);
			}
//		    JobStore.setInActiveStatusForJob(AccountUtil.getCurrentOrg().getId(), , status);
		}
		
	    return false;
	}
	
	private void updateConfigStatus(boolean status, long id) throws Exception{
		Map<String, Object> configProps = new HashMap<>();
		configProps.put("isActive", status);
		DigestConfigAPI.updateConfig(configProps, Collections.singletonList(id));
	}

}
