package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ChangeUserStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			FieldFactory.getOrgUserFields();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table("ORG_Users").fields(AccountConstants.getOrgUserFields())
					.andCustomWhere("ORG_USERID = ? AND USERID = ?", user.getOuid(), user.getUid());
			Map<String, Object> props = FieldUtil.getAsProperties(user);
			updateBuilder.update(props);
			context.put(FacilioConstants.ContextNames.RESULT, "success");
			//AccountUtil.getUserBean().updateUser(user.getOuid(), user);
		}
		else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
		return false;
	}
}
