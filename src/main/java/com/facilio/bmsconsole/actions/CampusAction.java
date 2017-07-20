package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class CampusAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String campusList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain getAllCampus = FacilioChainFactory.getAllCampusChain();
		getAllCampus.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setCampuses((List<CampusContext>) context.get(FacilioConstants.ContextNames.CAMPUS_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain newCampus = FacilioChainFactory.getNewCampusChain();
		newCampus.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		customFieldNames = new ArrayList<>();
		List<String> defaultFields = Arrays.asList(CampusContext.DEFAULT_CAMPUS_FIELDS);
		for(FacilioField field : fields) 
		{
			if(!defaultFields.contains(field.getName())) 
			{
				customFieldNames.add(field.getName());
			}
		}
		Map mp = ActionContext.getContext().getParameters();
		String isajax = ((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		if(isajax!=null && isajax.equals("true"))
		{
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	public String addCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CAMPUS, campus);
		
		Chain addCampus = FacilioChainFactory.getAddCampusChain();
		addCampus.execute(context);
		
		setCampusId(campus.getCampusId());
		
		return SUCCESS;
	}
	
	public String viewCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CAMPUS_ID, getCampusId());
		
		Chain getCampusChain = FacilioChainFactory.getCampusDetailsChain();
		getCampusChain.execute(context);
		
		setCampus((CampusContext) context.get(FacilioConstants.ContextNames.CAMPUS));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewCampusLayout();
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
	
	private CampusContext campus;
	public CampusContext getCampus() 
	{
		return campus;
	}
	public void setCampus(CampusContext campus) 
	{
		this.campus = campus;
	}
	
	private long campusId;
	public long getCampusId() 
	{
		return campusId;
	}
	public void setCampusId(long campusId) 
	{
		this.campusId = campusId;
	}
	
	private List<CampusContext> campuses;
	public List<CampusContext> getCampuses() 
	{
		return campuses;
	}
	public void setCampuses(List<CampusContext> campuses) 
	{
		this.campuses = campuses;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.CAMPUS;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewCampusLayout();
	}
	
	public String getViewName()
	{
		return "All Campus";
	}
	
	public List<CampusContext> getRecords() 
	{
		return campuses;
	}
}