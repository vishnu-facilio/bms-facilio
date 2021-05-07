package com.facilio.bmsconsoleV3.actions;

import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MailMessageAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	EmailFromAddress fromAddress;
	
	long recordId = -1l;
	long moduleId = -1l;
	
	public String reSendVerificationEmail() throws Exception {
		
		FacilioChain chain = TransactionChainFactoryV3.getReSendVerificationEmailChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME, fromAddress);
		
		chain.execute();
		
		return SUCCESS;
	}
	
	public String getfromEmailForEmailThreadingReply() throws Exception {
		
		if(getRecordId() > 0 && getModuleId() > 0) {
			
			FacilioChain chain = TransactionChainFactoryV3.getFromEmailForEmailThreadingReplyChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
			context.put(FacilioConstants.ContextNames.MODULE_ID, getModuleId());
			
			chain.execute();
			
			List<EmailFromAddress> emailAddressList  = (List<EmailFromAddress>) context.get(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
			
			JSONObject data = new JSONObject();
			
			data.put(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME, emailAddressList);
			setData(data);
		}
		
		return SUCCESS;
	}
}
