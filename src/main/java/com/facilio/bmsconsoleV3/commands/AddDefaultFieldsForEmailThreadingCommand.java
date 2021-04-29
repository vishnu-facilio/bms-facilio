package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;
import com.facilio.v3.context.Constants;

public class AddDefaultFieldsForEmailThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
	    
	    
	    for(EmailConversationThreadingContext emailConversation : emailConversations) {
	    	
	    	EmailToModuleDataContext emailToModuleData = MailMessageUtil.getEmailToModuleContext(emailConversation.getRecordId(), emailConversation.getModuleId());
	    	
	    	if(emailToModuleData != null) {		// record created from Email
	    		
	    		Long supportEmailIds = emailToModuleData.getParentId();
	    		
	    		SupportEmailContext parentSupportMailContext = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getSupportEmailFromId(AccountUtil.getCurrentOrg().getOrgId(), supportEmailIds));
	    		
	    		emailConversation.setFrom(parentSupportMailContext.getActualEmail());
	    		
	    	}
	    	else {								// record created from Somewhere else
	    		
	    		SupportEmailContext parentSupportMailContext = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.getSupportEmailsOfSite(AccountUtil.getCurrentOrg().getOrgId(),emailConversation.getSiteId()));
	    		if(parentSupportMailContext != null) {
	    			emailConversation.setFrom(parentSupportMailContext.getActualEmail());
	    		}
	    		else {
	    			emailConversation.setFrom("noreply@facilio.com");
	    		}
	    	}
	    	
	    	
	    }
	    
		return false;
	}

}
