package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;

public class VerifyQrCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		Boolean bool = (Boolean) context.get(FacilioConstants.ContextNames.SHOULD_VERIFY_QR);
		String qrVAL = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		if (bool != null && bool) {
			ResourceContext resource = ResourceAPI.getResource(resourceId);
			if (resource == null || (resource != null && qrVAL != null && resource.getQrVal().equals(qrVAL))) {
				return false;
			}
			else {
				throw new IllegalArgumentException("Resource Associated With this Workorder gets mismatched");
			}
		}
		return false;
	}

}
