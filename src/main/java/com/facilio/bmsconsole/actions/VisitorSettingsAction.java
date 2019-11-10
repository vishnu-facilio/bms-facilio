package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;

public class VisitorSettingsAction extends FacilioAction 
{
	private static final long serialVersionUID = 1L;
	
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

	public String addVisitorType() throws Exception	
	{
		FacilioChain chain=TransactionChainFactory.getAddVisitorTypeSettingChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.VISITOR_TYPE_PICKLIST_OPTION, getVisitorType());
		chain.execute();
		return SUCCESS;
	}
	
	public String fetchVisitorSettings() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFetchVisitorTypeSettingChain();
		FacilioContext context=chain.getContext();
		context.put(ContextNames.VISITOR_TYPE_PICKLIST_OPTION, getVisitorType());
		chain.execute();
		setResult(ContextNames.VISITOR_SETTINGS, context.get(ContextNames.VISITOR_SETTINGS));
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
