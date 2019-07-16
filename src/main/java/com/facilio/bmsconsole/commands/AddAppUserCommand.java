package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppUser;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class AddAppUserCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AppUser user = (AppUser)context.get("user");
		List<FacilioField> fields = AccountConstants.getAppUserFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getAppUserModule().getTableName())
				.fields(fields);

		Map<String, Object> props = FieldUtil.getAsProperties(user);

		insertBuilder.addRecord(props);
		insertBuilder.save();
		return false;
	}

}
