 package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class LoadEmailSettingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = FieldFactory.getEmailSettingFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.connection(((FacilioContext) context).getConnectionWithTransaction())
				.table("EmailSettings")
				.select(fields)
				.where("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
		List<Map<String, Object>> settings = builder.get();
		
		if(settings != null && settings.size() > 0) {
			Map<String, Object> props = settings.get(0);
			EmailSettingContext setting = new EmailSettingContext();
			BeanUtils.populate(setting, props);
			context.put(FacilioConstants.ContextNames.EMAIL_SETTING, setting);
		}
		
		return false;
	}

}
