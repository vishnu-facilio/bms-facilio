package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.constants.FacilioConstants;

public class GetEnabledDigestConfigCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.DIGEST_CONFIG_ID);
		List<DigestConfigContext> digests = DigestConfigAPI.getAllEnabledDigestConfig(configId);
		context.put(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, digests);
		return false;
	}

}
