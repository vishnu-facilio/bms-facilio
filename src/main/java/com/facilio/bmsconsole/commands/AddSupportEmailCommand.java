package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;

public class AddSupportEmailCommand extends FacilioCommand {
	@Override
	@SuppressWarnings("unchecked")
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		try {
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		
		if(supportEmail != null) {
			supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			CommonCommandUtil.setFwdMail(supportEmail);
			FacilioService.runAsService(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.addSupportEmailSetting(supportEmail));
		}
		return false;
		}catch(Exception e) {
			if(e.getMessage().contains("Duplicate entry")){
				throw new IllegalArgumentException("Email ID already exsists");
			}
			throw new IllegalArgumentException(e.getMessage());
		}
	}
		
	}
