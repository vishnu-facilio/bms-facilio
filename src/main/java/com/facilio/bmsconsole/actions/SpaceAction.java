package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class SpaceAction extends ActionSupport {
	
	@SuppressWarnings("unchecked")
	public String spaceList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		if (getSiteId() > 0) {
			context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		}
		if (getBuildingId() > 0) {
			context.put(FacilioConstants.ContextNames.BUILDING_ID, getBuildingId());
		}
		if (getFloorId() > 0) {
			context.put(FacilioConstants.ContextNames.FLOOR_ID, getFloorId());
		}
		if (getCategoryId() > 0) {
			context.put(FacilioConstants.ContextNames.SPACE_CATEGORY, getCategoryId());
		}
		
		Chain getAllSpace = FacilioChainFactory.getAllSpaceChain();
		getAllSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setSpaces((List<SpaceContext>) context.get(FacilioConstants.ContextNames.SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String independentSpaceList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SITE_ID, getSiteId());
		
		Chain getIndependentSpace = FacilioChainFactory.getIndependentSpaceChain();
		getIndependentSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setSpaces((List<SpaceContext>) context.get(FacilioConstants.ContextNames.SPACE_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain newSpace = FacilioChainFactory.getNewSpaceChain();
		newSpace.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map mp = ActionContext.getContext().getParameters();
		String isajax = ((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		if(isajax!=null && isajax.equals("true"))
		{
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public String addSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE, space);
		
		Chain addSpace = FacilioChainFactory.getAddSpaceChain();
		addSpace.execute(context);
		
		setSpaceId(space.getId());
		
		return SUCCESS;
	}
	
	public String viewSpace() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getSpaceId());
		
		Chain getSpaceChain = FacilioChainFactory.getSpaceDetailsChain();
		getSpaceChain.execute(context);
		
		setSpace((SpaceContext) context.get(FacilioConstants.ContextNames.SPACE));
		
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewSpaceLayout(fields);
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
	
	private SpaceContext space;
	public SpaceContext getSpace() 
	{
		return space;
	}
	public void setSpace(SpaceContext space) 
	{
		this.space = space;
	}
	
	private long spaceId;
	public long getSpaceId() 
	{
		return spaceId;
	}
	public void setSpaceId(long spaceId) 
	{
		this.spaceId = spaceId;
	}
	
	private long siteId = -1;
	public long getSiteId() 
	{
		return siteId;
	}
	public void setSiteId(long siteId) 
	{
		this.siteId = siteId;
	}
	
	private long buildingId = -1;
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}
	
	private long floorId = -1;
	public long getFloorId() 
	{
		return floorId;
	}
	public void setFloorId(long floorId) 
	{
		this.floorId = floorId;
	}
	
	private long categoryId = -1;
	public long getCategoryId() 
	{
		return categoryId;
	}
	public void setCategoryId(long categoryId) 
	{
		this.categoryId = categoryId;
	}
	
	private List<SpaceContext> spaces;
	public List<SpaceContext> getSpaces() 
	{
		return spaces;
	}
	public void setSpaces(List<SpaceContext> spaces) 
	{
		this.spaces = spaces;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.SPACE;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewSpaceLayout();
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<SpaceContext> getRecords() 
	{
		return spaces;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummarySpaceLayout();
	}
	
	public SpaceContext getRecord() 
	{
		return space;
	}
}