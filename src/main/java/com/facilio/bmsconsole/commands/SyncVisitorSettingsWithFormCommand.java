package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants.ContextNames;

public class SyncVisitorSettingsWithFormCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetPMCalendarResouceJobsCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorSettingsContext visitorSettingsContext=(VisitorSettingsContext)context.get(ContextNames.VISITOR_SETTINGS);
		//sync HOST FIELD , NDA field,PHOTO field with visitor settings

		FacilioForm logForm=visitorSettingsContext.getVisitorLogForm();
		FacilioForm inviteForm=visitorSettingsContext.getVisitorInviteForm();
		
		FormField loggingModuleHostField=logForm.getFieldsMap().get("host");
		
		FormField ndaField=logForm.getFieldsMap().get("nda");
		FormField avatarField=logForm.getFieldsMap().get("avatar");
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isFiltersEnabled", true);
		
		JSONObject hostSetting = visitorSettingsContext.getHostSettings();
		if(hostSetting != null && hostSetting.get("hostType") != null) {
			if(hostSetting.get("hostType").equals("3")) {
				jsonObject.put("filterValue", 6); //default employee
			}
			else if(hostSetting.get("hostType").equals("1")) {
				jsonObject.put("filterValue", 3); //tenant contact
			}
		}
		
		loggingModuleHostField.setConfig(jsonObject);
		
		//these fields A MUST IN VISITOR FORM
		loggingModuleHostField.setHideField(!visitorSettingsContext.getHostEnabled());
		loggingModuleHostField.setRequired((Boolean)visitorSettingsContext.getHostSettings().get("required"));

		loggingModuleHostField.setRequired((Boolean)visitorSettingsContext.getHostSettings().get("required"));
		
		ndaField.setHideField(!visitorSettingsContext.getNdaEnabled());
		avatarField.setHideField(!visitorSettingsContext.getPhotoEnabled());
		
		FacilioChain loggingHostFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		loggingHostFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, loggingModuleHostField);
		loggingHostFieldUpdateChain.execute();

		FacilioChain avatarFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		avatarFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, avatarField);
		avatarFieldUpdateChain.execute();
		
		FacilioChain ndaFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		ndaFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, ndaField);
		ndaFieldUpdateChain.execute();

		
		return false;
	}

}
