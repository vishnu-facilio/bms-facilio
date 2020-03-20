package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class AddRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		User requester = (User) context.get(FacilioConstants.ContextNames.REQUESTER);
		if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail())) {
			long orgid = AccountUtil.getCurrentOrg().getOrgId();
			AppDomain appDomain = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, orgid);
			if(appDomain != null) {
				User portalUser = AccountUtil.getUserBean().getUserForUserName(requester.getUserName(),appDomain.getDomain());
				Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
				if (portalUser != null) {
					requester.setId(portalUser.getOuid());
				}
				else {
					requester.setId(AccountUtil.getUserBean().inviteRequester(orgid, requester, isPublicRequest != null && isPublicRequest ? false : true, false, appDomain.getDomain(), 1, true));
					
				}
				
				if (isPublicRequest != null && isPublicRequest) {
					setRequesterAsCurrentUser(requester);
				}
			}
		}
		return false;
	}
	
	private void setRequesterAsCurrentUser(User requester) throws Exception {
		Account acct = new Account(AccountUtil.getCurrentOrg(), AccountUtil.getUserBean().getUser(requester.getId(), false));
		AccountUtil.setCurrentAccount(acct);
	}
}
