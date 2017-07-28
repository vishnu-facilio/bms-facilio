package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class BaseSpaceAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String baseSpaceList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain getAllSpace = FacilioChainFactory.getAllAreaChain();
		getAllSpace.execute(context);
		
		setModuleName("Base Space");
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() 
	{
		return moduleName;
	}
	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() 
	{
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) 
	{
		this.actionForm = actionForm;
	}
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() 
	{
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) 
	{
		this.customFieldNames = customFieldNames;
	}
	
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() 
	{
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) 
	{
		this.basespaces = basespaces;
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<BaseSpaceContext> getRecords() 
	{
		return basespaces;
	}
}