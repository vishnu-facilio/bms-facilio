package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
//import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerSettingsContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddControllerCommand implements Command {
	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerSettingsContext controllerSettings = (ControllerSettingsContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		
		if(controllerSettings != null) {
			controllerSettings.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
//			
////			CommonCommandUtil.setFwdMail(supportEmail);
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.setSerializationInclusion(Include.NON_DEFAULT);
//			Map<String, Object> controllerSettingsprops = mapper.convertValue(controllerSettings, Map.class);
////			emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
////			if(emailProps.get("autoAssignGroup")!=null)
////			{
////						emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
////			}
////			System.out.println(emailProps);
			
			Map<String, Object> controllerSettingsprops = FieldUtil.getAsProperties(controllerSettings);
			
			List<FacilioField> fields = FieldFactory.getControllerFields();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table("Controller")
													.fields(fields)
													.addRecord(controllerSettingsprops);
			builder.save();
			controllerSettings.setId((long) controllerSettingsprops.get("id"));
		}
		return false;
	}
}
