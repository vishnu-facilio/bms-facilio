package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteDigestConfigCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.DIGEST_CONFIG_ID);
		DigestConfigAPI.deleteDigestConfig(configId);
		return false;
	}
	
	
}
