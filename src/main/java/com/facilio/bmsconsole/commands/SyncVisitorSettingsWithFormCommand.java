package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.fields.FacilioField;

public class SyncVisitorSettingsWithFormCommand extends FacilioCommand{
	private static final Logger LOGGER = LogManager.getLogger(GetPMCalendarResouceJobsCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorSettingsContext visitorSettingsContext=(VisitorSettingsContext)context.get(ContextNames.VISITOR_SETTINGS);
		//sync HOST FIELD , NDA field,PHOTO field with visitor settings
		
		FacilioForm logForm=visitorSettingsContext.getVisitorLogForm();
		FacilioForm inviteForm=visitorSettingsContext.getVisitorInviteForm();
		
		FormField loggingModuleHostField=logForm.getFieldsMap().get("host");
		FormField inviteModuleHostField=inviteForm.getFieldsMap().get("host");
		
		FormField ndaField=logForm.getFieldsMap().get("nda");
		FormField avatarField=logForm.getFieldsMap().get("avatar");
		
		JSONObject hostSetting = visitorSettingsContext.getHostSettings();
		if(hostSetting != null && hostSetting.get("hostType") != null) {
			if(hostSetting.get("hostType").equals("3")) {
				loggingModuleHostField.setLookupModuleName(FacilioConstants.ContextNames.EMPLOYEE);
				inviteModuleHostField.setLookupModuleName(FacilioConstants.ContextNames.EMPLOYEE);
			}
			else if(hostSetting.get("hostType").equals("1")) {
				loggingModuleHostField.setLookupModuleName(FacilioConstants.ContextNames.TENANT_CONTACT);
				inviteModuleHostField.setLookupModuleName(FacilioConstants.ContextNames.TENANT_CONTACT);
			}
		}
		
		//these fields A MUST IN VISITOR FORM
		loggingModuleHostField.setHideField(!visitorSettingsContext.getHostEnabled());
		loggingModuleHostField.setRequired((Boolean)visitorSettingsContext.getHostSettings().get("required"));
		
		inviteModuleHostField.setHideField(!visitorSettingsContext.getHostEnabled());
		loggingModuleHostField.setRequired((Boolean)visitorSettingsContext.getHostSettings().get("required"));
		
		ndaField.setHideField(!visitorSettingsContext.getNdaEnabled());
		avatarField.setHideField(!visitorSettingsContext.getPhotoEnabled());
		
		FacilioChain loggingHostFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		loggingHostFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, loggingModuleHostField);
		loggingHostFieldUpdateChain.execute();
		
		FacilioChain inviteHostFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		inviteHostFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, inviteModuleHostField);
		inviteHostFieldUpdateChain.execute();
		
		FacilioChain avatarFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		avatarFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, avatarField);
		avatarFieldUpdateChain.execute();
		
		FacilioChain ndaFieldUpdateChain = TransactionChainFactory.getUpdateFormFieldChain();
		ndaFieldUpdateChain.getContext().put(ContextNames.FORM_FIELD, ndaField);
		ndaFieldUpdateChain.execute();

		
		return false;
	}

}
