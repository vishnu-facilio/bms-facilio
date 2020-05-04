package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;

public class AddRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		User requester = (User) context.get(FacilioConstants.ContextNames.REQUESTER);
		if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail())) {
			long orgid = AccountUtil.getCurrentOrg().getOrgId();
			long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
			AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
			
			if(appId > 0 && appDomain != null) {
				
				User portalUser = AccountUtil.getUserBean().getAppUserForUserName(requester.getUserName(), appId, orgid);
				Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
				requester.setApplicationId(appId);
				requester.setAppDomain(appDomain);
			
				if (portalUser != null) {
					requester.setId(portalUser.getOuid());
				}
				else {
					requester.setId(AccountUtil.getUserBean().inviteRequester(orgid, requester, isPublicRequest != null && isPublicRequest ? false : true, false, appDomain.getIdentifier(), true, false));
				}
				
				if (isPublicRequest != null && isPublicRequest) {
					setRequesterAsCurrentUser(requester);
				}
			}
		}
		return false;
	}
	
	private void setRequesterAsCurrentUser(User requester) throws Exception {
		Account acct = new Account(AccountUtil.getCurrentOrg(), requester);
		AccountUtil.setCurrentAccount(acct);
	}
}
