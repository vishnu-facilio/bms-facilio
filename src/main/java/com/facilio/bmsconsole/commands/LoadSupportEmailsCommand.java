package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;

public class LoadSupportEmailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<SupportEmailContext> emails = SupportEmailAPI.getSupportEmailsOfOrg(AccountUtil.getCurrentOrg().getOrgId());
		context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL_LIST, emails);
		return false;
	}

}
