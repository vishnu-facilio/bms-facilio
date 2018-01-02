package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ChangeTeamStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Group group = (Group) context.get(FacilioConstants.ContextNames.GROUP);
		if (group != null) {
			AccountUtil.getGroupBean().changeGroupStatus(group.getGroupId(), group);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
	
}