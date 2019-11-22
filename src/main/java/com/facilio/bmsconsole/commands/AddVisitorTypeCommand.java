package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
		
		VisitorTypeContext newVisitorType=(VisitorTypeContext)context.get(ContextNames.VISITOR_TYPE_PICKLIST_OPTION);
		newVisitorType.setEnabled(true);
		
		FacilioChain addPicklistOptionChain=TransactionChainFactory.getAddVisitorTypePicklistOption();
		FacilioContext picklistContext=addPicklistOptionChain.getContext();
		picklistContext.put(FacilioConstants.ContextNames.RECORD,newVisitorType);
		addPicklistOptionChain.execute();
		long visitorTypeId=(Long)picklistContext.get(FacilioConstants.ContextNames.RECORD_ID);
		newVisitorType.setId(visitorTypeId);
		
		
		
	
		
		
		
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule visitorLoggingModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
		FacilioForm visitorLogFormTemplate=FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_VISITOR_LOG_FORM_NAME,visitorLoggingModule);
		FacilioForm visitorInviteFormTemplate=FormsAPI.getFormFromDB(FacilioConstants.ContextNames.DEFAULT_VISITOR_INVITE_FORM_NAME,visitorLoggingModule);
		
		visitorLogFormTemplate.setId(-1);
		visitorLogFormTemplate.setName(newVisitorType.getName()+"_"+newVisitorType.getId());
		visitorLogFormTemplate.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_visitor_log_form");
		visitorLogFormTemplate.setFormType(FormType.WEB);
		
		

		visitorInviteFormTemplate.setId(-1);
		visitorInviteFormTemplate.setName(newVisitorType.getName()+"_"+newVisitorType.getId());
		visitorInviteFormTemplate.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_visitor_invite_form");
		visitorInviteFormTemplate.setFormType(FormType.WEB);
		
		
		FacilioChain addVisitorLogFormChain=TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorLogFormContext=addVisitorLogFormChain.getContext();
		visitorLogFormContext.put(ContextNames.MODULE_NAME, ContextNames.VISITOR_LOGGING);			
		visitorLogFormContext.put(FacilioConstants.ContextNames.FORM, visitorLogFormTemplate);
		addVisitorLogFormChain.execute();
		
		FacilioChain addVisitorInviteFormChain=TransactionChainFactory.getAddFormCommand();
		FacilioContext visitorInviteFormContext=addVisitorInviteFormChain.getContext();
		visitorInviteFormContext.put(ContextNames.MODULE_NAME, ContextNames.VISITOR_LOGGING);			
		visitorInviteFormContext.put(FacilioConstants.ContextNames.FORM, visitorInviteFormTemplate);
		addVisitorInviteFormChain.execute();
		
		
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
		return false;
		
	}

}
