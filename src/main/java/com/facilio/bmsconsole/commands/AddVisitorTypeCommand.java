package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.context.VisitorTypeFormsContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddVisitorTypeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// Add Picklist entry for visitor and insert that ID Into settings table
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		VisitorTypeContext newVisitorType=(VisitorTypeContext)context.get(ContextNames.VISITOR_TYPE_PICKLIST_OPTION);
		newVisitorType.setEnabled(true);
		
		FacilioChain addPicklistOptionChain=TransactionChainFactory.getAddVisitorTypePicklistOption();
		FacilioContext picklistContext=addPicklistOptionChain.getContext();
		picklistContext.put(FacilioConstants.ContextNames.RECORD,newVisitorType);
		addPicklistOptionChain.execute();
		long visitorTypeId=(Long)picklistContext.get(FacilioConstants.ContextNames.RECORD_ID);
		newVisitorType.setId(visitorTypeId);

		FacilioForm visitorLogFormTemplate=FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_VISITOR_LOG_CHECKIN_FORM_NAME,modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG));
		FacilioForm visitorInviteFormTemplate=FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_INVITE_VISITOR_FORM_NAME,modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR));
		
		visitorLogFormTemplate.setId(-1);
		visitorLogFormTemplate.setName(newVisitorType.getName()+"_"+newVisitorType.getId()+"visitor_log_checkin_form");
		visitorLogFormTemplate.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_visitor_log_checkin_form");
		visitorLogFormTemplate.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		visitorInviteFormTemplate.setId(-1);
		visitorInviteFormTemplate.setName(newVisitorType.getName()+"_"+newVisitorType.getId()+"invite_visitor_form");
		visitorInviteFormTemplate.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_invite_visitor_form");
		visitorInviteFormTemplate.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		
		FormField visitorLogModuleHostField=visitorLogFormTemplate.getFieldsMap().get("host");
		FormField inviteModuleHostField=visitorInviteFormTemplate.getFieldsMap().get("host");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("filterValue", 6); //default employee
		jsonObject.put("isFiltersEnabled", true);
		visitorLogModuleHostField.setConfig(jsonObject);
		inviteModuleHostField.setConfig(jsonObject);
		
		FacilioChain addVisitorLogFormChain=TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorLogFormContext=addVisitorLogFormChain.getContext();
		visitorLogFormContext.put(ContextNames.MODULE_NAME, ContextNames.VISITOR_LOG);			
		visitorLogFormContext.put(FacilioConstants.ContextNames.FORM, visitorLogFormTemplate);
		addVisitorLogFormChain.execute();
		
		FacilioChain addVisitorInviteFormChain=TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorInviteFormContext=addVisitorInviteFormChain.getContext();
		visitorInviteFormContext.put(ContextNames.MODULE_NAME, ContextNames.INVITE_VISITOR);			
		visitorInviteFormContext.put(FacilioConstants.ContextNames.FORM, visitorInviteFormTemplate);
		addVisitorInviteFormChain.execute();
		
		FacilioForm visitorInviteFormTemplateForOccupant=FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_INVITE_VISITOR_FORM_NAME,modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR));
		visitorInviteFormTemplateForOccupant.setId(-1);
		visitorInviteFormTemplateForOccupant.setName(newVisitorType.getName()+"_"+newVisitorType.getId()+"invite_visitor_form_Occupant");
		visitorInviteFormTemplateForOccupant.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_invite_visitor_form");
		visitorInviteFormTemplateForOccupant.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		
		FacilioChain addVisitorInviteFormOccupantChain = TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorInviteFormOccupantContext = addVisitorInviteFormOccupantChain.getContext();
		visitorInviteFormOccupantContext.put(ContextNames.MODULE_NAME, ContextNames.INVITE_VISITOR);			
		visitorInviteFormOccupantContext.put(FacilioConstants.ContextNames.FORM, visitorInviteFormTemplateForOccupant);
		addVisitorInviteFormOccupantChain.execute();
		
		FacilioForm visitorInviteFormTemplateForTenant = FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_INVITE_VISITOR_FORM_NAME,modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR));
		visitorInviteFormTemplateForTenant.setId(-1);
		visitorInviteFormTemplateForTenant.setName(newVisitorType.getName()+"_"+newVisitorType.getId()+"invite_visitor_form_Tenant");
		visitorInviteFormTemplateForTenant.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_invite_visitor_form");
		visitorInviteFormTemplateForTenant.setAppLinkName(ApplicationLinkNames.TENANT_PORTAL_APP);
		
		FacilioChain addVisitorInviteFormTenantChain = TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorInviteFormTenantContext = addVisitorInviteFormTenantChain.getContext();
		visitorInviteFormTenantContext.put(ContextNames.MODULE_NAME, ContextNames.INVITE_VISITOR);			
		visitorInviteFormTenantContext.put(FacilioConstants.ContextNames.FORM, visitorInviteFormTemplateForTenant);
		addVisitorInviteFormTenantChain.execute();
		
		FacilioModule visitorSettingsModule=ModuleFactory.getVisitorSettingsModule();
		List<FacilioField> visitorSettingsFields=FieldFactory.getVisitorSettingsFields();
		GenericInsertRecordBuilder insertVisitorSettingsBuilder=new GenericInsertRecordBuilder();
		insertVisitorSettingsBuilder.table(visitorSettingsModule.getTableName()).fields(visitorSettingsFields);		
		
		VisitorSettingsContext visitorSettingsDefault=new VisitorSettingsContext();
		visitorSettingsDefault.setVisitorTypeId(visitorTypeId);
		
		visitorSettingsDefault.setVisitorLogFormId(visitorLogFormTemplate.getId());
		visitorSettingsDefault.setVisitorInviteFormId(visitorInviteFormTemplate.getId());
		
		visitorSettingsDefault.setBadgeEnabled(true);
		visitorSettingsDefault.setHostEnabled(true);
		visitorSettingsDefault.setNdaEnabled(true);
		visitorSettingsDefault.setPhotoEnabled(true);
		visitorSettingsDefault.setApprovalRequiredForInvite(false);
		
		Map<String, Object> props =FieldUtil.getAsProperties(visitorSettingsDefault);
		

		insertVisitorSettingsBuilder.addRecord(props);
		insertVisitorSettingsBuilder.save();
		
		List<Map<String, Object>> visitorTypeFormProps = new ArrayList<Map<String,Object>>();
		
		FacilioModule visitorTypeFormsModule=ModuleFactory.getVisitorTypeFormsModule();
		List<FacilioField> visitorTypeFormsFields=FieldFactory.getVisitorTypeFormsFields();
		GenericInsertRecordBuilder insertVisitorTypeFormsBuilder=new GenericInsertRecordBuilder();
		insertVisitorTypeFormsBuilder.table(visitorTypeFormsModule.getTableName()).fields(visitorTypeFormsFields);	
		
		VisitorTypeFormsContext visitorTypeForm = new VisitorTypeFormsContext();
		visitorTypeForm.setVisitorTypeId(visitorTypeId);
		visitorTypeForm.setVisitorLogFormId(visitorLogFormTemplate.getId());
		visitorTypeForm.setVisitorInviteFormId(visitorInviteFormTemplate.getId());
		long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		visitorTypeForm.setAppId(appId);
		visitorTypeFormProps.add(FieldUtil.getAsProperties(visitorTypeForm));
		
		visitorTypeForm = new VisitorTypeFormsContext();
		visitorTypeForm.setVisitorTypeId(visitorTypeId);
		visitorTypeForm.setVisitorInviteFormId(visitorInviteFormTemplateForOccupant.getId());
		appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		visitorTypeForm.setAppId(appId);
		visitorTypeFormProps.add(FieldUtil.getAsProperties(visitorTypeForm));
		
		visitorTypeForm = new VisitorTypeFormsContext();
		visitorTypeForm.setVisitorTypeId(visitorTypeId);
		visitorTypeForm.setVisitorInviteFormId(visitorInviteFormTemplateForTenant.getId());
		appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
		visitorTypeForm.setAppId(appId);
		visitorTypeFormProps.add(FieldUtil.getAsProperties(visitorTypeForm));
		
		insertVisitorTypeFormsBuilder.addRecords(visitorTypeFormProps);
		insertVisitorTypeFormsBuilder.save();
		
		return false;
		
	}

}
