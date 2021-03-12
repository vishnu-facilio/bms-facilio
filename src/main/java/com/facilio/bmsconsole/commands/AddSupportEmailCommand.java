package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.service.FacilioService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddSupportEmailCommand extends FacilioCommand {
	@Override
	@SuppressWarnings("unchecked")
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		
		if(supportEmail != null) {
			supportEmail.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			CommonCommandUtil.setFwdMail(supportEmail);
			FacilioService.runAsService(() -> SupportEmailAPI.addSupportEmailSetting(supportEmail));
		}
		return false;
	}
		
	}
