package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

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

		if (domain.equalsIgnoreCase(AccountUtil.getCurrentOrg().getDomain() + "." + FacilioProperties.getMailDomain())) {
			emailFromAddress.setVerificationStatus(true);
		}
	}

}
