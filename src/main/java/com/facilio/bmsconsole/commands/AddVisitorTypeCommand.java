package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddVisitorTypeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// Add Picklist entry for visitor and insert that ID Into table
		
		VisitorTypeContext newVisitorType=(VisitorTypeContext)context.get(ContextNames.VISITOR_TYPE_PICKLIST_OPTION);
		
		FacilioChain addPicklistOptionChain=TransactionChainFactory.getAddVisitorTypePicklistOption();
		FacilioContext picklistContext=addPicklistOptionChain.getContext();
		picklistContext.put(FacilioConstants.ContextNames.RECORD,newVisitorType);
		addPicklistOptionChain.execute();
		long visitorTypeId=(Long)picklistContext.get(FacilioConstants.ContextNames.RECORD_ID);
		newVisitorType.setId(visitorTypeId);
		
		
		
		FacilioChain addFormChain=TransactionChainFactory.getAddFormCommand();
		FacilioContext formContext=addFormChain.getContext();
		formContext.put(ContextNames.MODULE_NAME, ContextNames.VISITOR);
		FacilioForm newVisitorTypeForm=new FacilioForm();//copy guest form
		List<FormField> newFormfields=FormFactory.getGuestForm().getFields();
		newVisitorTypeForm.setFields(newFormfields);
		newVisitorTypeForm.setName(newVisitorType.getName()+"_"+newVisitorType.getId());
		newVisitorTypeForm.setDisplayName(newVisitorType.getName()+"_"+newVisitorType.getId()+"_visitor_mgmt_form");
		newVisitorTypeForm.setFormType(FormType.WEB);
		
		formContext.put(FacilioConstants.ContextNames.FORM, newVisitorTypeForm);
		addFormChain.execute();
		
		
		FacilioModule visitorSettingsModule=ModuleFactory.getVisitorSettingsModule();
		List<FacilioField> visitorSettingsFields=FieldFactory.getVisitorSettingsFields();
		GenericInsertRecordBuilder insertVisitorSettingsBuilder=new GenericInsertRecordBuilder();
		insertVisitorSettingsBuilder.table(visitorSettingsModule.getTableName()).fields(visitorSettingsFields);		
		Map<String, Object> props =new HashMap<String, Object>();
		props.put("visitorTypeId", visitorTypeId);		
		props.put("visitorFormId",newVisitorTypeForm.getId());
		insertVisitorSettingsBuilder.addRecord(props);
		insertVisitorSettingsBuilder.save();
		return false;
		
	}

}
