package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.UserType;
import com.facilio.fw.OrgInfo;

public class AddRequesterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		UserContext requester = (UserContext) context.get(FacilioConstants.ContextNames.REQUESTER);
		if(requester != null && requester.getEmail() != null) 
		{
			UserContext requester1 = UserAPI.getRequester(requester.getEmail(), OrgInfo.getCurrentOrgInfo().getOrgid());
			if(requester1 == null)
			{
				requester.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
				UserAPI.addRequester(requester);
			}
			else
			{
				requester.setUserId(requester1.getUserId());
				requester.setOrgUserId(requester1.getOrgUserId());
				
				if(!FacilioConstants.UserType.REQUESTER.isUser(requester1.getUserType())) {
					UserAPI.updateUserType(requester1, UserType.REQUESTER);
				}
			}
		}
		return false;
	}
}
