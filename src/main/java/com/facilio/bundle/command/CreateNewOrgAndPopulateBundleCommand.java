package com.facilio.bundle.command;

import java.util.Locale;
import java.util.logging.Level;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.time.DateTimeUtil;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;

@Log
public class CreateNewOrgAndPopulateBundleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject signupInfo =  (JSONObject) context.get(BundleConstants.DESTINATION_ORG);
		
		signupInfo.put("cognitoId", "facilio");
		signupInfo.put("isFacilioAuth", true);
		
		Locale locale = DateTimeUtil.getLocale();
		IAMAccount iamAccount = null;
		try {
			
			iamAccount = IAMOrgUtil.signUpOrg(signupInfo, locale);
			Account account = new Account(iamAccount.getOrg(), new User(iamAccount.getUser()));
			AccountUtil.setCurrentAccount(account);
			
			FacilioChain populateBundleCommand = BundleTransactionChainFactory.getPopulateBundleChain();
			
			FacilioContext newcontext = populateBundleCommand.getContext();
			
			newcontext.putAll(context);
			
			populateBundleCommand.execute();
			
		}
		catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while copying customizations ", e);
			if(iamAccount != null && iamAccount.getOrg() != null && iamAccount.getOrg().getOrgId() > 0) {
				IAMOrgUtil.rollBackSignedUpOrg(iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
			}
			return true;
		}
		return false;
	}
}
