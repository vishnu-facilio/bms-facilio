package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class AddDefaultFieldsForEmailThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
	    
		Long orgID = AccountUtil.getCurrentOrg().getOrgId();
		
		fillMessageType(emailConversations);
		
	    for(EmailConversationThreadingContext emailConversation : emailConversations) {
	    	
	    	if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.ADMIN.getIndex() && emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.REPLY.getIndex()) {
	    		
	    		if(!emailConversation.getFrom().contains("<")) {
	    			
	    			EmailFromAddress emailFromAddress = MailMessageUtil.getEmailFromAddress(emailConversation.getFrom(), true);
	    			
	    			if(emailFromAddress != null) {
	    				emailConversation.setFrom(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(emailFromAddress.getDisplayName(), emailConversation.getFrom()));
	    			}
	    			else {
	    				throw new RESTException(ErrorCode.VALIDATION_ERROR, "From email is not verified");
	    			}
	    		}
		    	
		    	if(!emailConversation.getTo().contains("<")) {
		    		
		    		PeopleContext people = PeopleAPI.getPeople(emailConversation.getTo());
		    		
		    		if(people.getName() != null ) {
		    			
		    			emailConversation.setTo(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(people.getName(), emailConversation.getTo()));
		    		}
		    		else {
		    			emailConversation.setTo(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(MailMessageUtil.getNameFromEmail.apply(emailConversation.getTo()), emailConversation.getTo()));
		    		}
		    	}
	    	}
	    	
	    	if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.CLIENT.getIndex() && emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.REPLY.getIndex()) {
	    		
	    		if(!emailConversation.getFrom().contains("<")) {
	    			
	    			PeopleContext people = PeopleAPI.getPeople(emailConversation.getFrom());
		    		
		    		if(people.getName() != null ) {
		    			
		    			emailConversation.setFrom(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(people.getName(), emailConversation.getFrom()));
		    		}
		    		else {
		    			emailConversation.setFrom(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(MailMessageUtil.getNameFromEmail.apply(emailConversation.getFrom()), emailConversation.getFrom()));
		    		}
	    		}
	    		if(emailConversation.getTo() != null && !emailConversation.getTo().contains("<")) {
	    			
	    			EmailFromAddress emailFromAddress = MailMessageUtil.getEmailFromAddress(emailConversation.getTo(), false);

					if(emailFromAddress != null && emailFromAddress.getDisplayName() != null) {
						emailConversation.setTo(MailMessageUtil.getWholeEmailFromNameAndEmail.apply(emailFromAddress.getDisplayName(), emailConversation.getTo()));
					}
	    		}
	    	}
	    }
	    
		return false;
	}
	
	private void fillMessageType(List<EmailConversationThreadingContext> emailConversations) {
		// TODO Auto-generated method stub
		
		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		EmailConversationThreadingContext.From_Type fromType = null;
		if(currentApp == null || currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
			fromType = EmailConversationThreadingContext.From_Type.ADMIN;
		}
		else {
			fromType = EmailConversationThreadingContext.From_Type.CLIENT;
		}
		for(EmailConversationThreadingContext emailConversation : emailConversations) {
			if(emailConversation.getFromType() <= 0) {
				emailConversation.setFromType(fromType.getIndex());
			}
		}
	}

}
