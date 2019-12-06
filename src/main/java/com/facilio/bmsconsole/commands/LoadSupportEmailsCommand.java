package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class LoadSupportEmailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<SupportEmailContext> emails = FacilioService.runAsServiceWihReturn(() -> SupportEmailAPI.getSupportEmailsOfOrg(orgId));
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST, emails);
		return false;
	}

}
