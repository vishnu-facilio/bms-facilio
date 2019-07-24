package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;

public class GetSupportEmailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long supportEmailId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(supportEmailId != -1) {
			SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromId(AccountUtil.getCurrentOrg().getOrgId(), supportEmailId);
			if(supportEmail != null) {
				context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);
			}
		}
		
		return false;
	}

}
