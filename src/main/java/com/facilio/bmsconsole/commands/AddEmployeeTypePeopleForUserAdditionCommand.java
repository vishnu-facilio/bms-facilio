package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class AddEmployeeTypePeopleForUserAdditionCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(user != null) {
			PeopleAPI.addPeopleForUser(user);
		}
		return false;
	}

}
