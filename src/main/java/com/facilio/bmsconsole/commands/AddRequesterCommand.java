package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddRequesterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		User requester = (User) context.get(FacilioConstants.ContextNames.REQUESTER);
		if (requester != null && requester.getEmail() != null) {
			requester.setId(AccountUtil.getUserBean().addRequester(AccountUtil.getCurrentOrg().getOrgId(), requester));
		}
		return false;
	}
}
