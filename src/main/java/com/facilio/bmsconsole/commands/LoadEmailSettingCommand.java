 package com.facilio.bmsconsole.commands;

 import java.util.List;
import java.util.Map;

 import com.facilio.command.FacilioCommand;
 import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class LoadEmailSettingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = FieldFactory.getEmailSettingFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table("EmailSettings")
				.select(fields)
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
		List<Map<String, Object>> settings = builder.get();
		
		if(settings != null && settings.size() > 0) {
			Map<String, Object> props = settings.get(0);
			EmailSettingContext setting = FieldUtil.getAsBeanFromMap(props, EmailSettingContext.class);
			context.put(FacilioConstants.ContextNames.EMAIL_SETTING, setting);
		}
		
		return false;
	}

}
