package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		
		if (user != null) {
			List<FacilioField> fields = new ArrayList<>();
			fields.add(AccountConstants.getOrgUserDeletedTimeField());
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table("ORG_Users").fields(fields)
					.andCustomWhere("ORG_USERID = ? AND USERID = ?", user.getOuid(), user.getUid());
			Map<String, Object> props = FieldUtil.getAsProperties(user);
			props.put("deletedTime", System.currentTimeMillis());
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