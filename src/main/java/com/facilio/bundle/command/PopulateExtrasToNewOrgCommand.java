package com.facilio.bundle.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

public class PopulateExtrasToNewOrgCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject signupInfo =  (JSONObject) context.get(BundleConstants.DESTINATION_ORG);
		
		FacilioChain orgSignupAfterChain = TransactionChainFactory.getOrgCreateOnCopyCustomizationAfterSaveChain();
		FacilioContext signupContext = orgSignupAfterChain.getContext();
		signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);
		
		Account account = AccountUtil.getCurrentAccount();
		
		if (account != null && account.getOrg().getOrgId() > 0) {
			signupContext.put("orgId", account.getOrg().getOrgId());
			
			orgSignupAfterChain.execute();
		}
		
		return false;
	}

}
