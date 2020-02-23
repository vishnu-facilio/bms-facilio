package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		User requester = (User) context.get(FacilioConstants.ContextNames.REQUESTER);
		if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail())) {
			long orgid = AccountUtil.getCurrentOrg().getOrgId();
			String appDomain = AccountUtil.getCurrentOrg().getDomain() +".facilioportal.com";
			User portalUser = AccountUtil.getUserBean().getUser(requester.getUserName(),appDomain);
			Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
			if (portalUser != null) {
				requester.setId(portalUser.getOuid());
			}
			else {
				requester.setId(AccountUtil.getUserBean().inviteRequester(orgid, requester, isPublicRequest != null && isPublicRequest ? false : true, false, appDomain, 1));		
			}
			
			if (isPublicRequest != null && isPublicRequest) {
				setRequesterAsCurrentUser(requester);
			}
		}
		return false;
	}
	
	private void setRequesterAsCurrentUser(User requester) throws Exception {
		Account acct = new Account(AccountUtil.getCurrentOrg(), AccountUtil.getUserBean().getUser(requester.getId(), false));
		AccountUtil.setCurrentAccount(acct);
	}
}
