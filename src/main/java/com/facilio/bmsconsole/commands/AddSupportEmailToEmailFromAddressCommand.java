package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;

public class AddSupportEmailToEmailFromAddressCommand extends FacilioCommand {
	
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		SupportEmailContext supportEmail = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(supportEmail != null) {
			
			EmailFromAddress fromAddress = new EmailFromAddress();
			fromAddress.setDisplayName(supportEmail.getReplyName());
			fromAddress.setEmail(supportEmail.getActualEmail());
			fromAddress.setSiteId(supportEmail.getSiteId());
			fromAddress.setSourceType(EmailFromAddress.SourceType.SUPPORT.getIndex());
			
			V3Util.createRecord(modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME), FieldUtil.getAsJSON(fromAddress));
		}
		
		return false;
	}

}
