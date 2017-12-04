package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.EmailSettingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateEmailSettingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EmailSettingContext emailSetting = (EmailSettingContext) context.get(FacilioConstants.ContextNames.EMAIL_SETTING);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		Map<String, Object> emailSettingProps = mapper.convertValue(emailSetting, Map.class);
		System.out.println(emailSettingProps);
		
		List<FacilioField> fields = FieldFactory.getEmailSettingFields();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
												.connection(((FacilioContext)context).getConnectionWithTransaction())
												.table("EmailSettings")
												.fields(fields)
												.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
		builder.update(emailSettingProps);
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
