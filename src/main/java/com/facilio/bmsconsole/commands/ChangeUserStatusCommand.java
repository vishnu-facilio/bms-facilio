package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;

public class ChangeUserStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table("ORG_Users").fields(AccountConstants.getAppOrgUserFields())
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
