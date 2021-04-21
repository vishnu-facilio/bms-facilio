package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;

public class VisitorSettingsAction extends FacilioAction 
{
	private static final long serialVersionUID = 1L;
	
	Boolean fetchAll;
	VisitorTypeContext visitorType;
	VisitorSettingsContext visitorSettings;

	public VisitorSettingsContext getVisitorSettings() {
		return visitorSettings;
	}

	public void setVisitorSettings(VisitorSettingsContext visitorSettings) {
		this.visitorSettings = visitorSettings;
	}

	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	 
	public Boolean getFetchAll() {
		return fetchAll;
	}

	public void setFetchAll(Boolean fetchAll) {
		this.fetchAll = fetchAll;
	}
	
	private List<FacilioForm> forms;
	
	public void setForms(List<FacilioForm> forms) {
		this.forms = forms;
	}
	
	public List<FacilioForm> getForms() {
		return this.forms;
	}
	public String addVisitorType() throws Exception	
	{
		FacilioChain chain=TransactionChainFactory.getAddVisitorTypeSettingChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.VISITOR_TYPE_PICKLIST_OPTION, getVisitorType());
		chain.execute();
		return SUCCESS;
	}
	
	//duplicating initForms from FormAction.Java
	public String updateFormListFields() throws Exception{
	
	FacilioChain c = TransactionChainFactory.getUpdateVisitorFormsChain();
	c.getContext().put(ContextNames.FORMS_LIST, this.getForms());
	c.execute();
	setResult(ContextNames.FORMS_RESPONSE_LIST, c.getContext().get(ContextNames.FORMS_RESPONSE_LIST));
	return SUCCESS;
	}
	
	
	
	public String fetchVisitorSettings() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFetchVisitorTypeSettingChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.VISITOR_TYPE_PICKLIST_OPTION, getVisitorType());
		context.put(ContextNames.FETCH_ALL, getFetchAll());
		chain.execute();
		setResult(ContextNames.VISITOR_SETTINGS, context.get(ContextNames.VISITOR_SETTINGS));
		setResult(ContextNames.FORMS_LIST, context.get(ContextNames.FORMS_LIST));
		return SUCCESS;
	}
	
	public String updateVisitorSettings() throws Exception{
		FacilioChain chain=TransactionChainFactory.getUpdateVisitorTypeSettingChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.VISITOR_SETTINGS, getVisitorSettings());
		chain.execute();
		//setResult(ContextNames.VISITOR_SETTINGS, context.get(ContextNames.VISITOR_SETTINGS));
		return SUCCESS;
	}
}
