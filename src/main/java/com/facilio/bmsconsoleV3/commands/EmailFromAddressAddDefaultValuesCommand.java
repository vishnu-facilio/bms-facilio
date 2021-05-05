package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;

public class EmailFromAddressAddDefaultValuesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailFromAddress> emailFromAddreses = Constants.getRecordList((FacilioContext) context);
		
		for(EmailFromAddress emailFromAddress : emailFromAddreses) {
			
			emailFromAddress.setName(DisplayNameToLinkNameUtil.getLinkName(emailFromAddress.getDisplayName(), FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME, "name"));
			
			emailFromAddress.setActiveStatus(true);
			
			emailFromAddress.setCreationType(EmailFromAddress.CreationType.CUSTOM.getIndex());
			
			checkForOrgDomainAndSetAutoVerified(emailFromAddress);
		}
		return false;
	}

	private void checkForOrgDomainAndSetAutoVerified(EmailFromAddress emailFromAddress) {
		// TODO Auto-generated method stub
		
		String domain = emailFromAddress.getEmail().split("@")[1];
		
		if(domain.equalsIgnoreCase(AccountUtil.getCurrentOrg().getDomain()+".facilio.com")) {
			emailFromAddress.setVerificationStatus(true);
		}
	}

}
