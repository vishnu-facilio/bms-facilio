package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;

public class AddRequesterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		User requester = (User) context.get(FacilioConstants.ContextNames.REQUESTER);
		if (requester != null && requester.getEmail() != null && !"".equals(requester.getEmail())) {
			long orgid = AccountUtil.getCurrentOrg().getOrgId();
			Criteria criteria = new Criteria();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getUserFields());
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("email"), requester.getEmail(), StringOperators.IS));
			List<User> user = AccountUtil.getOrgBean().getOrgUsers(orgid, criteria);
			if (user != null && !user.isEmpty()) {
				requester.setId(user.get(0).getOuid());
			}
			else {
				requester.setId(AccountUtil.getUserBean().inviteRequester(orgid, requester));		
			}
			
			Boolean isPublicRequest = (Boolean) context.get(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST);
			if (isPublicRequest != null && isPublicRequest) {
				setRequesterAsCurrentUser(requester);
			}
		}
		return false;
	}
	
	private void setRequesterAsCurrentUser(User requester) throws Exception {
		Account acct = new Account(AccountUtil.getCurrentOrg(), AccountUtil.getUserBean().getUser(requester.getId()));
		AccountUtil.setCurrentAccount(acct);
	}
}
